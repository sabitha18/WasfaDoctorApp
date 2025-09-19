package com.wasfa.doctor.ui.pos.rx

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentPOSRxDetailsBinding

class POSRXDetailsFragment : Fragment() {
    private var _binding: FragmentPOSRxDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPOSRxDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        handleClick()
    }

    private fun handleClick() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imgCart.setOnClickListener {
            findNavController().navigate(R.id.nav_pos_shop_cart)
        }

    }

}