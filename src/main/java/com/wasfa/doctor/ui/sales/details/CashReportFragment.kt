package com.wasfa.doctor.ui.sales.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfa.doctor.databinding.FragmentCashReportBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.OrderListResponse
import com.wasfa.doctor.ui.sales.adapter.CashReportAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class CashReportFragment : Fragment() {
    private var _binding: FragmentCashReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var orderAdapter: CashReportAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCashReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClick()
        setViewModel()
        callOrderListApi()
        setUpRecyclerView()
        setUpPagination()
    }

    private fun callOrderListApi() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.currentPageOrder = 1
        viewModel.clearOrderData()
        val isPrescription =
            if (AppPreferences.getInstance(requireContext()).getIsPres() == "true") "1" else "0"

        val request = ApiService.OrderListRequest(
            area = "",
            per_page = "10",
            page_no = "1",
            search = "",
            date = "",
            order_type = "",
            collected_by_seller = "",
            zones = "",
            payment_change = "",
            payment_method = "",
            payment_status = "",
            collected_by = "",
            delivery_boy = "",
            delivery_status = "",
            userId = "",
            IsPrescription = "",
            driverCleared = "1"

        )
        viewModel.getOrderList(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }

    private fun setUpPagination() {
        binding.recyclerPres.isNestedScrollingEnabled = false
        scrollListener = ViewTreeObserver.OnScrollChangedListener {
            if (_binding == null) return@OnScrollChangedListener

            val view =
                binding.nestedScrollView.getChildAt(binding.nestedScrollView.childCount - 1) as View
            val diff: Int =
                view.bottom - (binding.nestedScrollView.height + binding.nestedScrollView.scrollY)
            if (diff == 0) {
                if (viewModel.isLastPageOrder()) {
                    // No action for the last page
                } else {
                    val isPrescription = if (AppPreferences.getInstance(requireContext())
                            .getIsPres() == "true"
                    ) "1" else "0"

                    viewModel.currentPageOrder++
                    viewModel.loadNextPageOrder(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "0"
                    )

                    if (binding.progressBar.visibility == View.VISIBLE) {
                        binding.progressBarSmall.visibility = View.GONE
                    } else {
                        binding.progressBarSmall.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.nestedScrollView.viewTreeObserver.addOnScrollChangedListener(scrollListener)

        // Clean up the listener when the view is destroyed
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                binding.nestedScrollView.viewTreeObserver.removeOnScrollChangedListener(
                    scrollListener
                )
            }
        })
    }

    private fun setUpRecyclerView() {
        orderAdapter = CashReportAdapter(
            mutableListOf(),
            { product, type ->

            }
        )


        binding.recyclerPres.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


            adapter = orderAdapter
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
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarSmall.visibility = View.GONE

        }
        viewModel.cashAmountData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            binding.txtTotalCashAmount.text = "Total Cash Amount: " + data?.total

        }
        viewModel.orderListData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountOrder = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPageOrder == 1) {

                manageSales(data)
            } else {
                orderAdapter.addOrder(data?.orders!!)
            }

        }


        viewModel.getTotalCashCleared(appPreferences.getToken().toString())

    }

    private fun manageSales(data: OrderListResponse) {
        binding.recyclerPres.visibility = View.VISIBLE
        orderAdapter.setOrder(data?.orders?.toMutableList() ?: mutableListOf())
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
    }
}