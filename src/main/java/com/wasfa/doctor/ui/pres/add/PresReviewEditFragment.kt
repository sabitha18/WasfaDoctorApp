package com.wasfa.doctor.ui.pres.add

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentPresReviewBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.PatientInfo
import com.wasfa.doctor.network.response.SubmitResponse
import com.wasfa.doctor.ui.main.DoctorHomeActivity
import com.wasfa.doctor.ui.pres.adapter.MedicationListPreviewAdapter
import com.wasfa.doctor.ui.helper.PrescriptionPdfHelper
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.io.File
import com.caverock.androidsvg.SVG
import android.graphics.Canvas
import androidx.navigation.fragment.findNavController
import com.wasfa.doctor.ui.pres.adapter.MedicationListPreviewEditAdapter
import com.wasfa.doctor.ui.report.PrescribedRxFragment
import com.wasfa.doctor.network.response.PresDetails

class PresReviewEditFragment : Fragment() {
    private var _binding: FragmentPresReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    var patientId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPresReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageClick()
        setViewModel()
        if (!isTablet()){
            (activity as? DoctorHomeActivity)?.showBottomNav()
        }else{
            (activity as? DoctorHomeActivity)?.hideBottomNav()
        }
        val prefs = AppPreferences.getInstance(requireContext())


            binding.lytPosRx.visibility = View.GONE
            binding.lytPosRxNew.visibility = View.VISIBLE


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
        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }
        viewModel.submitStatus.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PrescribedRxFragment())
                .addToBackStack(null)
                .commit()

        }
        viewModel.submitSaveStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), DoctorHomeActivity::class.java)
            startActivity(intent)

        }
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }

        viewModel.presRXNewData.observe(viewLifecycleOwner) { data ->
            println("--------------------------")
            binding.progressBar.visibility = View.GONE

            manageCart(data?.prescriptionDetails)
            managePatient(data?.patientInfo)

        }

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.EditPOSDetailsRequest(
            prescriptionId = appPreferences.getNewRXStatus().toString()
        )
        viewModel.getPresRXNewDetailsEdit(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
    }

    private fun generatePrescriptionPdf(data: SubmitResponse?) {
        Thread {
            val logoBitmap = try {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(data?.logo?.replace("\\/", "/"))
                    .submit(100, 80)
                    .get()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            val qrBitmap = svgToBitmap(data?.qrCode.toString())
            val pdfFile = PrescriptionPdfHelper.generatePdf(
                requireContext(),
                data?.prescriptionDetails,
                data?.patientInfo,
                data?.doctorInfo,
                logoBitmap,    // <-- Pass Bitmap here instead of String
                qrBitmap
            )

            pdfFile?.let {
                // Switch back to Main thread to open PDF
                requireActivity().runOnUiThread {
                    openPdf(it)
                }
            }
        }.start()
    }



    private fun svgToBitmap(svgString: String, width: Int = 200, height: Int = 200): Bitmap? {
        return try {
            val svg = SVG.getFromString(svgString)
            svg.setDocumentWidth("200px")
            svg.setDocumentHeight("200px")
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            svg.renderToCanvas(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    private fun openPdf(file: File) {
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            AppPreferences.getInstance(requireContext()).saveKey("true")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }
    private fun managePatient(patientInfo: List<PatientInfo>?) {

        patientId = patientInfo?.get(0)?.id.toString()

        fun safeText(text: String?): String {
            return text ?: ""
        }

        binding.txtPatientName.text = safeText(patientInfo?.getOrNull(0)?.name)
        binding.txtDob.text = safeText(patientInfo?.getOrNull(0)?.dob)
        binding.txtCivilId.text = safeText(patientInfo?.getOrNull(0)?.civil_id)
        binding.txtPhone.text = safeText(patientInfo?.getOrNull(0)?.phone)
        binding.txtGender.text = safeText(patientInfo?.getOrNull(0)?.gender)
        val nationality = patientInfo?.getOrNull(0)?.nationality
        binding.txtNationality.text = if (nationality == "0") "" else safeText(nationality)

        binding.txtGovernorate.text = safeText(patientInfo?.getOrNull(0)?.address?.getOrNull(0)?.governorateName)
        binding.txtArea.text = safeText(patientInfo?.getOrNull(0)?.address?.getOrNull(0)?.areaName)
        binding.txtBlock.text = safeText(patientInfo?.getOrNull(0)?.address?.getOrNull(0)?.block)
        binding.txtStreet.text = safeText(patientInfo?.getOrNull(0)?.address?.getOrNull(0)?.street)
        binding.txtBuilding.text = safeText(patientInfo?.getOrNull(0)?.address?.getOrNull(0)?.building)
        binding.txtFloor.text = safeText(patientInfo?.getOrNull(0)?.address?.getOrNull(0)?.floor)

        if (patientInfo?.getOrNull(0)?.address.isNullOrEmpty()) {
            binding.txtAddressDetails.visibility = View.GONE
            binding.cardAddress.visibility = View.GONE
        } else {
            binding.txtAddressDetails.visibility = View.VISIBLE
            binding.cardAddress.visibility = View.VISIBLE
        }
    }


    private fun manageCart(cartItems: List<PresDetails>?) {

        binding.recyclerMed.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = MedicationListPreviewEditAdapter(cartItems) { data, type ->

            }
            adapter = catAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        val prefs = AppPreferences.getInstance(requireContext())
        if (prefs.getKey() == "true") {
            prefs.saveKey("false")
            val intent = Intent(requireContext(), DoctorHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun manageClick() {
        binding.cardBack.setOnClickListener {
            val navController = runCatching { findNavController() }.getOrNull()
            if (navController?.currentDestination != null) {
                navController.popBackStack()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
        binding.cardSend.setOnClickListener {

            callSubmitRxApi()
        }
        binding.cardSave.setOnClickListener {
            callSubmitSaveRxApi()
        }
        binding.cardSubmitWithCall.setOnClickListener {
            callSubmitRxNewApi("1")

        }
        binding.cardSubmit.setOnClickListener {
            callSubmitRxNewApi("0")

        }
    }
    private fun callSubmitSaveRxApi() {
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.SubmitRXRequest(
            influencerId = "",
            customerId = patientId,
            is_edit = "",
            submitWithCustomerCall = ""
        )
        viewModel.submitSaveRX(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }
    private fun callSubmitRxApi() {
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.SubmitRXRequest(
            influencerId = "",
            customerId = patientId,
            is_edit = "",
            submitWithCustomerCall = ""
        )
        viewModel.submitRX(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }
    private fun callSubmitRxNewApi(status: String) {
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.SubmitRXRequest(
            influencerId = "",
            customerId = patientId,
            is_edit = "1",
            submitWithCustomerCall = status
        )
        viewModel.submitRX(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }

    private fun showPrintPopup() {
        val builder = AlertDialog.Builder(requireContext())

        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.popup_print, null)
        builder.setView(customLayout)
       //val imgClose = customLayout.findViewById<ImageView>(R.id.img_close)
        val cardPrint = customLayout.findViewById<MaterialCardView>(R.id.card_print)
        lateinit var dialog: AlertDialog
        cardPrint.setOnClickListener {
            dialog?.dismiss()
        }
//        imgClose.setOnClickListener {
//            dialog?.dismiss()
//        }

        dialog = builder.create()
        dialog.show()
    }
    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

}