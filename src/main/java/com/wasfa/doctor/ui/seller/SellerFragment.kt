package com.wasfa.doctor.ui.seller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentSellerBinding
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.seller.adapter.SellerListAdapter


class SellerFragment : Fragment() {
    private var _binding: FragmentSellerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageSeller()
        createList()
        handleClick()
    }
    private fun handleClick() {
        binding.imgCart.setOnClickListener {
            findNavController().navigate(R.id.nav_cart)
        }
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun manageSeller() {
        binding.recyclerList.layoutAnimation =
            android.view.animation.AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_fall_down
            )
        binding.recyclerList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = SellerListAdapter(createList()) { data, position ->
                findNavController().navigate(R.id.nav_seller_apix)
            }
            adapter = catAdapter
        }
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