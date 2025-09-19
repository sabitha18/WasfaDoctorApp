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
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.wasfa.doctor.databinding.FragmentAddproduct4Binding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.Product
import com.wasfa.doctor.ui.images.ImageListActivity


class AddProduct4Fragment : Fragment() {

    private var _binding: FragmentAddproduct4Binding? = null
    private val binding get() = _binding!!
    private var metaTitleRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private var arabicMetaTitleRunnable: Runnable? = null
    private var metaDescRunnable: Runnable? = null
    private var arabicMetaDescRunnable: Runnable? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddproduct4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleEditTexts()
        handleClick()
        manageImage()
        if (AppPreferences.getInstance(requireContext()).getAddOrEditStatus() == "Edit Product"){
            handleEditData()
        }

    }
    private fun handleEditData() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        var product = appPreferences.getAddProduct()

        product?.let {
            println(" empty********************  noooooooooo     " + product?.productName)
            if (it.metaTitla.isNotEmpty()) {
                binding.editMetaTitle.setText(it.metaTitla)
            }
            if (it.arabicMetaTitla.isNotEmpty()) {
                binding.editArabicMetaTitle.setText(it.arabicMetaTitla)
            }
            if (it.metaDesc.isNotEmpty()) {
                binding.editMetaDesc.setText( HtmlCompat.fromHtml(
                    it.metaDesc,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ))
            }
            if (it.arabicMetaDesc.isNotEmpty()) {
                binding.editArabicMetaDesc.setText( HtmlCompat.fromHtml(
                    it.arabicMetaDesc,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ))
            }

        } ?: {
            println(" empty********************" + product?.productName)
        }

    }
    private fun manageImage() {
        val product = AppPreferences.getInstance(requireContext()).getAddProduct()
        binding.txtMetaImageName.text = product?.metaImageUrl
    }

    private fun handleClick() {
        binding.cardMetaImage.setOnClickListener {
            AppPreferences.getInstance(requireContext()).saveImgStatus("seo")
            AppPreferences.getInstance(requireContext()).saveImgSelectionStatus("single")
            val intent = Intent(requireContext(), ImageListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleEditTexts() {
        binding.editMetaTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                metaTitleRunnable?.let { handler.removeCallbacks(it) }

                metaTitleRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product.metaTitla = name
                    prefs.saveAddProduct(product)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(metaTitleRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.editArabicMetaTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                arabicMetaTitleRunnable?.let { handler.removeCallbacks(it) }

                arabicMetaTitleRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.arabicMetaTitla = name
                    prefs.saveAddProduct(product!!)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(arabicMetaTitleRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.editMetaDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                metaDescRunnable?.let { handler.removeCallbacks(it) }

                metaDescRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.metaDesc = name
                    prefs.saveAddProduct(product!!)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(metaDescRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.editArabicMetaDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                arabicMetaDescRunnable?.let { handler.removeCallbacks(it) }

                arabicMetaDescRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product!!)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product!!.arabicMetaDesc= name
                    prefs.saveAddProduct(product!!)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(arabicMetaDescRunnable!!, 500) // save after 500ms delay
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

    override fun onResume() {
        super.onResume()
        manageImage()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        metaTitleRunnable?.let { handler.removeCallbacks(it) }
        metaTitleRunnable = null

        arabicMetaTitleRunnable?.let { handler.removeCallbacks(it) }
        arabicMetaTitleRunnable = null

        metaDescRunnable?.let { handler.removeCallbacks(it) }
        metaDescRunnable = null

        arabicMetaDescRunnable?.let { handler.removeCallbacks(it) }
        arabicMetaDescRunnable = null

    }

}