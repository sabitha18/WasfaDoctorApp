package com.wasfa.doctor.ui.sales.details

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentSalesDetailsBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.DeliveryBoyResponse
import com.wasfa.doctor.network.response.OrderListResponse
import com.wasfa.doctor.ui.filter.adapter.FilterNothingSelectedAdapter
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.pos.adapter.filter.FilterDeliveryBoyAdapter
import com.wasfa.doctor.ui.sales.adapter.OrderListAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class SalesDetailsFragment : Fragment() {
    private var _binding: FragmentSalesDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var orderAdapter: OrderListAdapter
    var searchValue: String = ""
    var area: String = ""
    var date: String = ""
    var order_type: String = ""
    var collected_by_seller: String = ""
    var zones: String = ""
    var payment_change: String = ""
    var payment_method: String = ""
    var payment_status: String = ""
    var collected_by: String = ""
    var delivery_boy: String = ""
    var delivery_status: String = ""
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSalesDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleFilterClick()
        handleClick()
        setViewModel()
        setUpRecyclerView()

        setUpPagination()
        manageSearch()
        manageFilter()



        binding.cardCount.visibility = View.GONE

        if (AppPreferences.getInstance(requireContext()).getOrderType() == "Pending Orders") {
            order_type = "pending"
            binding.txtHeader.text = "Pending Orders"
            callOrderListApi()
        } else if (AppPreferences.getInstance(requireContext())
                .getOrderType() == "Cancelled Orders"
        ) {
            order_type = "cancelled"
            binding.txtHeader.text = "Cancelled Orders"
            callOrderListApi()
        } else {
            order_type = ""
            binding.txtHeader.text = "All Orders"
            callOrderListApi()
        }
    }

    private fun handleFilterClick() {
        binding.pageFilter.cardApplyFilter.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.GONE
            callOrderListApi()
        }
        binding.pageFilter.imgClose.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.GONE
        }
        binding.pageFilter.cardFilterByDate.setOnClickListener {
            if (binding.pageFilter.imgFilterByDateArrow.rotation == 0f) {
                binding.pageFilter.imgFilterByDateArrow.rotation = 180f
                binding.pageFilter.cardFilterByDateHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgFilterByDateArrow.rotation = 0f
                binding.pageFilter.cardFilterByDateHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardFilterPaymentStatus.setOnClickListener {
            if (binding.pageFilter.imgPaymentStatusArrow.rotation == 0f) {
                binding.pageFilter.imgPaymentStatusArrow.rotation = 180f
                binding.pageFilter.cardFilterPaymentStatusHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgPaymentStatusArrow.rotation = 0f
                binding.pageFilter.cardFilterPaymentStatusHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardNothingSelected.setOnClickListener {
            if (binding.pageFilter.imgNothingSelectedArrow.rotation == 0f) {
                binding.pageFilter.imgNothingSelectedArrow.rotation = 180f
                binding.pageFilter.cardNothingSelectedHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgNothingSelectedArrow.rotation = 0f
                binding.pageFilter.cardNothingSelectedHide.visibility = View.GONE
            }
        }

        binding.pageFilter.cardFilterDeliveryStaff.setOnClickListener {
            if (binding.pageFilter.imgDeliveryStaffArrow.rotation == 0f) {
                binding.pageFilter.imgDeliveryStaffArrow.rotation = 180f
                binding.pageFilter.cardDeliveryStaffHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgDeliveryStaffArrow.rotation = 0f
                binding.pageFilter.cardDeliveryStaffHide.visibility = View.GONE
            }
        }

        binding.pageFilter.cardFilterPaymentChange.setOnClickListener {
            if (binding.pageFilter.imgFilterByPc.rotation == 0f) {
                binding.pageFilter.imgFilterByPc.rotation = 180f
                binding.pageFilter.cardFilterByPCHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgFilterByPc.rotation = 0f
                binding.pageFilter.cardFilterByPCHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardFilterPaymentMethod.setOnClickListener {
            if (binding.pageFilter.imgFilterPM.rotation == 0f) {
                binding.pageFilter.imgFilterPM.rotation = 180f
                binding.pageFilter.cardFilterByPMHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgFilterPM.rotation = 0f
                binding.pageFilter.cardFilterByPMHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardClear.setOnClickListener {
            delivery_boy = ""
            delivery_status = ""
            date = ""

            payment_change = ""
            payment_method = ""
            binding.pageFilter.txtFilterNothingSelected.text = "Filter By Delivery"
            binding.pageFilter.txtFilterArea.text = "Filter by Area"
            binding.pageFilter.txtFilterDeliveryStaff.text = "Filter by Delivery Staff"
            binding.pageFilter.txtFilterByDate.text = "Filter by date"
            binding.editSearch.text = null
            searchValue = ""
            binding.pageFilter.txtPaymentStatus.text = "Filter by Payment Status"
            binding.pageFilter.txtFilterByPc.text = "Filter by Payment Change"
            binding.pageFilter.txtFilterPM.text = "Filter by Payment method"
            binding.pageFilter.lytFilter.visibility = View.GONE

            callOrderListApi()
        }
    }

    private fun manageFilter() {

        manageNothingSelected()
        managePaymentStatus()
        manageFilterByDate()
        manageFilterByPaymentChange()
        manageFilterByPaymentMethod()
    }

    private fun manageFilterByPaymentMethod() {
        binding.pageFilter.recyclerFilterByPM.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createPC()) { data, position ->

                binding.pageFilter.txtFilterPM.text = data?.name
                payment_method = data?.name.toString()
                closeFilterByPM()
            }
            adapter = catAdapter
        }
    }

    private fun closeFilterByPM() {
        binding.pageFilter.imgFilterPM.rotation = 0f
        binding.pageFilter.cardFilterByPMHide.visibility = View.GONE
    }

    private fun manageFilterByPaymentChange() {
        binding.pageFilter.recyclerFilterByPC.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createPC()) { data, position ->

                binding.pageFilter.txtFilterByPc.text = data?.name
                payment_change = data?.name.toString()
                closeFilterByPC()
            }
            adapter = catAdapter
        }
    }

    private fun closeFilterByPC() {
        binding.pageFilter.imgFilterByPc.rotation = 0f
        binding.pageFilter.cardFilterByPCHide.visibility = View.GONE
    }

    private fun manageFilterByDate() {
        binding.pageFilter.recyclerFilterByDate.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createPD()) { data, position ->

                binding.pageFilter.txtFilterByDate.text = data?.name
                handleDateFilter(data?.name.toString())
                closeFilterByDate()
            }
            adapter = catAdapter
        }
    }

    private fun handleDateFilter(type: String) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        var calendar = Calendar.getInstance()

        val dateNew: String
        val rangeString: String

        when (type) {
            "Today" -> {
                dateNew = dateFormat.format(calendar.time)
                rangeString = "$dateNew to $dateNew"
                date = rangeString
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "Yesterday" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                dateNew = dateFormat.format(calendar.time)
                rangeString = "$dateNew to $dateNew"
                date = rangeString
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "Last 7 Days" -> {
                val end = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, -6)
                val start = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                date = rangeString
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "Last 30 Days" -> {
                val end = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, -29)
                val start = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                date = rangeString
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "This Month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(calendar.time)
                calendar = Calendar.getInstance()
                val end = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                date = rangeString
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "Last Month" -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(calendar.time)

                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
                val end = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                date = rangeString
                binding.pageFilter.txtFilterByDate.text = rangeString
            }
            "Custom Range" -> {
                showCustomRangePicker()
            }

            else -> return // unsupported type
        }
    }
    private fun showCustomRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val picker = builder.build()

        picker.show(childFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener { selection ->
            val startMillis = selection.first ?: 0L
            val endMillis = selection.second ?: 0L

            // Format to "dd-MM-yyyy"
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val startDate = formatter.format(Date(startMillis))
            val endDate = formatter.format(Date(endMillis))

            val rangeString = "$startDate to $endDate"
            binding.pageFilter.txtFilterByDate.text = rangeString

            // Show progress
            binding.progressBar.visibility = View.VISIBLE

            // Call API
            val request = ApiService.GraphRequest(
                date = rangeString
            )
            viewModel.getGraph(
                AppPreferences.getInstance(requireContext()).getToken().toString(),
                request
            )
            val request1 = ApiService.HomeRequest(
                date = rangeString
            )
            viewModel.getHomeData(AppPreferences.getInstance(requireContext()).getToken().toString(),request1)
        }
    }
    private fun closeFilterByDate() {
        binding.pageFilter.imgFilterByDateArrow.rotation = 0f
        binding.pageFilter.cardFilterByDateHide.visibility = View.GONE
    }

    private fun managePaymentStatus() {
        binding.pageFilter.recyclerPaymentStatus.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createPS()) { data, position ->

                binding.pageFilter.txtPaymentStatus.text = data?.name
                payment_status = data?.name.toString()
                closePaymentStatus()
            }
            adapter = catAdapter
        }
    }

    private fun closePaymentStatus() {
        binding.pageFilter.imgPaymentStatusArrow.rotation = 0f
        binding.pageFilter.cardFilterPaymentStatusHide.visibility = View.GONE
    }

    private fun manageNothingSelected() {

        binding.pageFilter.recyclerNothingSelected.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createListNothing()) { data, position ->

                binding.pageFilter.txtFilterNothingSelected.text = data?.name
                delivery_status = data?.name.toString()
                closeNothingSelected()
            }
            adapter = catAdapter
        }

    }

    private fun closeNothingSelected() {
        binding.pageFilter.imgNothingSelectedArrow.rotation = 0f
        binding.pageFilter.cardNothingSelectedHide.visibility = View.GONE
    }

    private fun createListNothing(): ArrayList<Cat> {
        return arrayListOf<Cat>(

            Cat(
                "Pending",
                R.drawable.dummy_image
            ),
            Cat(
                "Confirmed",
                R.drawable.dummy_image
            ),
            Cat(
                "Picked Up",
                R.drawable.dummy_image
            ),
            Cat(
                "On The Way",
                R.drawable.dummy_image
            ),
            Cat(
                "Delivered",
                R.drawable.dummy_image
            ),
            Cat(
                "Closed",
                R.drawable.dummy_image
            ),
            Cat(
                "Cancel",
                R.drawable.dummy_image
            )
        )
    }

    private fun createPS(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Un-Paid",
                R.drawable.dummy_image
            ),
            Cat(
                "Paid",
                R.drawable.dummy_image
            ),
            Cat(
                "Processed",
                R.drawable.dummy_image
            )
        )
    }

    private fun createPC(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Knet",
                R.drawable.dummy_image
            ),
            Cat(
                "Quick Pay",
                R.drawable.dummy_image
            ),
            Cat(
                "Go Tap",
                R.drawable.dummy_image
            ),
            Cat(
                "COD",
                R.drawable.dummy_image
            )
        )
    }

    private fun createPD(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Today",
                R.drawable.dummy_image
            ),
            Cat(
                "Yesterday",
                R.drawable.dummy_image
            ),
            Cat(
                "Last 7 Days",
                R.drawable.dummy_image
            ),
            Cat(
                "Last 30 Days",
                R.drawable.dummy_image
            ),
            Cat(
                "This Month",
                R.drawable.dummy_image
            ),
            Cat(
                "Last Month",
                R.drawable.dummy_image
            ),
            Cat(
                "Custom Range",
                R.drawable.dummy_image
            )
        )
    }

    private fun manageSearch() {
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    searchValue = s.toString()
                    callOrderListApi()
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })

    }

    private fun setUpPagination() {
        binding.recyclerList.isNestedScrollingEnabled = false
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
                        searchValue,
                        order_type,
                        area,
                        zones,
                        payment_status,
                        payment_change,
                        payment_method,
                        delivery_status,
                        delivery_boy,
                        date,
                        collected_by_seller,
                        collected_by,
                        "",
                        "",
                        ""
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
        orderAdapter = OrderListAdapter(
            mutableListOf(),
            { product, type ->
                if (type == "view") {
                    AppPreferences.getInstance(requireContext()).saveOrderID(product?.id)
                    findNavController().navigate(R.id.nav_order_details)
                } else if (type == "return") {
                    findNavController().navigate(R.id.nav_sell_return)
                }
            },
            { product, status ->
                showDeliveryStatusDialog(product?.deliveryStatus.toString(), product?.id.toString())

            }
        )


        binding.recyclerList.apply {
            if (isTablet()){
                layoutManager = GridLayoutManager(requireContext(), 3)

            }else{
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            }

            adapter = orderAdapter
        }
    }
    private fun showDeliveryStatusDialog(deliveryStatus: String, id: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_admin_change_delivery, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radio_group_status)
        val btnChange = dialogView.findViewById<MaterialCardView>(R.id.card_change)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        fun setButtonEnabled(enabled: Boolean) {
            btnChange.isEnabled = enabled
            btnChange.alpha = if (enabled) 1f else 0.5f
        }

        setButtonEnabled(false)

        imgClose.setOnClickListener {
            dialog.dismiss()
        }


        val currentStatus = deliveryStatus

        val checkedId = when (currentStatus) {
            "pending" -> R.id.radio_pending
            "confirmed" -> R.id.radio_confirmed
            "picked_up" -> R.id.radio_picked_up
            "on_the_way" -> R.id.radio_on_the_way
            "delivered" -> R.id.radio_delivered
            "cancelled" -> R.id.radio_cancel
            "closed" -> R.id.radio_closed
            else -> -1
        }

        if (checkedId != -1) {
            radioGroup.check(checkedId)
            setButtonEnabled(true)
        }

        radioGroup.setOnCheckedChangeListener { _, id ->
            setButtonEnabled(id != -1)
        }

        btnChange.setOnClickListener {
            if (!btnChange.isEnabled) return@setOnClickListener

            val selectedStatus = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_pending -> "pending"
                R.id.radio_confirmed -> "confirmed"
                R.id.radio_picked_up -> "picked_up"
                R.id.radio_on_the_way -> "on_the_way"
                R.id.radio_delivered -> "delivered"
                R.id.radio_cancel -> "cancelled"
                R.id.radio_closed -> "closed"
                else -> null
            }

            selectedStatus?.let {
                dialog.dismiss()
                updateDeliveryStatus(id, it)
            }
        }

        dialog.show()
    }
    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    private fun updateDeliveryStatus(id: String, status: String) {

        val request = ApiService.UpdateDeliveryStatusRequest(
            id = id,
            status = status
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.updateDeliveryStatus(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )
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
        viewModel.updatePaymentStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            callOrderListApi()
        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarSmall.visibility = View.GONE

        }
        viewModel.deliveryBoyData.observe(viewLifecycleOwner) { data ->

            manageFilterDeliveryBoys(data)


        }
        viewModel.orderListData.observe(viewLifecycleOwner) { data ->

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


        viewModel.getDeliveryBoys(appPreferences.getToken().toString())

    }

    private fun manageFilterDeliveryBoys(data: List<DeliveryBoyResponse>?) {
        binding.pageFilter.recyclerDeliveryStaff.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterDeliveryBoyAdapter(data) { data, position ->

                delivery_boy = data?.id.toString()
                binding.pageFilter.txtFilterDeliveryStaff.text = data?.name
                closeDeliveryBoy()
            }
            adapter = catAdapter
        }
    }

    private fun closeDeliveryBoy() {
        binding.pageFilter.imgDeliveryStaffArrow.rotation = 0f
        binding.pageFilter.cardFilterDeliveryStaff.visibility = View.GONE
    }

    private fun callOrderListApi() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.currentPageOrder = 1
        viewModel.clearOrderData()
        val isPrescription =
            if (AppPreferences.getInstance(requireContext()).getIsPres() == "true") "1" else "0"

        val request = ApiService.OrderListRequest(
            area = area,
            per_page = "3",
            page_no = "1",
            search = searchValue,
            date = date,
            order_type = order_type,
            collected_by_seller = collected_by_seller,
            zones = zones,
            payment_change = payment_change,
            payment_method = payment_method,
            payment_status = payment_status,
            collected_by = collected_by,
            delivery_boy = delivery_boy,
            delivery_status = delivery_status,
            userId = "",
            IsPrescription = "",
            driverCleared = ""

        )
        viewModel.getOrderList(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }

    private fun handleClick() {
        binding.cardFilter.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.VISIBLE
        }
        binding.pageFilter.imgClose.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.GONE
        }

        binding.imgBack.setOnClickListener {
            val navController = runCatching { findNavController() }.getOrNull()
            if (navController?.currentDestination != null) {
                navController.popBackStack()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun manageSales(data: OrderListResponse) {
        binding.recyclerList.visibility = View.VISIBLE
        orderAdapter.setOrder(data?.orders?.toMutableList() ?: mutableListOf())
    }


}