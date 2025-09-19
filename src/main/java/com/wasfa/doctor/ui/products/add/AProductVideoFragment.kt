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
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentAddproductVideoBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.Product
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.products.adapter.ProductStockAdapter


class AProductVideoFragment : Fragment() {

    private var _binding: FragmentAddproductVideoBinding? = null
    private val binding get() = _binding!!
    private lateinit var typeAdapter: ProductStockAdapter
    private var popupWindow: PopupWindow? = null
    private var videoRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddproductVideoBinding.inflate(inflater, container, false)
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
            if (it.videoType.isNotEmpty()) {
                binding.txtType.setText(it.videoType)
            }
            if (it.videoLink.isNotEmpty()) {
                binding.editVideoLink.setText(it.videoLink)
            }


        } ?: {
            println(" empty********************" + product?.productName)
        }

    }
    private fun handleEditTexts() {
        binding.editVideoLink.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                videoRunnable?.let { handler.removeCallbacks(it) }

                videoRunnable = Runnable {
                    val name = s.toString().trim()
                    val prefs = AppPreferences.getInstance(requireContext())
                    var product = prefs.getAddProduct()
                    if (product == null) {
                        product = createEmptyProduct()
                        prefs.saveAddProduct(product)
                        Log.d("ProductSave", "Initialized and saved empty product")
                    }

                    product.videoLink = name
                    prefs.saveAddProduct(product)
                    Log.d("ProductSave", "Saved product name: $name")
                }

                handler.postDelayed(videoRunnable!!, 500) // save after 500ms delay
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
    private fun handleClick() {
        binding.cardVideoType.setOnClickListener {
            showTypeDropdown(binding.cardVideoType, createList())
        }
    }
    private fun showTypeDropdown(anchorView: View, skuList: MutableList<Cat>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindow =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val edtSearch = popupView.findViewById<MaterialCardView>(R.id.card_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.visibility = View.GONE
        typeAdapter = ProductStockAdapter { selectedSku ->
            // Handle selection
            binding.txtType.text = selectedSku.name
            val prefs = AppPreferences.getInstance(requireContext())
            var currentProduct = prefs.getAddProduct()

            if (currentProduct == null) {
                currentProduct = createEmptyProduct()
                Log.d("ProductSave", "Initialized empty product for stock type save")
            }

            currentProduct.videoType = selectedSku.name
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
    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Youtube",
                R.drawable.dummy_image
            ),
            Cat(
                "Dailymotion",
                R.drawable.dummy_image
            ),
            Cat(
                "Vimeo",
                R.drawable.dummy_image
            )
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()

        videoRunnable?.let { handler.removeCallbacks(it) }
        videoRunnable = null

    }
    }