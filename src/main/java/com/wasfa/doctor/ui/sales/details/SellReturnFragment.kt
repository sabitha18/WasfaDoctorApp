package com.wasfa.doctor.ui.sales.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentSellReturnBinding
import com.wasfa.doctor.ui.home.model.Cat


class SellReturnFragment : Fragment() {
    private var _binding: FragmentSellReturnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellReturnBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageProduct()
        handleClick()
    }

    private fun handleClick() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun manageProduct() {

//        binding.recyclerProduct.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//
//            val catAdapter = OrderDetailsAdapter(createList()) { data, position ->
//                findNavController().navigate(R.id.nav_order_details)
//            }
//            adapter = catAdapter
//        }
    }

    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "All Orders",
                R.drawable.dummy_image
            ),
            Cat(
                "Seller Orders",
                R.drawable.dummy_image
            ),
            Cat(
                "Inhouse orders",
                R.drawable.dummy_image
            ),
            Cat(
                "Pending Orders",
                R.drawable.dummy_image
            ),
            Cat(
                "Cancelled Orders",
                R.drawable.dummy_image
            )
        )
    }
}