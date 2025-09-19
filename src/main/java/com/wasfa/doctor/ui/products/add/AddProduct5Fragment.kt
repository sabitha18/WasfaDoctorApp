package com.wasfa.doctor.ui.products.add

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentAddproduct5Binding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.Product
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.main.MainActivity
import com.wasfa.doctor.ui.products.adapter.ProductStockAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class AddProduct5Fragment : Fragment() {

    private var _binding: FragmentAddproduct5Binding? = null
    private val binding get() = _binding!!
    private var popupWindow: PopupWindow? = null
    private lateinit var typeAdapter: ProductStockAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var quantityRunnable: Runnable? = null
    private var discountRunnable: Runnable? = null
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddproduct5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        handleClick()
        handleEditTexts()
        manageShippingConfiguration()
        manageStockVisibility()
        manageCOD()
        manageFeatured()
        manageDeal()

        if (AppPreferences.getInstance(requireContext()).getAddOrEditStatus() == "Edit Product"){
            handleEditData()
        }

    }
    private fun handleEditData() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        var product = appPreferences.getAddProduct()

        product?.let {
            println(" empty********************  noooooooooo     " + product?.productName)
            if (it.shippingConfig.isNotEmpty()) {

                if (it.shippingConfig == "free"){
                    binding.switchFree.isChecked = true
                    binding.switchFlat.isChecked = false
                }else{
                    binding.switchFree.isChecked = false
                    binding.switchFlat.isChecked = true
                }
            }
            if (it.lowStockQuantity.isNotEmpty()) {
                binding.editQuantity.setText(it.lowStockQuantity)
            }
            if (it.stockVisibilityState.isNotEmpty()) {
                when (it.stockVisibilityState) {
                    "text" -> {
                        binding.switchShowWithQuantity.isChecked = false
                        binding.switchShowWithText.isChecked = true
                        binding.switchHideStock.isChecked = false
                    }

                    "hide" -> {
                        binding.switchShowWithQuantity.isChecked = false
                        binding.switchShowWithText.isChecked = false
                        binding.switchHideStock.isChecked = true
                    }

                    else -> {
                        binding.switchShowWithQuantity.isChecked = true
                        binding.switchShowWithText.isChecked = false
                        binding.switchHideStock.isChecked = false

                    }
                }
            }
            if (it.codStatus.isNotEmpty()) {
                if (it.codStatus == "1"){
                    binding.switchCod.isChecked = true
                }else{
                    binding.switchCod.isChecked = false
                }
            }
            if (it.featuredStatus.isNotEmpty()) {
                if (it.featuredStatus == "1"){
                    binding.switchFeatured.isChecked = true
                }else{
                    binding.switchFeatured.isChecked = false
                }
            }
            if (it.todaysDealStatus.isNotEmpty()) {
                if (it.todaysDealStatus == "1"){
                    binding.switchTodayDeal.isChecked = true
                }else{
                    binding.switchTodayDeal.isChecked = false
                }
            }
            if (it.flashDiscountType.isNotEmpty()){
                binding.txtDiscountType.text = it.flashDiscountType
            }
            if (it.flashDiscount.isNotEmpty()){
                binding.editDiscount.setText(it.flashDiscount)
            }
        } ?: {
            println(" empty********************" + product?.productName)
        }

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

        viewModel.addProductFailStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.addProductStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun manageDeal() {
        val prefs = AppPreferences.getInstance(requireContext())
        var currentProduct = prefs.getAddProduct()

        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for codStatus")
        }


        binding.switchTodayDeal.isChecked = currentProduct.todaysDealStatus == "1"

        binding.switchTodayDeal.setOnCheckedChangeListener { _, isChecked ->
            currentProduct.todaysDealStatus = if (isChecked) "1" else "0"
            prefs.saveAddProduct(currentProduct)
        }
    }

    private fun manageFeatured() {
        val prefs = AppPreferences.getInstance(requireContext())
        var currentProduct = prefs.getAddProduct()

        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for codStatus")
        }


        binding.switchFeatured.isChecked = currentProduct.featuredStatus == "1"

        binding.switchFeatured.setOnCheckedChangeListener { _, isChecked ->
            currentProduct.featuredStatus = if (isChecked) "1" else "0"
            prefs.saveAddProduct(currentProduct)
        }
    }

    private fun manageCOD() {
        val prefs = AppPreferences.getInstance(requireContext())
        var currentProduct = prefs.getAddProduct()

        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for codStatus")
        }


        binding.switchCod.isChecked = currentProduct.codStatus == "1"

        binding.switchCod.setOnCheckedChangeListener { _, isChecked ->
            currentProduct.codStatus = if (isChecked) "1" else "0"
            prefs.saveAddProduct(currentProduct)
        }
    }


    private fun manageStockVisibility() {
        val prefs = AppPreferences.getInstance(requireContext())
        var currentProduct = prefs.getAddProduct()

        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for stockVisibilityState")
        }


        when (currentProduct.stockVisibilityState) {
            "text" -> {
                binding.switchShowWithQuantity.isChecked = false
                binding.switchShowWithText.isChecked = true
                binding.switchHideStock.isChecked = false
            }

            "hide" -> {
                binding.switchShowWithQuantity.isChecked = false
                binding.switchShowWithText.isChecked = false
                binding.switchHideStock.isChecked = true
            }

            else -> {
                binding.switchShowWithQuantity.isChecked = true
                binding.switchShowWithText.isChecked = false
                binding.switchHideStock.isChecked = false
                currentProduct.stockVisibilityState = "quantity"
                prefs.saveAddProduct(currentProduct)
            }
        }


        binding.switchShowWithQuantity.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchShowWithText.isChecked = false
                binding.switchHideStock.isChecked = false
                currentProduct.stockVisibilityState = "quantity"
                prefs.saveAddProduct(currentProduct)
            }
        }

        binding.switchShowWithText.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchShowWithQuantity.isChecked = false
                binding.switchHideStock.isChecked = false
                currentProduct.stockVisibilityState = "text"
                prefs.saveAddProduct(currentProduct)
            }
        }

        binding.switchHideStock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchShowWithQuantity.isChecked = false
                binding.switchShowWithText.isChecked = false
                currentProduct.stockVisibilityState = "hide"
                prefs.saveAddProduct(currentProduct)
            }
        }
    }


    private fun manageShippingConfiguration() {
        val prefs = AppPreferences.getInstance(requireContext())
        var currentProduct = prefs.getAddProduct()

        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for shippingConfig")
        }


        when (currentProduct.shippingConfig) {
            "free" -> {
                binding.switchFree.isChecked = true
                binding.switchFlat.isChecked = false
            }

            "flat" -> {
                binding.switchFree.isChecked = false
                binding.switchFlat.isChecked = true
            }

            else -> {

                binding.switchFree.isChecked = false
                binding.switchFlat.isChecked = true
                currentProduct.shippingConfig = "flat"
                prefs.saveAddProduct(currentProduct)
            }
        }


        binding.switchFree.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchFlat.isChecked = false
                currentProduct.shippingConfig = "free"
                prefs.saveAddProduct(currentProduct)
            }
        }


        binding.switchFlat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.switchFree.isChecked = false
                currentProduct.shippingConfig = "flat"
                prefs.saveAddProduct(currentProduct)
            }
        }
    }


    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Flat",
                R.drawable.dummy_image
            ),
            Cat(
                "Percent",
                R.drawable.dummy_image
            )
        )
    }

    private fun showDiscountTypeDropdown(anchorView: View, skuList: MutableList<Cat>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindow =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val edtSearch = popupView.findViewById<MaterialCardView>(R.id.card_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.visibility = View.GONE
        typeAdapter = ProductStockAdapter { selectedSku ->
            // Handle selection
            binding.txtDiscountType.text = selectedSku.name
            val prefs = AppPreferences.getInstance(requireContext())
            var currentProduct = prefs.getAddProduct()

            if (currentProduct == null) {
                currentProduct = createEmptyProduct()
                Log.d("ProductSave", "Initialized empty product for stock type save")
            }

            currentProduct.flashDiscountType = selectedSku.name
            prefs.saveAddProduct(currentProduct)

            Log.d("ProductSave", "Saved stock type: ${selectedSku.name}")

            popupWindow?.dismiss()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = typeAdapter
        typeAdapter.submitList(skuList)

        popupWindow?.elevation = 10f
        popupWindow?.isOutsideTouchable = true
        popupWindow?.showAsDropDown(anchorView)
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

            shippingConfig = "",
            lowStockQuantity = "",
            stockVisibilityState = "",
            codStatus = "",
            featuredStatus = "",
            todaysDealStatus = "",
            flashDiscount = "",
            flashDiscountType = ""
        )
    }

    private fun handleEditTexts() {
        binding.editQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quantityRunnable?.let { handler.removeCallbacks(it) }

                quantityRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product.lowStockQuantity = name
                    prefs.saveAddProduct(product)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(quantityRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.editDiscount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                discountRunnable?.let { handler.removeCallbacks(it) }

                discountRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.flashDiscount = name
                    prefs.saveAddProduct(product!!)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(discountRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun handleClick() {
        val prefs = AppPreferences.getInstance(requireContext())

        binding.cardDiscountType.setOnClickListener {
            showDiscountTypeDropdown(binding.cardDiscountType, createList())
        }

        binding.cardSaveUnPublish.setOnClickListener {
            if (prefs.getAddOrEditStatus() == "Edit Product"){
                callUpdateApi("unpublish")
            }else{
                callAddApi("unpublish")
            }

        }
        binding.cardSavePublish.setOnClickListener {
            if (prefs.getAddOrEditStatus() == "Edit Product"){
                callUpdateApi("publish")
            }else{
                callAddApi("publish")
            }

        }
    }

    private fun callAddApi(buttonStatus: String) {

        val prefs = AppPreferences.getInstance(requireContext())
        var currentProduct = prefs.getAddProduct()

        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for shippingConfig")
        }

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.AddProductRequest(
            apix_sku = currentProduct.skuId.toString(),
            name = currentProduct.productName.toString(),
            arabic_name = currentProduct.arabicProductName.toString(),
            scientific_name = "",
            short_description = currentProduct.shortDesc.toString(),
            arabic_short_description = currentProduct.arabicShortDesc.toString(),
            usage_form = "",
            category_id = currentProduct.categoryId.toString(),
            brand_id = currentProduct.brandId.toString(),
            origin_id = "",
            seller_id = currentProduct.sellerId.toString(),
            pick_up_point_id = currentProduct.pickUpId,
            medical_rep_id = "",
            unit = currentProduct.unitId.toString(),
            weight = currentProduct.weight.toString(),
            purchase_from = currentProduct.purchaseFormId,
            purchase_price = currentProduct.purchasePrice.toString(),
            min_qty = currentProduct.minPurchaseQty.toString(),
            max_qty = currentProduct.maxPurchaseQty.toString(),
            tags = currentProduct.tags.joinToString(","),
            arabic_tags = currentProduct.arabicTags.joinToString(","),
            related_products = currentProduct.relatedProductsId,
            up_sell_products = currentProduct.upSellingProductsId,
            cross_sell_products = currentProduct.crossSellingProductsId,
            barcode = currentProduct.barcode.toString(),
            refundable =  if (currentProduct.refundable.toString() == "1") "1" else "0",
            cancelable =  if (currentProduct.isCancel.toString() == "1") "1" else "0",
            is_pharmaceutical =  if (currentProduct.isPharma.toString() == "1") "1" else "0",
            on_request = "0",
            expiry_date = currentProduct.expiryDate,
            product_level = currentProduct.productStockType,
            unit_price = currentProduct.unitPrice,
            date_range = currentProduct.discountDateRange,
            discount = currentProduct.discount,
            discount_type = currentProduct.discountType,
            seller_discount = currentProduct.sellerDiscount,
            apix_discount = currentProduct.apixDiscount,
            max_discount = "20",
            sku = currentProduct.sellerSku,
            current_stock = currentProduct.quantity,
            external_link = currentProduct.externalLink,
            external_link_btn = currentProduct.externalLinkButton,
            photos = currentProduct.photos.joinToString(",") { it.id },
            thumbnail_img = currentProduct.thumbId,
            description = currentProduct.description,
            arabic_description = currentProduct.arabicDescription,
            video_provider = currentProduct.videoType.toString(),
            video_link = currentProduct.videoLink.toString(),
            pdf = "",
            meta_title = currentProduct.metaTitla.toString(),
            arabic_meta_title = currentProduct.arabicMetaTitla.toString(),
            meta_description = currentProduct.metaDesc.toString(),
            arabic_meta_description = currentProduct.arabicMetaDesc.toString(),
            meta_img = currentProduct.metaImageId.toString(),
            shipping_type = currentProduct.shippingConfig,
            flat_shipping_cost = "",
            low_stock_quantity = currentProduct.lowStockQuantity.toString(),
            stock_visibility_state = currentProduct.stockVisibilityState,
            cash_on_delivery = if (currentProduct.codStatus.toString() == "1") "1" else "0",
            featured = if (currentProduct.featuredStatus.toString() == "1") "1" else "0",
            todays_deal = if (currentProduct.todaysDealStatus.toString() == "1") "1" else "0",
            flash_deal_id = "",
            flash_discount = currentProduct.flashDiscount.toString(),
            flash_discount_type = currentProduct.flashDiscountType.toString(),
            button = buttonStatus
        )
        viewModel.addProduct(prefs.getToken().toString(), request)
    }
    private fun callUpdateApi(buttonStatus: String) {

        val prefs = AppPreferences.getInstance(requireContext())
        var currentProduct = prefs.getAddProduct()
        Log.d("ProductSave", currentProduct!!.productStockType)
        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for shippingConfig")
        }

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.UpdateProductRequest(
            id = prefs.getProductId().toString(),
            apix_sku = currentProduct.skuId.toString(),
            name = currentProduct.productName.toString(),
            arabic_name = currentProduct.arabicProductName.toString(),
            scientific_name = "",
            short_description = currentProduct.shortDesc.toString(),
            arabic_short_description = currentProduct.arabicShortDesc.toString(),
            usage_form = currentProduct.dosageForm.toString(),
            category_id = currentProduct.categoryId.toString(),
            brand_id = currentProduct.brandId.toString(),
            origin_id = "",
            seller_id = currentProduct.sellerId.toString(),
            pick_up_point_id = currentProduct.pickUpId,
            medical_rep_id = "",
            unit = currentProduct.unitId.toString(),
            weight = currentProduct.weight.toString(),
            purchase_from = currentProduct.purchaseFormId,
            purchase_price = currentProduct.purchasePrice.toString(),
            min_qty = currentProduct.minPurchaseQty.toString(),
            max_qty = currentProduct.maxPurchaseQty.toString(),
            tags = currentProduct.tags.joinToString(","),
            arabic_tags = currentProduct.arabicTags.joinToString(","),
            related_products = currentProduct.relatedProductsId,
            up_sell_products = currentProduct.upSellingProductsId,
            cross_sell_products = currentProduct.crossSellingProductsId,
            barcode = currentProduct.barcode.toString(),
            refundable =  if (currentProduct.refundable.toString() == "1") "1" else "0",
            cancelable =  if (currentProduct.isCancel.toString() == "1") "1" else "0",
            is_pharmaceutical =  if (currentProduct.isPharma.toString() == "1") "1" else "0",
            on_request = "0",
            expiry_date = currentProduct.expiryDate,
            product_level = currentProduct.productStockType,
            unit_price = currentProduct.unitPrice,
            date_range = currentProduct.discountDateRange,
            discount = currentProduct.discount,
            discount_type = currentProduct.discountType,
            seller_discount = currentProduct.sellerDiscount,
            apix_discount = currentProduct.apixDiscount,
            max_discount = "20",
            sku = currentProduct.sellerSku,
            current_stock = currentProduct.quantity,
            external_link = currentProduct.externalLink,
            external_link_btn = currentProduct.externalLinkButton,
            photos = currentProduct.photos.joinToString(",") { it.id },
            thumbnail_img = currentProduct.thumbId,
            description = currentProduct.description,
            arabic_description = currentProduct.arabicDescription,
            video_provider = currentProduct.videoType.toString(),
            video_link = currentProduct.videoLink.toString(),
            pdf = "",
            meta_title = currentProduct.metaTitla.toString(),
            arabic_meta_title = currentProduct.arabicMetaTitla.toString(),
            meta_description = currentProduct.metaDesc.toString(),
            arabic_meta_description = currentProduct.arabicMetaDesc.toString(),
            meta_img = currentProduct.metaImageId.toString(),
            shipping_type = currentProduct.shippingConfig,
            flat_shipping_cost = "",
            low_stock_quantity = currentProduct.lowStockQuantity.toString(),
            stock_visibility_state = currentProduct.stockVisibilityState,
            cash_on_delivery = if (currentProduct.codStatus.toString() == "1") "1" else "0",
            featured = if (currentProduct.featuredStatus.toString() == "1") "1" else "0",
            todays_deal = if (currentProduct.todaysDealStatus.toString() == "1") "1" else "0",
            flash_deal_id = "",
            flash_discount = currentProduct.flashDiscount.toString(),
            flash_discount_type = currentProduct.flashDiscountType.toString(),
            button = buttonStatus
        )
        viewModel.updateProduct(prefs.getToken().toString(), request)
    }

    fun showAlertCustom(message: String) {
        val builder = AlertDialog.Builder(requireContext())


        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.validation_alert, null)
        builder.setView(customLayout)
        val text_validation = customLayout.findViewById<TextView>(R.id.text_validation)
        text_validation.text = message
        val continueShoppingButton = customLayout.findViewById<MaterialCardView>(R.id.view_cart)
        lateinit var dialog: AlertDialog
        continueShoppingButton.setOnClickListener {
            dialog?.dismiss()
        }

        dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        quantityRunnable?.let { handler.removeCallbacks(it) }
        quantityRunnable = null

        discountRunnable?.let { handler.removeCallbacks(it) }
        discountRunnable = null
    }
}