package com.wasfa.doctor.ui.sales

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
import com.wasfa.doctor.databinding.FragmentSalesBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.sales.adapter.SalesListAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class SalesFragment : Fragment() {
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (AppPreferences.getInstance(requireContext()).getLoginType() == "delivery_boy") {

            if (AppPreferences.getInstance(requireContext()).getFrom() == "login") {
                AppPreferences.getInstance(requireContext()).saveOrderType("Pending Orders")
                findNavController().navigate(R.id.nav_order_list)
            }
            AppPreferences.getInstance(requireContext()).saveFrom("other")
            manageSalesDelivery()
        } else {
            manageSales()
        }

        createList()
        createListDelivery()
        handleClick()
        setViewModel()

        if (AppPreferences.getInstance(requireContext()).getLoginType() == "delivery_boy") {
            binding.imgBack.visibility = View.GONE
            binding.rltCart.visibility = View.GONE
            binding.imgNotif.visibility = View.GONE
        } else {
            binding.imgBack.visibility = View.VISIBLE
            binding.rltCart.visibility = View.VISIBLE
            binding.imgNotif.visibility = View.VISIBLE
        }
    }

    private fun handleClick() {
        binding.imgCart.setOnClickListener {
            findNavController().navigate(R.id.nav_cart)
        }
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
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

        viewModel.emptyCart(appPreferences.getToken().toString())
    }

    private fun manageSalesDelivery() {
        binding.recyclerSales.layoutAnimation =
            android.view.animation.AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_fall_down
            )
        binding.recyclerSales.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = SalesListAdapter(createListDelivery()) { data, position ->
                if (data?.name == "Cash Report") {
                    findNavController().navigate(R.id.nav_cash_report)
                } else {
                    AppPreferences.getInstance(requireContext()).saveOrderType(data?.name)
                    findNavController().navigate(R.id.nav_order_list)
                }

            }
            adapter = catAdapter
        }
    }

    private fun manageSales() {
        binding.recyclerSales.layoutAnimation =
            android.view.animation.AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_fall_down
            )
        binding.recyclerSales.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = SalesListAdapter(createList()) { data, position ->

                AppPreferences.getInstance(requireContext()).saveOrderType(data?.name)
                findNavController().navigate(R.id.nav_sale_details)


            }
            adapter = catAdapter
        }
    }

    private fun createListDelivery(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "All Orders",
                R.drawable.dummy_image
            ),
            Cat(
                "Pending Orders",
                R.drawable.dummy_image
            ),
            Cat(
                "Cash Report",
                R.drawable.dummy_image
            )
        )
    }

    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "All Orders",
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