package com.wasfa.doctor.ui.products.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentAddproductPdfBinding
import com.wasfa.doctor.helper.AppPreferences


class AProductPDFFragment : Fragment() {

    private var _binding: FragmentAddproductPdfBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddproductPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        binding.txtHeader.text = AppPreferences.getInstance(requireContext()).getAddOrEditStatus().toString()

    }

    private fun handleClick() {
        binding.imgCart.setOnClickListener {
            findNavController().navigate(R.id.nav_cart)
        }
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cardBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cardNext.setOnClickListener {

            findNavController().navigate(R.id.nav_add_product_5)
        }
    }
}