package com.wasfa.doctor.ui.pos.shop

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentPOSShopDetailsBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class POSShopDetailsFragment : Fragment() {
    private var _binding: FragmentPOSShopDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPOSShopDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        handleClick()
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
            showAlertCustom(message)

        }
        viewModel.cartCount.observe(viewLifecycleOwner) { data ->

            binding.txtCartCount.text = data?.cartCount
            AppPreferences.getInstance(requireContext()).saveCartCount(data?.cartCount)

            if (data?.cartCount == "0"){
                binding.rltCartCount.visibility = View.INVISIBLE
            }else{
                binding.rltCartCount.visibility = View.VISIBLE
            }
        }
        viewModel.addCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            if (message == "added to cart successfully"){
                binding.txtAddCart.text = "Go to Cart"
            }else{
                showAlertCustom(message)
            }
            viewModel.getCartCount(appPreferences.getToken().toString())

        }
        viewModel.productDetailsData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE

            Glide.with(requireContext())
                .load(data?.get(0)?.thumbnailImage)
                .into(binding.imgProduct)

            binding.txtPdName.text = data?.get(0)?.name
            binding.txtPrize.text = data?.get(0)?.unitPrice
            binding.txtBrand.text = data?.get(0)?.brand

            binding.txtDescription.text = HtmlCompat.fromHtml(
                data?.get(0)?.description ?: "",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            if (data?.get(0)?.hasDiscount == "false") {
                binding.txtStrikePrize.visibility = View.GONE
            } else {
                binding.txtStrikePrize.visibility = View.VISIBLE
                binding.txtStrikePrize.apply {
                    text = data?.get(0)?.strikedPrice
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            }



            if (data?.get(0)?.currentStock == "0") {
                binding.cardAddCart.isEnabled = false
                binding.cardAddCart.alpha = 0.5f
            } else {
                binding.cardAddCart.isEnabled = true
                binding.cardAddCart.alpha = 1f
            }
//            if (data?.get(0)?.photos.isNullOrEmpty()) {
//                binding.lytPhotos.visibility = View.GONE
//                binding.imgProduct.visibility = View.VISIBLE
//            } else {
//                managePhotos(data?.get(0)?.photos)
//                binding.lytPhotos.visibility = View.VISIBLE
//                binding.imgProduct.visibility = View.GONE
//
//            }

        }
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.ProductDetailsRequest(
            productId = appPreferences?.getProductId().toString()
        )
        viewModel.getProductDetails(appPreferences.getToken().toString(), request)
        viewModel.getCartCount(appPreferences.getToken().toString())
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

    override fun onResume() {
        super.onResume()
        AppPreferences.getInstance(requireContext()).saveAddressId(null)
        AppPreferences.getInstance(requireContext()).saveCustName(null)
        AppPreferences.getInstance(requireContext()).clearAddress()
    }

    private fun handleClick() {
        binding.imgBack.setOnClickListener {
            val navController = runCatching { findNavController() }.getOrNull()
            if (navController?.currentDestination != null) {
                navController.popBackStack()
            } else {
                parentFragmentManager.popBackStack()
            }
        }


        binding.imgCart.setOnClickListener {
            findNavController().navigate(R.id.nav_pos_shop_cart)
        }

        binding.cardAddCart.setOnClickListener{

            if (binding.txtAddCart.text == "Go to Cart"){
                findNavController().navigate(R.id.nav_pos_shop_cart)
            }else{
                binding.progressBar.visibility = View.VISIBLE

                val request = ApiService.AddCartRXRequest(
                    productId = AppPreferences.getInstance(requireContext()).getProductId().toString(),
                    quantity = "1",
                    patientId = AppPreferences.getInstance(requireContext()).getPatientId().toString(),
                    dose_day = "",
                    description = "",
                    dose = "",
                    course_day = "",
                    dose_time = "",
                    course_duration = "",
                    is_edit = "",
                    prescriptionId = ""
                )
                viewModel.addCartRX(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
            }

        }

    }

}