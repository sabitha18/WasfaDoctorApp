package com.wasfa.doctor.ui.products

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentProductBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.products.adapter.ProductListAdapter
import com.wasfa.doctor.ui.products.add.AddProductActivity
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        manageProduct()
        createList()
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

        viewModel.cartCount.observe(viewLifecycleOwner) { data ->

            binding.txtCartCount.text = data?.cartCount
            AppPreferences.getInstance(requireContext()).saveCartCount(data?.cartCount)

            if (data?.cartCount == "0"){
                binding.rltCartCount.visibility = View.INVISIBLE
            }else{
                binding.rltCartCount.visibility = View.VISIBLE
            }
        }

        viewModel.getCartCount(appPreferences.getToken().toString())

    }
    private fun handleClick() {
        binding.imgCart.setOnClickListener {
            findNavController().navigate(R.id.nav_cart)
        }
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun manageProduct() {
        binding.recyclerProduct.layoutAnimation =
            android.view.animation.AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        binding.recyclerProduct.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = ProductListAdapter(createList()) { data, position ->

                if (data?.name == "Add New product") {
                    AppPreferences.getInstance(requireContext()).saveAddOrEditStatus("Add New Product")

                    AppPreferences.getInstance(requireContext()).clearAddProduct()
                    val intent = Intent(requireContext(), AddProductActivity::class.java)
                    startActivity(intent)

                  // findNavController().navigate(R.id.nav_add_product)
                }else  {
                    AppPreferences.getInstance(requireContext()).savePDType(data?.name)
                    findNavController().navigate(R.id.nav_all_product)
                }
            }
            adapter = catAdapter
        }
    }

    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Add New product",
                R.drawable.dummy_image
            ),
            Cat(
                "All Products",
                R.drawable.dummy_image
            )
        )
    }
}