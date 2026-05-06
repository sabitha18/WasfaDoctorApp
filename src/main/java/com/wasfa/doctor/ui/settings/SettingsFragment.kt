package com.wasfa.doctor.ui.settings

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentSettingsBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.ProfileResponse
import com.wasfa.doctor.ui.main.DoctorHomeActivity
import com.wasfa.doctor.ui.login.LoginActivity
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.Calendar

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private val REQUEST_STORAGE_PERMISSION = 1001
    private val REQUEST_IMAGE_PICK = 1002
    private val UCROP_REQUEST_CODE = 1003
    private var originalProfile: ProfileResponse? = null
    var imageFile: File? = null
    var password = ""
    private lateinit var dialog: BottomSheetDialog
    private lateinit var cardCancel: MaterialCardView
    private lateinit var cardLogout: MaterialCardView
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            startCrop(it)
        }
    }

    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                imageFile = uriToFile(requireContext(), resultUri)
                Glide.with(this)
                    .load(resultUri)
                    .into(binding.imgProfile)
                binding.cardSave.isEnabled = true
                binding.cardSave.alpha = 1.0f

            } else {
                Toast.makeText(requireContext(), "Crop failed", Toast.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            Toast.makeText(requireContext(), "Crop error: ${cropError?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setInputListeners()
        handleClick()
        if (!isTablet()){
            (activity as? DoctorHomeActivity)?.showBottomNav()
        }else{
            (activity as? DoctorHomeActivity)?.hideBottomNav()
        }
    }

    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    private fun showLogoutPopup() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.sheet_log_out, null)
        dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)
        cardCancel = dialogView.findViewById(R.id.card_cancel)
        cardLogout = dialogView.findViewById(R.id.card_yes)
        cardLogout.setOnClickListener {
            dialog.dismiss()
            var email = ""
            var pass = ""
            var rememberStatus = ""
            if (appPreferences?.getRememberStatus().toString() == "true"){
                email = appPreferences.getEmail().toString()
                pass =  appPreferences.getPass().toString()
                rememberStatus = "true"
            }
            AppPreferences.getInstance(requireContext()).clearAllPreferences()

            if (rememberStatus == "true"){
                appPreferences.saveEmail(email)
                appPreferences.savePass(pass)
                appPreferences.saveRememberStatus("true")
            }
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        cardCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun handleClick() {
        binding.imgMenu.setOnClickListener {
            (activity as? DoctorHomeActivity)?.toggleSideMenu()
        }
        binding.lytLogOut.setOnClickListener {

            showLogoutPopup()
        }
        binding.cardReset.setOnClickListener{
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getProfile(AppPreferences.getInstance(requireContext()).getToken().toString())
        }
        binding.cardDob.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    binding.txtDob.text = selectedDate
                    checkInputs()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }
        binding.rltImage.setOnClickListener {
           checkStoragePermission()
        }
        binding.cardSave.setOnClickListener {
            val appPreferences = AppPreferences.getInstance(requireContext())
            val request = ApiService.UpdateProfileRequest(
                phone = binding.editPhome.text.toString(),
                name = binding.editName.text.toString(),
                email = binding.editEmail.text.toString(),
                dob = binding.txtDob.text.toString(),
                password = "",
                civilId = binding.editCivilId.text.toString(),
                alternateNumber = binding.editAltPhome.text.toString()
            )
            binding.progressBar.visibility = View.VISIBLE
            viewModel.updateProfile(appPreferences.getToken().toString(),request, imageFile)
        }

    }
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_STORAGE_PERMISSION
                )
            } else {
                openGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )
            } else {
                openGallery()
            }
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.png"))
        val uCrop = UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1080, 1080)

        cropImageLauncher.launch(uCrop.getIntent(requireContext()))
    }



    private fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("cropped_image", ".png", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        // No need for the extra renaming logic here as the file is already named correctly
        return tempFile
    }



    private fun setViewModel() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(requireContext())
        ).get(HomeViewModel::class.java)

        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                _binding = null
            }
        })
        viewModel.profileStatus.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),data,Toast.LENGTH_LONG).show()
        }
        viewModel.profileData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            manageData(data)
        }
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getProfile(appPreferences.getToken().toString())
    }

    private fun manageData(data: List<ProfileResponse>?) {
        val profile = data?.getOrNull(0)
        originalProfile = profile

        binding.editName.setText(profile?.name ?: "")
        binding.editEmail.setText(profile?.email ?: "")
        binding.editPhome.setText(profile?.phone ?: "")
        binding.editAltPhome.setText(profile?.alternateNumber ?: "")
        binding.editCivilId.setText(profile?.civilId ?: "")
        binding.txtDob.text = profile?.dob ?: ""

        Glide.with(requireContext())
            .load(profile?.profilePic)
            .into(binding.imgProfile)

        checkInputs()
    }

    private fun setInputListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputs()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.editName.addTextChangedListener(textWatcher)
        binding.editEmail.addTextChangedListener(textWatcher)
        binding.editPhome.addTextChangedListener(textWatcher)
        binding.editAltPhome.addTextChangedListener(textWatcher)
        binding.editCivilId.addTextChangedListener(textWatcher)

      
    }

    private fun checkInputs() {
        val original = originalProfile ?: return

        val isChanged =
            binding.editName.text.toString() != (original.name ?: "") ||
                    binding.editEmail.text.toString() != (original.email ?: "") ||
                    binding.editPhome.text.toString() != (original.phone ?: "") ||
                    binding.editAltPhome.text.toString() != (original.alternateNumber ?: "") ||
                    binding.editCivilId.text.toString() != (original.civilId ?: "") ||
                    binding.txtDob.text.toString() != (original.dob ?: "")

        binding.cardSave.isEnabled = isChanged
        binding.cardSave.alpha = if (isChanged) 1.0f else 0.5f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
