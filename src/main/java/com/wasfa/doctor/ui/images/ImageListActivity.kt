package com.wasfa.doctor.ui.images

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ActivityImageListBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PhotoMetaData
import com.wasfa.doctor.helper.Product
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.MediaResponse
import com.wasfa.doctor.ui.images.adapter.MediaListAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class ImageListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageListBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var mediaAdapter: MediaListAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    var searchValue: String = ""
    var sortValue: String = ""
    var catId: String = ""
    var catName: String = ""
    var thumbSize: String = ""
    var thumbUrl: String = ""
    var thumbId: String = ""
    private var dX = 0f
    private var dY = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        binding = ActivityImageListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpRecyclerView()
        handleClick()
        setViewModel()
        setUpPagination()
        callMediaApi()
        manageSearch()
    }

    private fun manageSearch() {
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    searchValue = s.toString()
                    callMediaApi()
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })

    }

    private fun handleClick() {
        binding.fabMoveBack.setOnClickListener {
            val appPref = AppPreferences.getInstance(this@ImageListActivity)
            val imgStatus = appPref.getImgStatus()
            val imgSelectionType = appPref.getImgSelectionStatus()

            if (imgSelectionType == "multiple") {
                val selectedUploads = mediaAdapter.getSelectedUploads()
                val photoMetaList = selectedUploads.map {
                    PhotoMetaData(
                        name = it.file_original_name ?: "",
                        sizeInBytes = it.file_size ?: "",
                        id = it.id,
                        fileUrl = it.url
                    )
                }

                val currentProduct = appPref.getAddProduct() ?: Product()

                val updatedProduct = if (appPref.getAddOrEditStatus() == "Edit Product") {
                    // Append
                    currentProduct.copy(photos = currentProduct.photos + photoMetaList)
                } else {
                    // Replace
                    currentProduct.copy(photos = photoMetaList)
                }

                appPref.saveAddProduct(updatedProduct)
                finish()
            }

            else {
                // Keep existing logic for single image selection
                if (imgStatus == "banner") {
                    appPref.saveCatImgId(catId)
                    appPref.saveCatImgName(catName)
                } else if (imgStatus == "icon") {
                    appPref.saveCatIconImgId(catId)
                    appPref.saveCatIconImgName(catName)
                }else if (imgStatus == "seo") {
                    val prefs = AppPreferences.getInstance(this@ImageListActivity)
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product.metaImageId = catId
                    product.metaImageUrl = catName
                    prefs.saveAddProduct(product)
                }else if (imgStatus == "thumbnail"){
                    val prefs = AppPreferences.getInstance(this@ImageListActivity)
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product.thumbUrl = thumbUrl
                    product.thumbSize = thumbSize
                    product.thumbId = thumbId
                    prefs.saveAddProduct(product)
                }
                finish()
            }
        }

        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.cardSortBy.setOnClickListener {
            val popupMenu = PopupMenu(this@ImageListActivity, binding.cardSortBy)
            popupMenu.menuInflater.inflate(R.menu.sort_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.sort_newest -> sortCategories("newest")
                    R.id.sort_oldest -> sortCategories("oldest")
                    R.id.sort_smallest -> sortCategories("smallest")
                    R.id.sort_largest -> sortCategories("largest")
                }
                true
            }

            popupMenu.show()
        }
    }
    fun createEmptyProduct(): Product {
        return Product(
            skuId = "",
            skuName = "",
            productName = "",
            arabicProductName = "",
            shortDesc = "",
            arabicShortDesc = "",
            dosageForm = "",
            categoryId = "",
            categoryName = "",
            brandId = "",
            brandName = "",
            sellerName = "",
            sellerId = "",
            pickUpId = listOf(),
            pickUpName = listOf(),
            unitId = "",
            unitName = "",
            weight = "",
            purchaseFormId = listOf(),
            purchaseFormName = listOf(),
            purchasePrice = "",
            minPurchaseQty = "",
            maxPurchaseQty = "",
            tags = listOf(),
            arabicTags = listOf(),
            relatedProductsName = listOf(),
            relatedProductsId = listOf(),
            upSellingProductsName = listOf(),
            upSellingProductsId = listOf(),
            crossSellingProductsName = listOf(),
            crossSellingProductsId = listOf(),
            barcode = "",
            refundable = "",
            isCancel = "",
            isPharma = "",
            expiryDate = "",
            productStockType = "",
            unitPrice = "",
            discountDateRange = "",
            discount = "",
            discountType = "Flat",
            sellerDiscount = "",
            apixDiscount = "",
            quantity = "",
            sellerSku = "",
            externalLink = "",
            externalLinkButton = "",
            photos = listOf(),
            description = "",
            arabicDescription = "",
            thumbUrl = "",
            thumbId = "",
            thumbSize = "",
            videoType = "Youtube",
            videoLink = "",
            metaTitla = "",
            arabicMetaDesc = "",
            arabicMetaTitla = "",
            metaDesc = "",
            metaImageUrl = "",
            metaImageId = "",
        )
    }
    private fun sortCategories(sortType: String) {
        sortValue = sortType
        callMediaApi()
    }

    private fun callMediaApi() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.currentPageMedia = 1
        viewModel.clearMediaData()
        val request = ApiService.MediaRequest(
            page_no = "1",
            per_page = "4",
            type = "image",
            search = searchValue,
            sortBy = sortValue
        )
        viewModel.getMediaList(AppPreferences.getInstance(this).getToken().toString(), request)
    }

    private fun setUpPagination() {
        binding.recyclerMedia.isNestedScrollingEnabled = false
        scrollListener = ViewTreeObserver.OnScrollChangedListener {

            val view =
                binding.nestedScrollView.getChildAt(binding.nestedScrollView.childCount - 1) as View
            val diff: Int =
                view.bottom - (binding.nestedScrollView.height + binding.nestedScrollView.scrollY)
            if (diff == 0) {
                if (viewModel.isLastPageMedia()) {
                    // No action for the last page
                } else {
                    viewModel.currentPageMedia++
                    viewModel.loadNextPageMedia(searchValue, "image", sortValue)

                    if (binding.progressBar.visibility == View.VISIBLE) {
                        binding.progressBarSmall.visibility = View.GONE
                    } else {
                        binding.progressBarSmall.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.nestedScrollView.viewTreeObserver.addOnScrollChangedListener(scrollListener)


    }

    private fun setUpRecyclerView() {
        mediaAdapter = MediaListAdapter(
            this,
            mutableListOf()
        ) { product, type ->

            binding.fabMoveBack.visibility = View.VISIBLE
            catName = product?.url?.substringAfterLast("/") ?: ""
            catId = product?.id.toString()


            thumbUrl = product.url.toString()
            thumbId  = product.id.toString()
            thumbSize = product.file_size ?: "0"

        }

        binding.recyclerMedia.apply {
            layoutManager = GridLayoutManager(this@ImageListActivity, 2)

            adapter = mediaAdapter
        }
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
        viewModel.showAlertEvent.observe(this) { message ->
            binding.progressBar.visibility = View.GONE


        }
        viewModel.productEvent.observe(this) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.mediaList.observe(this) { data ->

            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountMedia = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPageMedia == 1) {

                manageMedia(data)
            } else {
                mediaAdapter.addProducts(data?.uploads!!)
            }

        }


    }

    private fun manageMedia(data: MediaResponse?) {
        binding.recyclerMedia.visibility = View.VISIBLE
        mediaAdapter.setProducts(data?.uploads?.toMutableList() ?: mutableListOf())
    }

}