package com.wasfa.doctor.ui.products.add

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentAddproductBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.helper.PhotoMetaData
import com.wasfa.doctor.helper.Product
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.Brands
import com.wasfa.doctor.network.response.CatList
import com.wasfa.doctor.network.response.PickUps
import com.wasfa.doctor.network.response.ProductDetailsResponse
import com.wasfa.doctor.network.response.Products
import com.wasfa.doctor.network.response.PurchaseForm
import com.wasfa.doctor.network.response.SKU
import com.wasfa.doctor.network.response.SellerListResponse
import com.wasfa.doctor.network.response.Units
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.products.adapter.BrandListAdapter
import com.wasfa.doctor.ui.products.adapter.CatListAdapter
import com.wasfa.doctor.ui.products.adapter.CrossSellingAdapter
import com.wasfa.doctor.ui.products.adapter.PickUpAdapter
import com.wasfa.doctor.ui.products.adapter.ProductStockAdapter
import com.wasfa.doctor.ui.products.adapter.PurchaseFormAdapter
import com.wasfa.doctor.ui.products.adapter.RelatedProductAdapter
import com.wasfa.doctor.ui.products.adapter.SellerListAdapter
import com.wasfa.doctor.ui.products.adapter.SkuListAdapter
import com.wasfa.doctor.ui.products.adapter.UnitListAdapter
import com.wasfa.doctor.ui.products.adapter.UpSellingAdapter
import com.wasfa.doctor.ui.products.brand.BrandActivity
import com.wasfa.doctor.ui.products.category.CategoryActivity

import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.util.Calendar


class AddProductFragment : Fragment() {

    private var _binding: FragmentAddproductBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    var searchValue: String = ""
    var searchValueProduct: String = ""

    private val productList = mutableListOf<Products>()
    private lateinit var productAdapter: RelatedProductAdapter
    private lateinit var usProductAdapter: UpSellingAdapter
    private lateinit var csProductAdapter: CrossSellingAdapter
    private val skuList = mutableListOf<SKU>()
    private lateinit var skuAdapter: SkuListAdapter
    private lateinit var catAdapter: CatListAdapter
    private val catList = mutableListOf<CatList>()
    private lateinit var productStockAdapter: ProductStockAdapter
    private val productStockList = mutableListOf<Cat>()
    private lateinit var brandAdapter: BrandListAdapter
    private val brandList = mutableListOf<Brands>()
    private lateinit var sellerAdapter: SellerListAdapter
    private val sellerList = mutableListOf<SellerListResponse>()
    private lateinit var pickUpAdapter: PickUpAdapter
    private val pickUpList = mutableListOf<PickUps>()
    private lateinit var unitAdapter: UnitListAdapter
    private val unitList = mutableListOf<Units>()
    private lateinit var purchaseAdapter: PurchaseFormAdapter
    private val purchaseList = mutableListOf<PurchaseForm>()

    private lateinit var scrollListenerSKU: ViewTreeObserver.OnScrollChangedListener

    private var popupWindow: PopupWindow? = null
    private var popupWindowCat: PopupWindow? = null
    private var popupWindowBrand: PopupWindow? = null
    private var popupWindowProductStock: PopupWindow? = null
    private var popupWindowSeller: PopupWindow? = null
    private var popupWindowPickUps: PopupWindow? = null
    private var popupWindowUnits: PopupWindow? = null
    private var popupWindowPurchaseForm: PopupWindow? = null
    private var popupWindowRelatedPD: PopupWindow? = null
    private var popupWindowUSP: PopupWindow? = null
    private var popupWindowCSP: PopupWindow? = null

    private val selectedProducts = mutableListOf<Products>()
    private val selectedProductsUSP = mutableListOf<Products>()
    private val selectedProductsCSP = mutableListOf<Products>()
    private val selectedPF = mutableListOf<PurchaseForm>()
    private val selectedPickUps = mutableListOf<PickUps>()
    private val tagList = mutableListOf<String>()
    private val arabicTagList = mutableListOf<String>()
    private val handler = Handler(Looper.getMainLooper())
    private var productNameRunnable: Runnable? = null
    private var scientificNameRunnable: Runnable? = null
    private var arabicProductNameRunnable: Runnable? = null
    private var shortDescRunnable: Runnable? = null
    private var arabicShortDescRunnable: Runnable? = null
    private var dosageFormRunnable: Runnable? = null
    private var weightRunnable: Runnable? = null
    private var purchasePriceRunnable: Runnable? = null
    private var minPurchaseRunnable: Runnable? = null
    private var maxPurchaseRunnable: Runnable? = null
    private var barcodeRunnable: Runnable? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddproductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()


        if (AppPreferences.getInstance(requireContext()).getLoginType().toString() == "staff") {
            manageStaffViews()
        }

//        callSKUAPI()
//        callCatApi()
//        callProductAPI()
        handleClick()
        setupTagInput()
        setupArabicTagInput()
        handleEditTexts()
        handleSwitch()


    }

    private fun manageStaffViews() = with(binding) {
        cardAddSku.setVisibleIfPermission(PermissionKeys.ADD_NEW_APIX_SKU)
        cardAddCategory.setVisibleIfPermission(PermissionKeys.ADD_PRODUCT_CATEGORY)
        cardAddBrand.setVisibleIfPermission(PermissionKeys.ADD_BRAND)
        cardAddUnit.setVisibleIfPermission(PermissionKeys.ADD_UNIT)
    }

    private fun View.setVisibleIfPermission(permissionKey: String) {
        visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE

    }

    private fun handleEditData() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        var product = appPreferences.getAddProduct()
        Log.d(
            "MappedProduct",
            "related IDs: ${product?.relatedProductsId}, Names: ${product?.relatedProductsName}"
        )

        product?.let {
            println(" empty********************  noooooooooo     " + product?.productName)
            if (it.skuName.isNotEmpty()) {
                binding.txtSkuValue.text = it.skuName
            }
            if (it.scientificName.isNotEmpty()) {
                binding.editScientificName.setText(it.scientificName)
            }
            if (it.productName.isNotEmpty()) {
                binding.editProductName.setText(it.productName)
            }
            if (it.arabicProductName.isNotEmpty()) {
                binding.editArabicProductName.setText(it.arabicProductName)
            }
            if (it.shortDesc.isNotEmpty()) {
                binding.editShortDesc.setText(
                    HtmlCompat.fromHtml(
                        it.shortDesc,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                )

            }
            if (it.arabicShortDesc.isNotEmpty()) {
                binding.editArabicShortDesc.setText(
                    HtmlCompat.fromHtml(
                        it.arabicShortDesc,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                )
            }
            if (it.dosageForm.isNotEmpty()) {
                binding.editDosageFrom.setText(it.dosageForm)
            }
            if (it.categoryName.isNotEmpty()) {
                binding.txtCategory.text = it.categoryName
            }
            if (it.brandName.isNotEmpty()) {
                binding.txtBrand.setText(it.brandName)
            }
            if (it.pickUpName.isNotEmpty()) {
                if (!it.pickUpName.isNullOrEmpty()) {
                    selectedPickUps.clear()
                    for (i in it.pickUpId.indices) {
                        selectedPickUps.add(
                            PickUps(
                                id = it.pickUpId.getOrNull(i).toString(),
                                name = it.pickUpName.getOrNull(i).toString()
                            )
                        )
                    }
                    showSelectedPickUpChips()
                } else {

                    binding.pickUpHint.visibility = View.VISIBLE
                }
            }
            if (it.sellerName.isNotEmpty()) {
                binding.txtSeller.setText(it.sellerName)
            }
            if (it.unitName.isNotEmpty()) {
                binding.txtUnit.text = it.unitName
            }
            if (it.weight.isNotEmpty()) {
                binding.editWeight.setText(it.weight)
            }
            if (it.purchasePrice.isNotEmpty()) {
                binding.editPurchasePrice.setText(it.purchasePrice)
            }
            if (it.minPurchaseQty.isNotEmpty()) {
                binding.editMinPurchaseQty.setText(it.minPurchaseQty)
            }
            if (it.maxPurchaseQty.isNotEmpty()) {
                binding.editMaxPurchaseQty.setText(it.maxPurchaseQty)
            }
            if (!it.tags.isNullOrEmpty()) {
                tagList.clear()
                tagList.addAll(it.tags)
                tagList.forEach { tag ->
                    addTagChip(tag)
                }
                updateTagHint()
            }
            if (!it.arabicTags.isNullOrEmpty()) {
                arabicTagList.clear()
                arabicTagList.addAll(it.arabicTags)
                arabicTagList.forEach { tag ->
                    addArabicTagChip(tag)
                }
                updateArabicTagHint()
            }
            if (!it.relatedProductsId.isNullOrEmpty()) {
                selectedProducts.clear()
                for (i in it.relatedProductsId.indices) {
                    selectedProducts.add(
                        Products(
                            id = it.relatedProductsId.getOrNull(i).toString(),
                            name = it.relatedProductsName.getOrNull(i).toString(),
                            thumbnail_image = "",
                            discount = "",
                            basePrice = "",
                            purchasePrice = "",
                            createdAt = "",
                            published = "",
                            rating = "",
                            numberOfsales = "",
                            sellerDiscount = "",
                            sellerLogo = "",
                            sellerName = "",
                            unitPrice = "",
                            sellerSku = "",
                            seller = "",
                            apixSku = "",
                            currentStock = "",
                            publishedOnWebsite = "",
                            Restricted = "",
                            publishedOnPos = "",
                            isPharma = "",
                            isClean = "",
                            cog = "",
                            apixMargin = "",
                            influencerrMargin = "",
                            isFavourite = false
                        )
                    )
                }
                showSelectedProductChips()
            } else {
                Log.e("ProductError", "relatedProductsId and relatedProductsName size mismatch!")
            }
            if (!it.upSellingProductsId.isNullOrEmpty()) {
                selectedProductsUSP.clear()
                for (i in it.upSellingProductsId.indices) {
                    selectedProductsUSP.add(
                        Products(
                            id = it.upSellingProductsId.getOrNull(i).toString(),
                            name = it.upSellingProductsName.getOrNull(i).toString(),
                            thumbnail_image = "",
                            discount = "",
                            basePrice = "",
                            purchasePrice = "",
                            createdAt = "",
                            published = "",
                            rating = "",
                            numberOfsales = "",
                            sellerDiscount = "",
                            sellerLogo = "",
                            sellerName = "",
                            unitPrice = "",
                            sellerSku = "",
                            seller = "",
                            apixSku = "",
                            currentStock = "",
                            publishedOnWebsite = "",
                            Restricted = "",
                            publishedOnPos = "",
                            isPharma = "",
                            isClean = "",
                            cog = "",
                            apixMargin = "",
                            influencerrMargin = "",
                            isFavourite = false
                        )
                    )
                }
                showSelectedProductChipsUSP()
            }
            if (!it.crossSellingProductsId.isNullOrEmpty()) {
                selectedProductsCSP.clear()
                for (i in it.crossSellingProductsId.indices) {
                    selectedProductsCSP.add(
                        Products(
                            id = it.crossSellingProductsId.getOrNull(i).toString(),
                            name = it.crossSellingProductsName.getOrNull(i).toString(),
                            thumbnail_image = "",
                            discount = "",
                            basePrice = "",
                            purchasePrice = "",
                            createdAt = "",
                            published = "",
                            rating = "",
                            numberOfsales = "",
                            sellerDiscount = "",
                            sellerLogo = "",
                            sellerName = "",
                            unitPrice = "",
                            sellerSku = "",
                            seller = "",
                            apixSku = "",
                            currentStock = "",
                            publishedOnWebsite = "",
                            Restricted = "",
                            publishedOnPos = "",
                            isPharma = "",
                            isClean = "",
                            cog = "",
                            apixMargin = "",
                            influencerrMargin = "",
                            isFavourite = false
                        )
                    )
                }
                showSelectedProductChipsCSP()
            }

            if (it.barcode.isNotEmpty()) {
                binding.editBarcode.setText(it.barcode)
            }
            if (it.refundable == "1") {
                binding.switchRefundable.isChecked = true
            }
            if (it.isCancel == "1") {
                binding.switchIsCancel.isChecked = true
            }
            if (it.isPharma == "1") {
                binding.switchIsPharma.isChecked = true
            }
            if (it.expiryDate.isNotEmpty()) {
                binding.txtExpiryDate.text = it.expiryDate
            }
            if (it.productStockType.isNotEmpty()) {
                binding.txtProductStockType.text = it.productStockType
            }
        } ?: {
            println(" empty********************" + product?.productName)
        }

    }

    private fun handleSwitch() {
        binding.switchRefundable.setOnCheckedChangeListener { _, isChecked ->
            val prefs = AppPreferences.getInstance(requireContext())
            var product = prefs.getAddProduct() ?: createEmptyProduct()

            product.refundable = if (isChecked) "1" else "0"
            prefs.saveAddProduct(product)

            Log.d("RefundableSwitch", "Saved refundable: ${product.refundable}")
        }
        binding.switchIsCancel.setOnCheckedChangeListener { _, isChecked ->
            val prefs = AppPreferences.getInstance(requireContext())
            var product = prefs.getAddProduct() ?: createEmptyProduct()

            product.isCancel = if (isChecked) "1" else "0"
            prefs.saveAddProduct(product)

            Log.d("RefundableSwitch", "Saved refundable: ${product.refundable}")
        }
        binding.switchIsPharma.setOnCheckedChangeListener { _, isChecked ->
            val prefs = AppPreferences.getInstance(requireContext())
            var product = prefs.getAddProduct() ?: createEmptyProduct()

            product.isPharma = if (isChecked) "1" else "0"
            prefs.saveAddProduct(product)

            Log.d("RefundableSwitch", "Saved refundable: ${product.refundable}")
        }
    }

    private fun callCatApi() {
        viewModel.currentPageCat = 1
        viewModel.clearCatData()
        val request = ApiService.CatRequest(
            page_no = "1",
            per_page = "4",
            parent_id = "0"
        )
        viewModel.getCategoryList(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )
    }

    private fun callBrandApi() {
        viewModel.currentPageBrand = 1
        viewModel.clearBrandData()
        val request = ApiService.BrandRequest(
            page_no = "1",
            per_page = "4"
        )
        viewModel.getBrandList(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }

    fun createEmptyProduct(): Product {
        return Product(
            scientificName = "",
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
        binding.editScientificName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                scientificNameRunnable?.let { handler.removeCallbacks(it) }

                scientificNameRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product.scientificName = name
                    prefs.saveAddProduct(product)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(scientificNameRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editProductName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                productNameRunnable?.let { handler.removeCallbacks(it) }

                productNameRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.productName = name
                    prefs.saveAddProduct(product!!)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(productNameRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editArabicProductName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                arabicProductNameRunnable?.let { handler.removeCallbacks(it) }

                arabicProductNameRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.arabicProductName = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(arabicProductNameRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editShortDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                shortDescRunnable?.let { handler.removeCallbacks(it) }

                shortDescRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.shortDesc = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(shortDescRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editArabicShortDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                arabicShortDescRunnable?.let { handler.removeCallbacks(it) }

                arabicShortDescRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.arabicShortDesc = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(arabicShortDescRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editDosageFrom.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                dosageFormRunnable?.let { handler.removeCallbacks(it) }

                dosageFormRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.dosageForm = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(dosageFormRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                weightRunnable?.let { handler.removeCallbacks(it) }

                weightRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.weight = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(weightRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editMinPurchaseQty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                minPurchaseRunnable?.let { handler.removeCallbacks(it) }

                minPurchaseRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.minPurchaseQty = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(minPurchaseRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editMaxPurchaseQty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                maxPurchaseRunnable?.let { handler.removeCallbacks(it) }

                maxPurchaseRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.maxPurchaseQty = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(maxPurchaseRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editBarcode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                barcodeRunnable?.let { handler.removeCallbacks(it) }

                barcodeRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.barcode = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(barcodeRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "In House Consignment",
                R.drawable.dummy_image
            ),
            Cat(
                "In House Stock",
                R.drawable.dummy_image
            ),
            Cat(
                "Vendor Consignment",
                R.drawable.dummy_image
            )
        )
    }

    private fun updateTagHint() {
        val editTags = binding.editTags
        if (tagList.isEmpty()) {
            editTags.hint = "Type and hit enter to add a tag"
        } else {
            editTags.hint = ""
        }
    }

    private fun updateArabicTagHint() {
        val editTags = binding.editArabicTags
        if (arabicTagList.isEmpty()) {
            editTags.hint = "Type and hit enter to add a tag"
        } else {
            editTags.hint = ""
        }
    }

    private fun setupArabicTagInput() {
        val tagContainer = binding.arabicTagContainer
        val editTags = binding.editArabicTags

        editTags.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val tag = editTags.text.toString().trim()
                if (tag.isNotEmpty() && !arabicTagList.contains(tag)) {
                    arabicTagList.add(tag)
                    addArabicTagChip(tag)
                    editTags.text.clear()
                    updateArabicTagHint()
                }
                true
            } else {
                false
            }
        }
        updateArabicTagHint()
    }

    private fun addArabicTagChip(tag: String) {
        val tagContainer = binding.arabicTagContainer
        val chipView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_related_product_chip, tagContainer, false)

        val tagText = chipView.findViewById<TextView>(R.id.chipText)
        val tagClose = chipView.findViewById<ImageView>(R.id.chipClose)

        tagText.text = tag

        tagClose.setOnClickListener {
            arabicTagList.remove(tag)
            tagContainer.removeView(chipView)
            val prefs = AppPreferences.getInstance(requireContext())
            val product =
                prefs.getAddProduct() ?: createEmptyProduct().also { prefs.saveAddProduct(it) }
            product.tags = arabicTagList
            prefs.saveAddProduct(product)
        }


        tagContainer.addView(chipView, tagContainer.childCount - 1)


        val prefs = AppPreferences.getInstance(requireContext())
        val product =
            prefs.getAddProduct() ?: createEmptyProduct().also { prefs.saveAddProduct(it) }
        product.arabicTags = arabicTagList
        prefs.saveAddProduct(product)
    }

    private fun setupTagInput() {
        val tagContainer = binding.tagContainer
        val editTags = binding.editTags

        editTags.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val tag = editTags.text.toString().trim()
                if (tag.isNotEmpty() && !tagList.contains(tag)) {
                    tagList.add(tag)
                    addTagChip(tag)
                    editTags.text.clear()
                    updateTagHint()
                }
                true
            } else {
                false
            }
        }
        updateTagHint()
    }

    private fun addTagChip(tag: String) {
        val tagContainer = binding.tagContainer

        val chipView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_related_product_chip, tagContainer, false)

        val tagText = chipView.findViewById<TextView>(R.id.chipText)
        val tagClose = chipView.findViewById<ImageView>(R.id.chipClose)

        tagText.text = tag

        tagClose.setOnClickListener {
            tagList.remove(tag)
            tagContainer.removeView(chipView)


            val prefs = AppPreferences.getInstance(requireContext())
            val product =
                prefs.getAddProduct() ?: createEmptyProduct().also { prefs.saveAddProduct(it) }
            product.tags = tagList
            prefs.saveAddProduct(product)
        }


        tagContainer.addView(chipView, tagContainer.childCount - 1)


        val prefs = AppPreferences.getInstance(requireContext())
        val product =
            prefs.getAddProduct() ?: createEmptyProduct().also { prefs.saveAddProduct(it) }
        product.tags = tagList
        prefs.saveAddProduct(product)
    }

    private fun callProductAPI() {

        viewModel.currentPage = 1
        viewModel.clearProductListData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.ProductRequest(
            page_no = "1",
            per_page = "4",
            category = "",
            brand = "",
            sku = "",
            seller = "",
            medical_rep_id = "",
            keyword = searchValueProduct,
            influencer_id = "",
            isFavourite = "",
            listFrom = ""
        )

        viewModel.getProductList(appPreferences.getToken().toString(), request)
    }

    private fun callSKUAPI() {

        viewModel.currentPageSKU = 1
        viewModel.clearSKUData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.SKURequest(
            pageNo = "1",
            perPage = "4",
            search = searchValue
        )

        viewModel.getSKUList(appPreferences.getToken().toString(), request)
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
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE


        }
        viewModel.productDetailsData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE

            val product = mapProductDetailsResponseToProduct(data)
            appPreferences.saveAddProduct(product)
            println("edited -----------   "+appPreferences.getAddProduct()?.productName)
            handleEditData()
        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.SKUData.observe(viewLifecycleOwner) { data ->

            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountSKU = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPageSKU == 1) {
                skuList.clear()
                skuList.addAll(data?.apixSku!!)
            } else {
                skuList.addAll(data?.apixSku!!)
            }
            updateSkuDropdownList()


        }

        viewModel.productListData.observe(viewLifecycleOwner) { data ->

            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCount = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPage == 1) {
                productList.clear()
                productList.addAll(data?.products!!)

            } else {
                productList.addAll(data?.products!!)
            }
            updateProductDropdownList()


        }

        viewModel.brandList.observe(viewLifecycleOwner) { data ->

            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountBrand = totalPages.toInt()
                }
            } catch (e: NumberFormatException) {
                // Handle invalid page number format
            }

            data?.brands?.let {
                if (viewModel.currentPageBrand == 1) {
                    brandList.clear()
                }
                brandList.addAll(it)
                updateBrandDropdownList()
            }

        }
        viewModel.pickUpList.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            pickUpList.addAll(data?.pickuppoints!!)

        }
        viewModel.sellerList.observe(viewLifecycleOwner) { data ->

            sellerList.addAll(data)

        }
        viewModel.categoryList.observe(viewLifecycleOwner) { data ->
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

        viewModel.unitList.observe(viewLifecycleOwner) { data ->

            unitList.addAll(data?.units!!)

        }
        viewModel.purchaseList.observe(viewLifecycleOwner) { data ->

            purchaseList.addAll(data?.list!!)

        }
        val request = ApiService.BrandRequest(
            page_no = "1000",
            per_page = "1"
        )

        viewModel.getBrandList(appPreferences.getToken().toString(), request)
        viewModel.getSellerList(appPreferences.getToken().toString())
        viewModel.getUnitList(appPreferences.getToken().toString())
        viewModel.getPurchaseFormList(appPreferences.getToken().toString())

        viewModel.addUnitStatus.observe(viewLifecycleOwner) { message ->

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            viewModel.getSellerList(appPreferences.getToken().toString())

        }
        viewModel.addUnitFailStatus.observe(viewLifecycleOwner) { message ->

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()


        }
        if (AppPreferences.getInstance(requireContext()).getAddOrEditStatus()
                .toString() == "Edit Product"
        ) {
            binding.progressBar.visibility = View.VISIBLE
            val request = ApiService.ProductDetailsRequest(
                productId = AppPreferences.getInstance(requireContext()).getProductId().toString()
            )
            viewModel.getProductDetails(
                AppPreferences.getInstance(requireContext()).getToken().toString(), request
            )
        }

    }


    private fun updateProductDropdownList() {
        if (::productAdapter.isInitialized) {
            productAdapter.submitList(productList.toList())
        }
        if (::usProductAdapter.isInitialized) {
            usProductAdapter.submitList(productList.toList())
        }
        if (::csProductAdapter.isInitialized) {
            csProductAdapter.submitList(productList.toList())
        }
    }

    private fun updateSkuDropdownList() {
        if (::skuAdapter.isInitialized) {
            skuAdapter.submitList(skuList.toList()) // pass a new list to trigger diff
        }
    }

    private fun updateCatDropdownList() {
        if (::catAdapter.isInitialized) {
            val flattened = flattenCategories(catList)
            catAdapter.submitList(flattened)
        }
    }

    private fun updateBrandDropdownList() {
        if (::brandAdapter.isInitialized) {
            brandAdapter.submitList(brandList)
        }
    }

    private fun handleClick() {
        binding.cardAddUnit.setOnClickListener {
            showAddUnitDialog()
        }

        binding.cardProductStockType.setOnClickListener {
            showProductStockDropdown(binding.cardProductStockType, createList())
        }

        binding.cardAddSku.setOnClickListener {
            showAddSkuDialog()
        }
        binding.cardApixSku.setOnClickListener {
            viewModel.currentPageSKU = 1
            callSKUAPI()
            showSkuDropdown(binding.cardApixSku, skuList)

        }
        binding.cardRelatedProducts.setOnClickListener {
            viewModel.currentPage = 1
            searchValueProduct = ""
            callProductAPI()
            showProductDropdown(binding.cardRelatedProducts, productList)

        }

        binding.cardUpSellingProducts.setOnClickListener {
            viewModel.currentPage = 1
            callProductAPI()
            searchValueProduct = ""
            showUpSellingDropdown(binding.cardUpSellingProducts, productList)

        }

        binding.cardCrossSellingProducts.setOnClickListener {
            viewModel.currentPage = 1
            callProductAPI()
            searchValueProduct = ""
            showCrossSellingDropdown(binding.cardCrossSellingProducts, productList)

        }
        binding.cardSeller.setOnClickListener {
            showSellerDropdown(binding.cardSeller, sellerList)
        }
        binding.cardBrand.setOnClickListener {
            viewModel.currentPageBrand = 1
            callBrandApi()
            showBrandDropdown(binding.cardBrand)
        }
        binding.cardPickUp.setOnClickListener {
            showPickUpDropdown(binding.cardPickUp, pickUpList)
        }
        binding.cardUnit.setOnClickListener {
            showUnitDropdown(binding.cardUnit, unitList)
        }
        binding.cardPurchaseFrom.setOnClickListener {
            showPurchaseFormDropdown(binding.cardPurchaseFrom, purchaseList)
        }
        binding.cardCategory.setOnClickListener {
            viewModel.currentPageCat = 1
            callCatApi()
            showCategoryDropdown(binding.cardCategory)
        }

        binding.cardExpiryDate.setOnClickListener {
            showDatePickerDialog()
        }
        binding.cardAddCategory.setOnClickListener {

            val intent = Intent(requireContext(), CategoryActivity::class.java)
            startActivity(intent)

        }
        binding.cardAddBrand.setOnClickListener {
            val intent = Intent(requireContext(), BrandActivity::class.java)
            startActivity(intent)

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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date to Y-m-d
                val formattedDate =
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.txtExpiryDate.text = formattedDate
                val prefs = AppPreferences.getInstance(requireContext())
                var currentProduct = prefs.getAddProduct()

                if (currentProduct == null) {
                    currentProduct = createEmptyProduct()
                    Log.d("ProductSave", "Initialized empty product for unit selection")
                }

                currentProduct.expiryDate = formattedDate

                prefs.saveAddProduct(currentProduct)
            }, year, month, day)
        datePicker.datePicker.minDate = System.currentTimeMillis()

        datePicker.show()
    }

    private fun showPurchaseFormDropdown(anchorView: View, skuList: MutableList<PurchaseForm>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowPurchaseForm =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val txtNoData = popupView.findViewById<TextView>(R.id.txt_no_data)
        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        if (skuList.isNullOrEmpty()) {
            txtNoData.visibility = View.VISIBLE
        } else {
            txtNoData.visibility = View.GONE
        }

        edtSearch.setText(searchValue)
        purchaseAdapter = PurchaseFormAdapter { selectedSku ->
            selectedPF.clear()
            selectedPF.addAll(selectedSku)
            showSelectedPFChips()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = purchaseAdapter
        purchaseAdapter.submitList(skuList)



        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredList = skuList.filter {
                    it.purchase_from.contains(query, ignoreCase = true)
                }
                purchaseAdapter.submitList(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindowPurchaseForm?.elevation = 10f
        popupWindowPurchaseForm?.isOutsideTouchable = true
        popupWindowPurchaseForm?.showAsDropDown(anchorView)
    }

    private fun showSelectedPFChips() {
        val container = binding.purchaseFromContainer
        val hint = binding.purchaseFromHint
        container.removeAllViews()

        if (selectedPF.isEmpty()) {
            hint.visibility = View.VISIBLE
            return
        } else {
            hint.visibility = View.GONE
        }
        val prefs = AppPreferences.getInstance(requireContext())
        val formIds = selectedPF.mapNotNull { it.id }
        val formNames = selectedPF.mapNotNull { it.purchase_from }

        var currentProduct = prefs.getAddProduct()
        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product in PF save")
        }

        currentProduct.purchaseFormId = formIds
        currentProduct.purchaseFormName = formNames
        prefs.saveAddProduct(currentProduct)

        Log.d("ProductSave", "Saved Purchase Forms: $formNames ($formIds)")

        selectedPF.forEach { product ->
            val chipView = LayoutInflater.from(container.context)
                .inflate(R.layout.item_related_product_chip, container, false)

            val chipText = chipView.findViewById<TextView>(R.id.chipText)
            val chipClose = chipView.findViewById<ImageView>(R.id.chipClose)

            chipText.text = product.purchase_from ?: ""

            chipClose.setOnClickListener {
                selectedPF.remove(product)
                updateAdapterPFSelection()
                showSelectedPFChips()
            }

            container.addView(chipView)
        }
    }

    private fun updateAdapterPFSelection() {
        val selectedIds = selectedPF.mapNotNull { it.id }.toSet()
        purchaseAdapter.restoreSelection(selectedIds)
    }

    private fun showUnitDropdown(anchorView: View, skuList: MutableList<Units>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowUnits =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val txtNoData = popupView.findViewById<TextView>(R.id.txt_no_data)
        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        if (skuList.isNullOrEmpty()) {
            txtNoData.visibility = View.VISIBLE
        } else {
            txtNoData.visibility = View.GONE
        }

        edtSearch.setText(searchValue)
        unitAdapter = UnitListAdapter { selectedSku ->
            // Handle selection
            binding.txtUnit.text = selectedSku.name

            val prefs = AppPreferences.getInstance(requireContext())
            var currentProduct = prefs.getAddProduct()

            if (currentProduct == null) {
                currentProduct = createEmptyProduct()
                Log.d("ProductSave", "Initialized empty product for unit selection")
            }

            currentProduct.unitId = selectedSku.id
            currentProduct.unitName = selectedSku.name

            prefs.saveAddProduct(currentProduct)
            Log.d("ProductSave", "Saved unit: ${selectedSku.name} (ID: ${selectedSku.id})")

            popupWindowUnits?.dismiss()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = unitAdapter
        unitAdapter.submitList(skuList)



        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredList = skuList.filter {
                    it.name.contains(query, ignoreCase = true)
                }
                unitAdapter.submitList(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindowUnits?.elevation = 10f
        popupWindowUnits?.isOutsideTouchable = true
        popupWindowUnits?.showAsDropDown(anchorView)
    }

    private fun showPickUpDropdown(anchorView: View, skuList: MutableList<PickUps>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowPickUps =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val txtNoData = popupView.findViewById<TextView>(R.id.txt_no_data)
        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        if (skuList.isNullOrEmpty()) {
            txtNoData.visibility = View.VISIBLE
        } else {
            txtNoData.visibility = View.GONE
        }

        edtSearch.setText(searchValue)
        pickUpAdapter = PickUpAdapter { selectedSku ->
            selectedPickUps.clear()
            selectedPickUps.addAll(selectedSku)
            showSelectedPickUpChips()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = pickUpAdapter
        pickUpAdapter.submitList(skuList)



        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredList = skuList.filter {
                    it.name.contains(query, ignoreCase = true)
                }
                pickUpAdapter.submitList(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindowPickUps?.elevation = 10f
        popupWindowPickUps?.isOutsideTouchable = true
        popupWindowPickUps?.showAsDropDown(anchorView)
    }

    private fun showSelectedPickUpChips() {
        val container = binding.pickUpContainer
        val hint = binding.pickUpHint
        container.removeAllViews()

        if (selectedPickUps.isEmpty()) {
            hint.visibility = View.VISIBLE
            return
        } else {
            hint.visibility = View.GONE
        }
        val prefs = AppPreferences.getInstance(requireContext())
        val formIds = selectedPickUps.mapNotNull { it.id }
        val formNames = selectedPickUps.mapNotNull { it.name }

        var currentProduct = prefs.getAddProduct()
        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for pickup save")
        }

        currentProduct.pickUpId = formIds
        currentProduct.pickUpName = formNames

        prefs.saveAddProduct(currentProduct)

        Log.d("ProductSave", "Saved pickup points: $formNames ($formIds)")

        selectedPickUps.forEach { product ->
            val chipView = LayoutInflater.from(container.context)
                .inflate(R.layout.item_related_product_chip, container, false)

            val chipText = chipView.findViewById<TextView>(R.id.chipText)
            val chipClose = chipView.findViewById<ImageView>(R.id.chipClose)

            chipText.text = product.name ?: ""

            chipClose.setOnClickListener {
                selectedPickUps.remove(product)
                updateAdapterPickUpSelection()
                showSelectedPickUpChips()
            }

            container.addView(chipView)
        }
    }

    private fun updateAdapterPickUpSelection() {
        val selectedIds = selectedPickUps.mapNotNull { it.id }.toSet()
        pickUpAdapter.restoreSelection(selectedIds)
    }

    private fun showSellerDropdown(anchorView: View, skuList: MutableList<SellerListResponse>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowSeller =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)


        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.setText(searchValue)
        pickUpAdapter = PickUpAdapter { selectedSku ->

        }
        sellerAdapter = SellerListAdapter { selectedSku ->
            // Handle selection
            binding.txtSeller.text = selectedSku.name
            val prefs = AppPreferences.getInstance(requireContext())
            var currentProduct = prefs.getAddProduct()

            if (currentProduct == null) {
                currentProduct = createEmptyProduct()
                Log.d("ProductSave", "Initialized empty product for seller save")
            }

            currentProduct.sellerId = selectedSku.id
            currentProduct.sellerName = selectedSku.name

            prefs.saveAddProduct(currentProduct)

            Log.d("ProductSave", "Saved seller: ${selectedSku.name} (ID: ${selectedSku.id})")

            popupWindowSeller?.dismiss()
            binding.progressBar.visibility = View.VISIBLE
            val request = ApiService.PickUpPointsRequest(
                sellerId = selectedSku?.id.toString()
            )
            pickUpList.clear()
            selectedPickUps.clear()
            showSelectedPickUpChips()
            viewModel.getPickUpListList(
                AppPreferences.getInstance(requireContext()).getToken().toString(), request
            )
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = sellerAdapter
        sellerAdapter.submitList(skuList)



        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredList = skuList.filter {
                    it.name.contains(query, ignoreCase = true)
                }
                sellerAdapter.submitList(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindowSeller?.elevation = 10f
        popupWindowSeller?.isOutsideTouchable = true
        popupWindowSeller?.showAsDropDown(anchorView)
    }

    private fun showProductStockDropdown(anchorView: View, skuList: MutableList<Cat>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowProductStock =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val edtSearch = popupView.findViewById<MaterialCardView>(R.id.card_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.visibility = View.GONE
        productStockAdapter = ProductStockAdapter { selectedSku ->
            // Handle selection
            binding.txtProductStockType.text = selectedSku.name
            val prefs = AppPreferences.getInstance(requireContext())
            var currentProduct = prefs.getAddProduct()

            if (currentProduct == null) {
                currentProduct = createEmptyProduct()
                Log.d("ProductSave", "Initialized empty product for stock type save")
            }

            currentProduct.productStockType = selectedSku.name
            prefs.saveAddProduct(currentProduct)

            Log.d("ProductSave", "Saved stock type: ${selectedSku.name}")

            popupWindowProductStock?.dismiss()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = productStockAdapter
        productStockAdapter.submitList(skuList)

        popupWindowProductStock?.elevation = 10f
        popupWindowProductStock?.isOutsideTouchable = true
        popupWindowProductStock?.showAsDropDown(anchorView)
    }

    private fun showBrandDropdown(anchorView: View) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowBrand =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)


        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.setText(searchValue)
        brandAdapter = BrandListAdapter { selectedSku ->
            // Handle selection
            binding.txtBrand.text = selectedSku.name
            val prefs = AppPreferences.getInstance(requireContext())
            var currentProduct = prefs.getAddProduct()

            if (currentProduct == null) {
                currentProduct = createEmptyProduct()
                Log.d("ProductSave", "Initialized empty product for stock type save")
            }

            currentProduct.brandId = selectedSku.id
            currentProduct.brandName = selectedSku.id
            prefs.saveAddProduct(currentProduct)

            Log.d("ProductSave", "Saved stock type: ${selectedSku.name}")

            popupWindowBrand?.dismiss()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = brandAdapter
        brandAdapter.submitList(brandList)

        rvSku.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItem >= totalItemCount - 1 && !viewModel.isLastPageBrand()) {
                    viewModel.currentPageBrand++
                    viewModel.loadNextPageBrand()
                }
            }
        })

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredList = brandList.filter {
                    it.name.contains(query, ignoreCase = true)
                }
                brandAdapter.submitList(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindowBrand?.elevation = 10f
        popupWindowBrand?.isOutsideTouchable = true
        popupWindowBrand?.showAsDropDown(anchorView)
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
            binding.txtCategory.text = selectedSku.name
            val prefs = AppPreferences.getInstance(requireContext())
            var currentProduct = prefs.getAddProduct()

            if (currentProduct == null) {
                currentProduct = createEmptyProduct()
                Log.d("ProductSave", "Initialized empty product for stock type save")
            }

            currentProduct.categoryId = selectedSku.id.toString()
            currentProduct.categoryName = selectedSku.name.toString()
            prefs.saveAddProduct(currentProduct)

            Log.d("ProductSave", "Saved stock type: ${selectedSku.name}")

            popupWindowCat?.dismiss()
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

    private fun showSkuDropdown(anchorView: View, skuList: MutableList<SKU>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_sku_dropdown, null)
        popupWindow =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)


        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.setText(searchValue)
        skuAdapter = SkuListAdapter { selectedSku ->
            // Handle selection
            binding.txtSkuValue.text = selectedSku.sku
            searchValue = ""
            val prefs = AppPreferences.getInstance(requireContext())
            var currentProduct = prefs.getAddProduct()

            if (currentProduct == null) {
                currentProduct = createEmptyProduct()
                Log.d("ProductSave", "Initialized empty product for stock type save")
            }

            currentProduct.skuId = selectedSku.id
            currentProduct.skuName = selectedSku.sku
            prefs.saveAddProduct(currentProduct)

            Log.d("ProductSave", "Saved stock type: ${selectedSku.sku}")

            popupWindow?.dismiss()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = skuAdapter
        skuAdapter.submitList(skuList)

        rvSku.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Load more when we reach the end
                if (lastVisibleItem >= totalItemCount - 1 && !viewModel.isLastPageSKU()) {
                    viewModel.currentPageSKU++
                    viewModel.loadNextPageSKU(searchValue)
                }
            }
        })

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchValue = s.toString()
                callSKUAPI()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindow?.elevation = 10f
        popupWindow?.isOutsideTouchable = true
        popupWindow?.showAsDropDown(anchorView)
    }

    private fun showProductDropdown(anchorView: View, skuList: MutableList<Products>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_sku_dropdown, null)
        popupWindowRelatedPD =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)


        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.setText(searchValueProduct)
        productAdapter = RelatedProductAdapter { selectedList ->

            selectedProducts.clear()
            selectedProducts.addAll(selectedList)
            showSelectedProductChips()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = productAdapter
        productAdapter.submitList(skuList)

        rvSku.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Load more when we reach the end
                if (lastVisibleItem >= totalItemCount - 1 && !viewModel.isLastPage()) {
                    viewModel.currentPage++
                    viewModel.loadNextPage("", "", searchValueProduct, "", "", "", "", "")

                }
            }
        })

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchValueProduct = s.toString()
                callProductAPI()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindowRelatedPD?.elevation = 10f
        popupWindowRelatedPD?.isOutsideTouchable = true
        popupWindowRelatedPD?.showAsDropDown(anchorView)
    }

    private fun showSelectedProductChips() {
        val container = binding.relatedProductsContainer
        val hint = binding.relatedProductsHint
        container.removeAllViews()

        if (selectedProducts.isEmpty()) {
            hint.visibility = View.VISIBLE
            return
        } else {
            hint.visibility = View.GONE
        }
        val prefs = AppPreferences.getInstance(requireContext())
        val formIds = selectedProducts.mapNotNull { it.id }
        val formNames = selectedProducts.mapNotNull { it.name }

        var currentProduct = prefs.getAddProduct()
        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for pickup save")
        }

        currentProduct.relatedProductsId = formIds
        currentProduct.relatedProductsName = formNames

        prefs.saveAddProduct(currentProduct)

        Log.d("ProductSave", "Saved pickup points: $formNames ($formIds)")

        selectedProducts.forEach { product ->
            val chipView = LayoutInflater.from(container.context)
                .inflate(R.layout.item_related_product_chip, container, false)

            val chipText = chipView.findViewById<TextView>(R.id.chipText)
            val chipClose = chipView.findViewById<ImageView>(R.id.chipClose)

            chipText.text = product.name ?: ""

            chipClose.setOnClickListener {
                selectedProducts.remove(product)
                updateAdapterSelection()
                showSelectedProductChips()
            }

            container.addView(chipView)
        }
    }

    private fun updateAdapterSelection() {
        val selectedIds = selectedProducts.mapNotNull { it.id }.toSet()
        productAdapter.restoreSelection(selectedIds)
    }

    private fun showAddUnitDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_unit, null)
        val editArabic = dialogView.findViewById<EditText>(R.id.edit_count)
        val editUnit = dialogView.findViewById<EditText>(R.id.edit_unit)
        val txtGenerate = dialogView.findViewById<TextView>(R.id.txt_generate)
        val progressBar = dialogView.findViewById<RelativeLayout>(R.id.progressBar_small)
        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val btnSave = dialogView.findViewById<MaterialCardView>(R.id.card_generate)
        btnSave.alpha = 1f
        btnSave.isClickable = true
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnSave.setOnClickListener {
            val input = editUnit.text.toString().trim()
            val arabicInput = editArabic.text.toString().trim()

            if (input.isNotEmpty() && arabicInput.isNotEmpty()) {

                progressBar.visibility = View.VISIBLE
                txtGenerate.visibility = View.GONE
                val request = ApiService.AddUnitRequest(
                    name = editUnit.text.toString(),
                    arabicName = editArabic.text.toString()
                )
                viewModel.addUnit(
                    AppPreferences.getInstance(requireContext()).getToken().toString(), request
                )
                dialog.cancel()
            }
        }

        imgClose.setOnClickListener {
            dialog.cancel()
        }
        dialog.show()
    }

    private fun showAddSkuDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_sku, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_count)
        val txtResult = dialogView.findViewById<TextView>(R.id.txt_sku)
        val txtGenerate = dialogView.findViewById<TextView>(R.id.txt_generate)
        val progressBar = dialogView.findViewById<RelativeLayout>(R.id.progressBar_small)
        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val btnSave = dialogView.findViewById<MaterialCardView>(R.id.card_generate)
        btnSave.alpha = 1f
        btnSave.isClickable = true
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()
        viewModel.GenerateValue.removeObservers(viewLifecycleOwner)
        viewModel.GenerateValue.observe(viewLifecycleOwner) { skuList ->
            if (!skuList.isNullOrEmpty()) {
                progressBar.visibility = View.GONE
                btnSave.alpha = 0.5f
                btnSave.isClickable = false
                txtGenerate.visibility = View.VISIBLE
                val displaySku = skuList.joinToString(separator = ", ")
                txtResult.text = displaySku // or any other TextView
            }
        }
        btnSave.setOnClickListener {
            val input = editText.text.toString().trim()
            if (input.isNotEmpty()) {
                // Call your Add SKU API

                progressBar.visibility = View.VISIBLE
                txtGenerate.visibility = View.GONE
                val request = ApiService.GenerateSKURequest(
                    count = editText.text.toString()
                )
                viewModel.generateApixSku(
                    AppPreferences.getInstance(requireContext()).getToken().toString(), request
                )
            } else {
                txtResult.text = "Enter valid SKU"
            }
        }

        imgClose.setOnClickListener {
            dialog.cancel()
        }
        dialog.show()
    }

    private fun showUpSellingDropdown(anchorView: View, skuList: MutableList<Products>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_sku_dropdown, null)
        popupWindowUSP =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)


        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.setText(searchValueProduct)
        usProductAdapter = UpSellingAdapter { selectedList ->

            selectedProductsUSP.clear()
            selectedProductsUSP.addAll(selectedList)
            showSelectedProductChipsUSP()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = usProductAdapter
        usProductAdapter.submitList(skuList)

        rvSku.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Load more when we reach the end
                if (lastVisibleItem >= totalItemCount - 1 && !viewModel.isLastPage()) {
                    viewModel.currentPage++
                    viewModel.loadNextPage("", "", searchValueProduct, "", "", "", "", "")

                }
            }
        })

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchValueProduct = s.toString()
                callProductAPI()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindowUSP?.elevation = 10f
        popupWindowUSP?.isOutsideTouchable = true
        popupWindowUSP?.showAsDropDown(anchorView)
    }

    private fun showSelectedProductChipsUSP() {
        val container = binding.upSellingProductsContainer
        val hint = binding.upSellingProductsHint
        container.removeAllViews()

        if (selectedProductsUSP.isEmpty()) {
            hint.visibility = View.VISIBLE
            return
        } else {
            hint.visibility = View.GONE
        }
        val prefs = AppPreferences.getInstance(requireContext())
        val formIds = selectedProductsUSP.mapNotNull { it.id }
        val formNames = selectedProductsUSP.mapNotNull { it.name }

        var currentProduct = prefs.getAddProduct()
        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for pickup save")
        }

        currentProduct.upSellingProductsId = formIds
        currentProduct.upSellingProductsName = formNames

        prefs.saveAddProduct(currentProduct)

        Log.d("ProductSave", "Saved pickup points: $formNames ($formIds)")

        selectedProductsUSP.forEach { product ->
            val chipView = LayoutInflater.from(container.context)
                .inflate(R.layout.item_related_product_chip, container, false)

            val chipText = chipView.findViewById<TextView>(R.id.chipText)
            val chipClose = chipView.findViewById<ImageView>(R.id.chipClose)

            chipText.text = product.name ?: ""

            chipClose.setOnClickListener {
                selectedProductsUSP.remove(product)
                updateAdapterSelectionUSP()
                showSelectedProductChipsUSP()
            }

            container.addView(chipView)
        }
    }

    private fun updateAdapterSelectionUSP() {
        val selectedIds = selectedProductsUSP.mapNotNull { it.id }.toSet()
        usProductAdapter.restoreSelection(selectedIds)
    }

    private fun showCrossSellingDropdown(anchorView: View, skuList: MutableList<Products>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_sku_dropdown, null)
        popupWindowCSP =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)


        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.setText(searchValueProduct)
        csProductAdapter = CrossSellingAdapter { selectedList ->

            selectedProductsCSP.clear()
            selectedProductsCSP.addAll(selectedList)
            showSelectedProductChipsCSP()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = csProductAdapter
        csProductAdapter.submitList(skuList)

        rvSku.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Load more when we reach the end
                if (lastVisibleItem >= totalItemCount - 1 && !viewModel.isLastPage()) {
                    viewModel.currentPage++
                    viewModel.loadNextPage("", "", searchValueProduct, "", "", "", "", "")

                }
            }
        })

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchValueProduct = s.toString()
                callProductAPI()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        popupWindowCSP?.elevation = 10f
        popupWindowCSP?.isOutsideTouchable = true
        popupWindowCSP?.showAsDropDown(anchorView)
    }

    private fun showSelectedProductChipsCSP() {
        val container = binding.crossSellingProductsContainer
        val hint = binding.crossSellingProductsHint
        container.removeAllViews()

        if (selectedProductsCSP.isEmpty()) {
            hint.visibility = View.VISIBLE
            return
        } else {
            hint.visibility = View.GONE
        }
        val prefs = AppPreferences.getInstance(requireContext())
        val formIds = selectedProductsCSP.mapNotNull { it.id }
        val formNames = selectedProductsCSP.mapNotNull { it.name }

        var currentProduct = prefs.getAddProduct()
        if (currentProduct == null) {
            currentProduct = createEmptyProduct()
            Log.d("ProductSave", "Initialized empty product for pickup save")
        }

        currentProduct.crossSellingProductsId = formIds
        currentProduct.crossSellingProductsName = formNames

        prefs.saveAddProduct(currentProduct)

        Log.d("ProductSave", "Saved pickup points: $formNames ($formIds)")

        selectedProductsCSP.forEach { product ->
            val chipView = LayoutInflater.from(container.context)
                .inflate(R.layout.item_related_product_chip, container, false)

            val chipText = chipView.findViewById<TextView>(R.id.chipText)
            val chipClose = chipView.findViewById<ImageView>(R.id.chipClose)

            chipText.text = product.name ?: ""

            chipClose.setOnClickListener {
                selectedProductsCSP.remove(product)
                updateAdapterSelectionCSP()
                showSelectedProductChipsCSP()
            }

            container.addView(chipView)
        }
    }

    private fun updateAdapterSelectionCSP() {
        val selectedIds = selectedProductsCSP.mapNotNull { it.id }.toSet()
        csProductAdapter.restoreSelection(selectedIds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the pending Runnable to prevent crash/memory leak
        productNameRunnable?.let { handler.removeCallbacks(it) }
        productNameRunnable = null

        scientificNameRunnable?.let { handler.removeCallbacks(it) }
        scientificNameRunnable = null

        arabicProductNameRunnable?.let { handler.removeCallbacks(it) }
        arabicProductNameRunnable = null

        shortDescRunnable?.let { handler.removeCallbacks(it) }
        shortDescRunnable = null

        arabicShortDescRunnable?.let { handler.removeCallbacks(it) }
        arabicShortDescRunnable = null

        dosageFormRunnable?.let { handler.removeCallbacks(it) }
        dosageFormRunnable = null

        weightRunnable?.let { handler.removeCallbacks(it) }
        weightRunnable = null

        purchasePriceRunnable?.let { handler.removeCallbacks(it) }
        purchasePriceRunnable = null

        minPurchaseRunnable?.let { handler.removeCallbacks(it) }
        minPurchaseRunnable = null

        maxPurchaseRunnable?.let { handler.removeCallbacks(it) }
        maxPurchaseRunnable = null

        barcodeRunnable?.let { handler.removeCallbacks(it) }
        barcodeRunnable = null
    }

    private fun mapProductDetailsResponseToProduct(apiData: List<ProductDetailsResponse>?): Product {
        val data = apiData?.getOrNull(0) ?: return Product()
        Log.d("API-Debug", "related_products = ${data.product_stock_type}")

        return Product(
            skuId = data.apix_sku ?: "",
            skuName = data.apix_sku_name ?: "",
            productName = data.name ?: "",
            arabicProductName = data.arabic_name ?: "",
            shortDesc = data.shortDescription ?: "",
            arabicShortDesc = data.arabic_short_description ?: "",
            dosageForm = data.usage_form ?: "",
            categoryId = data.category_id ?: "",
            brandId = data.brand_id ?: "",
            sellerId = data.seller_id ?: "",
            pickUpId = data.pick_up_points?.map { it.id } ?: emptyList(),
            unitId = data.unit ?: "",
            weight = data.weight ?: "",
            purchaseFormId = data.purchase_from.map { it.id },
            purchasePrice = data.purchase_price ?: "",
            minPurchaseQty = data.min_qty?.ifEmpty { "1" } ?: "1",
            maxPurchaseQty = data.max_qty?.ifEmpty { "1" } ?: "1",
            tags = data.tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            arabicTags = data.arabic_tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            relatedProductsId = data.relatedProducts?.map { it.id } ?: emptyList(),
            upSellingProductsId = data.upSellProducts?.map { it.id } ?: emptyList(),
            crossSellingProductsId = data.crossSellProducts?.map { it.id } ?: emptyList(),
            barcode = data.barcode ?: "",
            refundable = data.refundable ?: "0",
            isCancel = data.cancelable ?: "0",
            isPharma = data.is_pharmaceutical ?: "0",
            expiryDate = data.expiry_date ?: "",
            unitPrice = data.unitPrice?.replace("KD", "")?.trim() ?: "",
            discount = data.discount ?: "",
            discountType = data.discount_type ?: "",
            sellerDiscount = data.seller_discount ?: "",
            apixDiscount = data.apix_discount ?: "",
            quantity = data.current_stock ?: "",
            externalLink = data.external_link ?: "",
            externalLinkButton = data.external_link_btn ?: "",
            photos = data.photos?.map { photo ->
                PhotoMetaData(
                    id = photo.id ?: "",
                    name = photo.name ?: "",
                    sizeInBytes = photo.size ?: "0",
                    fileUrl = photo.path ?: ""
                )
            } ?: emptyList(),
            description = data.description ?: "",
            arabicDescription = data.arabic_description ?: "",
            thumbUrl = data.thumbnailImage ?: "",
            thumbId = data.thumbnailImageId ?: "",
            videoType = data.video_provider ?: "",
            videoLink = data.video_link ?: "",
            metaTitla = data.meta_title ?: "",
            arabicMetaTitla = data.arabic_meta_title ?: "",
            metaDesc = data.meta_description ?: "",
            arabicMetaDesc = data.arabic_meta_description ?: "",
            metaImageUrl = data.meta_img ?: "",
            shippingConfig = data.shipping_type ?: "",
            lowStockQuantity = data.low_stock_quantity ?: "",
            stockVisibilityState = data.stock_visibility_state ?: "",
            codStatus = data.cash_on_delivery ?: "0",
            featuredStatus = data.featured ?: "0",
            todaysDealStatus = data.todays_deal ?: "0",
            flashDiscount = data.flash_discount ?: "",
            flashDiscountType = data.flash_discount_type ?: "",
            categoryName = data.category_name ?: "",
            brandName = data.brand_name ?: "",
            sellerName = data.seller ?: "",
            pickUpName = data.pick_up_points?.map { it.name } ?: emptyList(),
            unitName = data.unit_name ?: "",
            purchaseFormName = data.purchase_from.map { it.name },
            relatedProductsName = data.relatedProducts?.map { it.name } ?: emptyList(),
            upSellingProductsName = data.upSellProducts?.map { it.name } ?: emptyList(),
            crossSellingProductsName = data.crossSellProducts?.map { it.name } ?: emptyList(),
            productStockType = data.product_stock_type ?: "",


            )
    }
}