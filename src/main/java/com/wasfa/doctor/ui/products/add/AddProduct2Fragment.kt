package com.wasfa.doctor.ui.products.add

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentAddproduct2Binding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.Product
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.products.adapter.ProductStockAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddProduct2Fragment : Fragment() {

    private var _binding: FragmentAddproduct2Binding? = null
    private val binding get() = _binding!!
    private var popupWindow: PopupWindow? = null
    private lateinit var typeAdapter: ProductStockAdapter
    private val productStockList = mutableListOf<Cat>()
    private val handler = Handler(Looper.getMainLooper())
    private var unitRunnable: Runnable? = null
    private var sellerDiscountRunnable: Runnable? = null
    private var apixDiscountRunnable: Runnable? = null
    private var quantityRunnable: Runnable? = null
    private var sellerSkuRunnable: Runnable? = null
    private var externalLinkRunnable: Runnable? = null
    private var externalLinkButtonRunnable: Runnable? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddproduct2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        handleDiscount()
        handleEditTexts()

       if (AppPreferences.getInstance(requireContext()).getAddOrEditStatus() == "Edit Product"){
           handleEditData()
       }

    }
    private fun handleEditData() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        var product = appPreferences.getAddProduct()

        product?.let {
            println(" empty********************  noooooooooo     " + product?.productName)
            if (it.unitPrice.isNotEmpty()) {
                binding.editUnitPrice.setText(it.unitPrice)
            }
            if (it.quantity.isNotEmpty()) {
                binding.editQuantity.setText(it.quantity)
            }
            if (it.discountDateRange.isNotEmpty()) {
                binding.txtDateRange.setText(it.discountDateRange)
            }
            if (it.discount.isNotEmpty()) {
                binding.txtDiscount.text = it.discount
            }
            if (it.discountType.isNotEmpty()) {
                binding.txtDiscountType.text = it.discountType

            }
            if (it.sellerDiscount.isNotEmpty()) {
                binding.editSellerDiscount.setText(it.sellerDiscount)
            }
            if (it.apixDiscount.isNotEmpty()) {
                binding.editApixDiscount.setText(it.apixDiscount)
            }
            if (it.sellerSku.isNotEmpty()) {
                binding.editSellerSku.setText(it.sellerSku)
            }
            if (it.externalLink.isNotEmpty()) {
                binding.editExternalLink.setText(it.externalLink)
            }
            if (it.externalLinkButton.isNotEmpty()) {
              binding.editExternalBtnLink.setText(it.externalLinkButton)
            }

        } ?: {
            println(" empty********************" + product?.productName)
        }

    }
    private fun handleEditTexts() {
        binding.editUnitPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                unitRunnable?.let { handler.removeCallbacks(it) }

                unitRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product.unitPrice = name
                    prefs.saveAddProduct(product)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(unitRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editSellerDiscount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                sellerDiscountRunnable?.let { handler.removeCallbacks(it) }

                sellerDiscountRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.sellerDiscount = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(sellerDiscountRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editApixDiscount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                apixDiscountRunnable?.let { handler.removeCallbacks(it) }

                apixDiscountRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.apixDiscount = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(apixDiscountRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quantityRunnable?.let { handler.removeCallbacks(it) }

                quantityRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.quantity = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(quantityRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editSellerSku.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                sellerSkuRunnable?.let { handler.removeCallbacks(it) }

                sellerSkuRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.sellerSku = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(sellerSkuRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editExternalLink.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                externalLinkRunnable?.let { handler.removeCallbacks(it) }

                externalLinkRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.externalLink = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(externalLinkRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editExternalBtnLink.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                externalLinkButtonRunnable?.let { handler.removeCallbacks(it) }

                externalLinkButtonRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.externalLinkButton = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(externalLinkButtonRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun handleDiscount() {
        val sellerEdit = binding.editSellerDiscount
        val apixEdit = binding.editApixDiscount
        val discountText = binding.txtDiscount

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val sellerValue = sellerEdit.text.toString().trim().toDoubleOrNull() ?: 0.0
                val apixValue = apixEdit.text.toString().trim().toDoubleOrNull() ?: 0.0

                val total = sellerValue + apixValue
                discountText.text = total.toString()
                val prefs = AppPreferences.getInstance(requireContext())
                var product = prefs.getAddProduct() ?: createEmptyProduct()
                product.discount = total.toString()
                prefs.saveAddProduct(product)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        // Attach listener to both EditTexts
        sellerEdit.addTextChangedListener(textWatcher)
        apixEdit.addTextChangedListener(textWatcher)
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
            ),
            Cat(
                "For By Option",
                R.drawable.dummy_image
            )
        )
    }
    private fun handleClick() {
        binding.cardDateRange.setOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Date Range")
                    .build()
            dateRangePicker.show(parentFragmentManager, "DATE_RANGE_PICKER")
            dateRangePicker.addOnPositiveButtonClickListener { selection ->
                val startMillis = selection.first ?: return@addOnPositiveButtonClickListener
                val endMillis = selection.second ?: return@addOnPositiveButtonClickListener

                // Format: dd-MM-yyyy HH:mm:ss
                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val startFormatted = "${sdf.format(Date(startMillis))} 00:00:00"
                val endFormatted = "${sdf.format(Date(endMillis))} 23:59:00"
                val finalDateRange = "$startFormatted to $endFormatted"
                val prefs = AppPreferences.getInstance(requireContext())
                var product = prefs.getAddProduct() ?: createEmptyProduct()
                product.discountDateRange = finalDateRange
                prefs.saveAddProduct(product)
                binding.txtDateRange.text = finalDateRange
                Log.d("DateRange", "Saved: $finalDateRange")
            }
        }
        binding.cardDiscountType.setOnClickListener {
            showDiscountTypeDropdown(binding.cardDiscountType, createList())
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

            currentProduct.discountType = selectedSku.name
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
    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the pending Runnable to prevent crash/memory leak
        unitRunnable?.let { handler.removeCallbacks(it) }
        unitRunnable = null

        sellerDiscountRunnable?.let { handler.removeCallbacks(it) }
        sellerDiscountRunnable = null

        apixDiscountRunnable?.let { handler.removeCallbacks(it) }
        apixDiscountRunnable = null

        quantityRunnable?.let { handler.removeCallbacks(it) }
        quantityRunnable = null

        sellerSkuRunnable?.let { handler.removeCallbacks(it) }
        sellerSkuRunnable = null

        externalLinkRunnable?.let { handler.removeCallbacks(it) }
        externalLinkRunnable = null

        externalLinkButtonRunnable?.let { handler.removeCallbacks(it) }
        externalLinkButtonRunnable = null


    }
}