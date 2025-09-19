package com.wasfa.doctor.ui.products.brand

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentEditBrandBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CategoryDetailsResponse
import com.wasfa.doctor.ui.images.ImageListActivity
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class EditBrandActivity : AppCompatActivity() {
    private lateinit var binding: FragmentEditBrandBinding
    private lateinit var viewModel: HomeViewModel
    private var selectedId: String = ""
    private var originalData: CategoryDetailsResponse? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        binding = FragmentEditBrandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleClick()
        setViewModel()
        manageImages()
        setupFormValidation()

        binding.cardSave.alpha = 0.5f
        binding.cardSave.isClickable = false
        binding.cardSave.isFocusable = false
    }
    private fun validateFormAndEnableButton() {
        val allEditTextsFilled = listOf(binding.editName, binding.editArabicName,binding.editMetaTitle)
            .all { it.text.toString().trim().isNotEmpty() }

        val shouldEnable = allEditTextsFilled

        binding.cardSave.alpha = if (shouldEnable) 1.0f else 0.5f
        binding.cardSave.isClickable = shouldEnable
        binding.cardSave.isFocusable = shouldEnable
    }
    private fun setupFormValidation() {
        val editTexts = listOf(binding.editName, binding.editArabicName,binding.editMetaTitle)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateFormAndEnableButton()
            }
        }

        editTexts.forEach { it.addTextChangedListener(textWatcher) }

        // Call initially in case fields are pre-filled
        validateFormAndEnableButton()
    }
    private fun manageImages() {

        if (AppPreferences.getInstance(this).getImgStatus() == "icon") {
            if (AppPreferences.getInstance(this).getCatIconImgId().isNullOrEmpty()) {

            } else {
                binding.txtLogo.text =
                    AppPreferences.getInstance(this).getCatIconImgName()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        manageImages()
    }
    private fun setViewModel() {
        val appPreferences = AppPreferences.getInstance(this)
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(this)
        ).get(HomeViewModel::class.java)

        viewModel.loadingState.observe(this) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }

        viewModel.productEvent.observe(this) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.showAlertEvent.observe(this) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.editBrandFailStatus.observe(this) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this@EditBrandActivity, message, Toast.LENGTH_LONG).show()

        }
        viewModel.editBrandStatus.observe(this) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this@EditBrandActivity, message, Toast.LENGTH_LONG).show()
            val intent = Intent(this, BrandActivity::class.java)
            startActivity(intent)
            finish()
        }
        viewModel.brandDetails.observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            manageData(data)
        }
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.CatDetailsRequest(
            id = appPreferences.getCategoryId().toString()
        )
        viewModel.getBrandDetails(appPreferences.getToken().toString(),request)

    }
    private fun manageData(data: List<CategoryDetailsResponse>?) {
        val firstItem = data?.getOrNull(0) ?: return
        originalData = firstItem

        selectedId = firstItem.id.toString()

        binding.editName.setText(firstItem.name)
        binding.editArabicName.setText(firstItem.arabicName)



        val iconUrl = data?.get(0)?.logo ?: ""

        val appPreferences = AppPreferences.getInstance(this)
        appPreferences.saveCatIconImgId(data?.get(0)?.logoid)

        binding.txtLogo.text = iconUrl.substringAfterLast("/")

        binding.editMetaTitle.setText(firstItem.metaTitle)
        binding.editMetaDesc.setText(firstItem.metaDescription)
        binding.editArabicMetaTitle.setText(firstItem.arabicMetaTitle)
        binding.editArabicMetaDesc.setText(firstItem.arabicMetaDescription)



    }
    private fun handleClick() {

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.cardSave.setOnClickListener {
            manageEditBrandApi()
        }
        binding.cardLogo.setOnClickListener {
            AppPreferences.getInstance(this@EditBrandActivity).saveImgSelectionStatus("single")
            AppPreferences.getInstance(this@EditBrandActivity).saveImgStatus("icon")
            val intent = Intent(this, ImageListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun manageEditBrandApi() {
        val appPreferences = AppPreferences.getInstance(this@EditBrandActivity)
        binding.progressBar.visibility = View.VISIBLE

        val request = ApiService.EditBrandRequest(
            name = binding.editName.text.toString(),
            arabicName = binding.editArabicName.text.toString(),
            metaTitle = binding.editMetaTitle.text.toString(),
            metaDescription = binding.editMetaDesc.text.toString(),
            arabicMetaTitle = binding.editArabicMetaTitle.text.toString(),
            arabicMetaDescription = binding.editArabicMetaDesc.text.toString(),
            id = selectedId,
            logo = appPreferences.getCatIconImgId().toString(),

        )
        viewModel.editBrand(appPreferences.getToken().toString(), request)
    }
}