package com.wasfa.doctor.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentCartDetailBinding
import com.wasfa.doctor.ui.cart.adapter.CartRXDetailAdapter
import com.wasfa.doctor.ui.home.model.Cat

//not using
class CartDetailFragment : Fragment() {
    private var _binding: FragmentCartDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageCart()
        createList()
        handleClick()
    }

    private fun handleClick() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cardAddAddress.setOnClickListener {
            binding.pageAddress.lytAddress.visibility = View.VISIBLE
        }

        binding.pageAddress.imgCloseDate.setOnClickListener {
            binding.pageAddress.lytAddress.visibility = View.GONE
        }
    }

    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Add New product",
                R.drawable.dummy_image
            ),
            Cat(
                "Product Update",
                R.drawable.dummy_image
            )
        )
    }
    private fun manageCart() {
        binding.recyclerRxCart.layoutAnimation =
            android.view.animation.AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        binding.recyclerRxCart.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val catAdapter = CartRXDetailAdapter(createList()) { Cat, position ->

            }
            adapter = catAdapter
        }
    }

}