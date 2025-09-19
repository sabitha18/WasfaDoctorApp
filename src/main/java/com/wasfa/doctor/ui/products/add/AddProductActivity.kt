package com.wasfa.doctor.ui.products.add

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ActivityAddProductBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PhotoMetaData
import com.wasfa.doctor.helper.Product
import com.wasfa.doctor.network.response.ProductDetailsResponse
import com.wasfa.doctor.ui.products.adapter.EditProductPagerAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var viewModel: HomeViewModel
    private val tabTitles = listOf(
        "General Info",
        "Product Price + Stock",
        "Product Images",
        "Product Description",
        "Product Videos",
        "SEO Meta Tags",
        "Other"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageClick()
        setViewModel()
        binding.txtHeader.text = AppPreferences.getInstance(this@AddProductActivity).getAddOrEditStatus().toString()
        binding.viewPager.adapter = EditProductPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)

                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
    private fun manageClick() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun setViewModel() {
        val appPreferences = AppPreferences.getInstance(this)
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(this@AddProductActivity)
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
        viewModel.productDetailsData.observe(this) { data ->
            binding.progressBar.visibility = View.GONE

            val product = mapProductDetailsResponseToProduct(data)
            AppPreferences.getInstance(this).saveAddProduct(product)
        }

//        if (appPreferences.getAddOrEditStatus()
//                .toString() == "Edit Product"
//        ) {
//            binding.progressBar.visibility = View.VISIBLE
//            val request = ApiService.ProductDetailsRequest(
//                productId = appPreferences?.getProductId().toString()
//            )
//            viewModel.getProductDetails(appPreferences.getToken().toString(), request)
//        }
    }
    private fun mapProductDetailsResponseToProduct(apiData: List<ProductDetailsResponse>?): Product {
        val data = apiData?.getOrNull(0) ?: return Product()

        return Product(
            skuId = data.apix_sku,
            sellerSku = "",
            skuName = data.apix_sku_name,
            scientificName = data.scientific_name,
            productName = data.name,
            arabicProductName = data.arabic_name,
            shortDesc = data.shortDescription,
            arabicShortDesc = data.arabic_short_description,
            dosageForm = data.usage_form,
            categoryId = data.category_id,
            brandId = data.brand_id,
            sellerId = data.seller_id,
            pickUpId = data.pick_up_points.map { it.id },
            unitId = data.unit,
            weight = data.weight,
            purchaseFormName = data.purchase_from.map { it.name },
            purchaseFormId = data.purchase_from.map { it.id },
            purchasePrice = data.purchase_price,
            minPurchaseQty = data.min_qty.ifEmpty { "1" },
            maxPurchaseQty = data.max_qty.ifEmpty { "1" },
            tags = data.tags.split(","),
            arabicTags = data.arabic_tags.split(","),
            relatedProductsId = data.relatedProducts.map { it.id },
            upSellingProductsId = data.upSellProducts.map { it.id },
            crossSellingProductsId = data.crossSellProducts.map { it.id },
            barcode = data.barcode,
            refundable = data.refundable,
            isCancel = data.cancelable,
            isPharma = data.is_pharmaceutical,
            expiryDate = data.expiry_date,
            unitPrice = data.unitPrice.replace("KD", "").trim(),
            discount = data.discount,
            discountType = data.discount_type,
            sellerDiscount = data.seller_discount,
            apixDiscount = data.apix_discount,
            quantity = data.current_stock,
            externalLink = data.external_link,
            externalLinkButton = data.external_link_btn,
            photos = data.photos.mapIndexed { index, photo ->
                PhotoMetaData(
                    id = photo.id,
                    name = photo.name,
                    sizeInBytes = photo.size,
                    fileUrl = photo.path
                )
            },
            description = data.description,
            arabicDescription = data.arabic_description,
            thumbUrl = data.thumbnailImage,
            thumbId = data.thumbnailImageId,
            videoType = data.video_provider,
            videoLink = data.video_link,
            metaTitla = data.meta_title,
            arabicMetaTitla = data.arabic_meta_title,
            metaDesc = data.meta_description,
            arabicMetaDesc = data.arabic_meta_description,
            metaImageUrl = data.meta_img,
            shippingConfig = data.shipping_type,
            lowStockQuantity = data.low_stock_quantity,
            stockVisibilityState = data.stock_visibility_state,
            codStatus = data.cash_on_delivery,
            featuredStatus = data.featured,
            todaysDealStatus = data.todays_deal,
            flashDiscount = data.flash_discount,
            flashDiscountType = data.flash_discount_type,
            productStockType = data.product_stock_type
        )
    }

}