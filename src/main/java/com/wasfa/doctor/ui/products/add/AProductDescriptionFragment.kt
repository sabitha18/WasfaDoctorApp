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
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.wasfa.doctor.databinding.FragmentAddproductDescriptionBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.Product


class AProductDescriptionFragment : Fragment() {

    private var _binding: FragmentAddproductDescriptionBinding? = null
    private val binding get() = _binding!!
    private var descRunnable: Runnable? = null
    private var arabicDescRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddproductDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
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
            if (it.description.isNotEmpty()) {
                binding.editDesc.setText( HtmlCompat.fromHtml(
                    it.description,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ))
            }
            if (it.arabicDescription.isNotEmpty()) {
                binding.editArabicDesc.setText( HtmlCompat.fromHtml(
                    it.arabicDescription,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ))
            }

        } ?: {
            println(" empty********************" + product?.productName)
        }

    }
    private fun handleEditTexts() {
        binding.editDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                descRunnable?.let { handler.removeCallbacks(it) }

                descRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product.description = name
                    prefs.saveAddProduct(product)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(descRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.editArabicDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                arabicDescRunnable?.let { handler.removeCallbacks(it) }

                arabicDescRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.arabicDescription = name
                    prefs.saveAddProduct(product!!)
                }

                handler.postDelayed(arabicDescRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
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

    private fun handleClick() {

    }
    override fun onDestroyView() {
        super.onDestroyView()

        descRunnable?.let { handler.removeCallbacks(it) }
        descRunnable = null

        arabicDescRunnable?.let { handler.removeCallbacks(it) }
        arabicDescRunnable = null
    }
}