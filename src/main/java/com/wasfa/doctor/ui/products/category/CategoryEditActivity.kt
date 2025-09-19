package com.wasfa.doctor.ui.products.category

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentCategoryEditBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CatList
import com.wasfa.doctor.network.response.CategoryDetailsResponse
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.images.ImageListActivity
import com.wasfa.doctor.ui.products.adapter.CatListAdapter
import com.wasfa.doctor.ui.products.adapter.ProductStockAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class CategoryEditActivity : AppCompatActivity() {
    private lateinit var binding: FragmentCategoryEditBinding
    private lateinit var viewModel: HomeViewModel
    private var selectedId: String = ""
    private var originalData: CategoryDetailsResponse? = null
    private lateinit var catAdapter: CatListAdapter
    private val catList = mutableListOf<CatList>()
    private var popupWindowCat: PopupWindow? = null
    private var popupWindow: PopupWindow? = null
    private lateinit var productStockAdapter: ProductStockAdapter
    private val productStockList = mutableListOf<Cat>()
    var searchValue: String = ""
    var typeStatus: String = ""
    var parentId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        binding = FragmentCategoryEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleClick()
        setViewModel()
        setupFormValidation()
        manageImages()

        binding.cardSave.alpha = 0.5f
        binding.cardSave.isClickable = false
        binding.cardSave.isFocusable = false
    }
    private fun validateFormAndEnableButton() {
        val allEditTextsFilled = listOf(binding.editName, binding.editArabicName)
            .all { it.text.toString().trim().isNotEmpty() }

        val allTextViewsFilled = listOf(binding.textParent, binding.textType)
            .all { it.text.toString().trim().isNotEmpty() }

        val shouldEnable = allEditTextsFilled && allTextViewsFilled

        binding.cardSave.alpha = if (shouldEnable) 1.0f else 0.5f
        binding.cardSave.isClickable = shouldEnable
        binding.cardSave.isFocusable = shouldEnable
    }
    private fun setupFormValidation() {
        val editTexts = listOf(binding.editName, binding.editArabicName)

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
    private fun setViewModel() {
        val appPreferences = AppPreferences.getInstance(this)
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(this)
        ).get(HomeViewModel::class.java)

        viewModel.loadingState.observe(this) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }
        viewModel.categoryList.observe(this) { data ->
            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountCat = totalPages.toInt()
                }
            } catch (e: NumberFormatException) {
                // Handle invalid page number format
            }

            data?.categories?.let {
                if (viewModel.currentPageCat == 1) {
                    catList.clear()
                }
                catList.addAll(it)
                updateCatDropdownList()
            }
        }
        viewModel.productEvent.observe(this) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.showAlertEvent.observe(this) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.editCatFailStatus.observe(this) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this@CategoryEditActivity, message, Toast.LENGTH_LONG).show()

        }
        viewModel.editCatStatus.observe(this) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this@CategoryEditActivity, message, Toast.LENGTH_LONG).show()
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
            finish()
        }
        viewModel.categoryDetails.observe(this) { data ->
            binding.progressBar.visibility = View.GONE
                manageData(data)
        }
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.CatDetailsRequest(
            id = appPreferences.getCategoryId().toString()
        )
        viewModel.getCategoryDetails(appPreferences.getToken().toString(),request)

    }
    private fun manageData(data: List<CategoryDetailsResponse>?) {
        val firstItem = data?.getOrNull(0) ?: return
        originalData = firstItem

        selectedId = firstItem.id.toString()

        binding.editName.setText(firstItem.name)
        binding.editArabicName.setText(firstItem.arabicName)
        binding.textParent.text = firstItem.parentName
        binding.editOrderLevel.setText(firstItem.orderLevel)

        binding.textType.text = if (firstItem.digital == "0") "Physical" else "Digital"

        val bannerUrl = data?.get(0)?.banner ?: ""
        val iconUrl = data?.get(0)?.icon ?: ""

        val appPreferences = AppPreferences.getInstance(this)
        appPreferences.saveCatImgId(data?.get(0)?.bannerId)
        appPreferences.saveCatIconImgId(data?.get(0)?.iconId)

        binding.txtBannerName.text = bannerUrl.substringAfterLast("/")
        binding.txtIcon.text = iconUrl.substringAfterLast("/")

        binding.editMetaTitle.setText(firstItem.metaTitle)
        binding.editMetaDesc.setText(firstItem.metaDescription)
        binding.editArabicMetaTitle.setText(firstItem.arabicMetaTitle)
        binding.editArabicMetaDesc.setText(firstItem.arabicMetaDescription)
        binding.editCommissionRate.setText(firstItem.commisionRate)


    }
    private fun updateCatDropdownList() {
        if (::catAdapter.isInitialized) {
            val flattened = flattenCategories(catList)
            catAdapter.submitList(flattened)
        }
    }
    private fun flattenCategories(categories: List<CatList>?, level: Int = 0): List<CatList> {
        val result = mutableListOf<CatList>()
        Log.d("FLATTEN", "Flattening: ***** ")
        categories?.forEach { cat ->
            Log.d("FLATTEN", "Flattening: ${cat.name}, level=$level")

            val flattenedCat = CatList(
                id = cat.id,
                name = cat.name,
                children = null,
                level = level,
                icon = cat.icon,
                parentCategory = cat.parentCategory
            )

            result.add(flattenedCat)

            // Recursively flatten children
            val children = cat.children ?: emptyList()
            result.addAll(flattenCategories(children, level + 1))
        }
        return result
    }
    private fun manageImages() {
        if (AppPreferences.getInstance(this).getImgStatus() == "banner"){
            if (AppPreferences.getInstance(this).getCatImgId().isNullOrEmpty()) {

            } else {
                binding.txtBannerName.text =
                    AppPreferences.getInstance(this).getCatImgName()
            }
        }

        if (AppPreferences.getInstance(this).getImgStatus() == "icon") {
            if (AppPreferences.getInstance(this).getCatImgId().isNullOrEmpty()) {

            } else {
                binding.txtIcon.text =
                    AppPreferences.getInstance(this).getCatIconImgName()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        manageImages()
    }
    private fun showCategoryDropdown(anchorView: View) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowCat =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.setText(searchValue)

        catAdapter = CatListAdapter { selectedSku ->
            binding.textParent.text = selectedSku.name
            parentId = selectedSku?.id.toString()
            popupWindowCat?.dismiss()
            validateFormAndEnableButton()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = catAdapter

        val flattenedList = flattenCategories(catList)
        Log.d("CAT_LIST", "Flattened list: ${flattenedList.size} items")
        flattenedList.forEach {
            Log.d("FLATTENED", "-".repeat(it.level + 1) + " ${it.name}")
        }

        catAdapter.submitList(flattenedList)

        rvSku.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItem >= totalItemCount - 1 && !viewModel.isLastPageCat()) {
                    viewModel.currentPageCat++
                    viewModel.loadNextPageCat("0")
                }
            }
        })

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredList = flattenCategories(catList).filter {
                    it.name!!.contains(query, ignoreCase = true)
                }
                catAdapter.submitList(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindowCat?.elevation = 10f
        popupWindowCat?.isOutsideTouchable = true
        popupWindowCat?.showAsDropDown(anchorView)
    }
    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Physical",
                R.drawable.dummy_image
            ),
            Cat(
                "Digital",
                R.drawable.dummy_image
            )
        )
    }
    private fun callCatApi() {
        viewModel.currentPageCat = 1
        viewModel.clearCatData()
        val request = ApiService.CatRequest(
            page_no = "1",
            per_page = "4",
            parent_id = "0"
        )
        viewModel.getCategoryList(AppPreferences.getInstance(this).getToken().toString(), request)
    }
    private fun handleClick() {
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.cardSave.setOnClickListener {
            manageEditCatApi()
        }
        binding.cardType.setOnClickListener {
            showTypeDropdown(binding.cardType, createList())
        }
        binding.cardParent.setOnClickListener {
            viewModel.currentPageCat = 1
            callCatApi()
            showCategoryDropdown(binding.cardParent)
        }
        binding.cardBanner.setOnClickListener {
            AppPreferences.getInstance(this@CategoryEditActivity).saveImgSelectionStatus("single")
            AppPreferences.getInstance(this@CategoryEditActivity).saveImgStatus("banner")
            val intent = Intent(this, ImageListActivity::class.java)
            startActivity(intent)
        }
        binding.cardIcon.setOnClickListener {
            AppPreferences.getInstance(this@CategoryEditActivity).saveImgSelectionStatus("single")
            AppPreferences.getInstance(this@CategoryEditActivity).saveImgStatus("icon")
            val intent = Intent(this, ImageListActivity::class.java)
            startActivity(intent)
        }
    }
    private fun manageEditCatApi() {
        val appPreferences = AppPreferences.getInstance(this@CategoryEditActivity)
        binding.progressBar.visibility = View.VISIBLE

        val request = ApiService.EditCatRequest(
            name = binding.editName.text.toString(),
            arabicName = binding.editArabicName.text.toString(),
            orderLevel = binding.editOrderLevel.text.toString(),
            digital = typeStatus,
            banner = appPreferences.getCatImgId().toString(),
            icon = appPreferences.getCatIconImgId().toString(),
            metaTitle = binding.editMetaTitle.text.toString(),
            metaDescription = binding.editMetaDesc.text.toString(),
            arabicMetaTitle = binding.editArabicMetaTitle.text.toString(),
            arabicMetaDescription = binding.editArabicMetaDesc.text.toString(),
            parentId = parentId,
            commisionRate = binding.editCommissionRate.text.toString(),
            id = selectedId

        )
        viewModel.editCategory(appPreferences.getToken().toString(), request)
    }
    private fun showTypeDropdown(anchorView: View, skuList: MutableList<Cat>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindow =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val edtSearch = popupView.findViewById<MaterialCardView>(R.id.card_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.visibility = View.GONE
        productStockAdapter = ProductStockAdapter { selectedSku ->
            // Handle selection
            binding.textType.text = selectedSku.name
            if (selectedSku.name == "Digital") {
                typeStatus = "1"
            } else {
                typeStatus = "0"
            }
            popupWindow?.dismiss()
            validateFormAndEnableButton()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = productStockAdapter
        productStockAdapter.submitList(skuList)

        popupWindow?.elevation = 10f
        popupWindow?.isOutsideTouchable = true
        popupWindow?.showAsDropDown(anchorView)
    }
}
