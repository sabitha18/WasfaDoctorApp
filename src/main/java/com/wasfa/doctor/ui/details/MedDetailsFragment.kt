package com.wasfa.doctor.ui.details

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.FragmentMedDetailsBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class MedDetailsFragment : Fragment() {
    private var _binding: FragmentMedDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMedDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        handleClick()
    }

    private fun handleClick() {
        binding.cardBack.setOnClickListener {
            parentFragmentManager.popBackStack()
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
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

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


        }
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.ProductDetailsRequest(
            productId = appPreferences?.getProductId().toString()
        )
        viewModel.getProductDetails(appPreferences.getToken().toString(), request)
    }
}