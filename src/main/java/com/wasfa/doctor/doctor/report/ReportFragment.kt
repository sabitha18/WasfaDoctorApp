package com.wasfa.doctor.doctor.report

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
import android.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentReportBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.doctor.main.DoctorHomeActivity
import com.wasfa.doctor.doctor.report.adapter.FilterClearanceAdapter
import com.wasfa.doctor.doctor.report.adapter.FilterNewAdapter
import com.wasfa.doctor.doctor.report.adapter.ReportAdapter
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: ReportAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var dateFilter: String = ""
    private var itemNameSearch: String = ""
    private var isPharmaceutical: String = ""
    private var clearance: String = ""
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        manageFilterClick()
        manageClick()
        manageSearch()
        setViewModel()
        managePresRecyclerView()
        setUpPagination()
        callReportAPI()
        managePharmaFilter()
        manageClearanceFilter()
        if (!isTablet()) {
            (activity as? DoctorHomeActivity)?.showBottomNav()
        } else {
            (activity as? DoctorHomeActivity)?.hideBottomNav()
        }
    }

    private fun manageClearanceFilter() {
        binding.pageFilter.recyclerFilterByClearance.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterClearanceAdapter(createListClearance()) { data, position ->

                if(data?.name == "Cleared"){
                    clearance = "1"
                }else{
                    clearance = "0"
                }

                binding.pageFilter.txtFilterClearance.text = data?.name
                closeClearance()
            }
            adapter = catAdapter
        }
    }

    private fun managePharmaFilter() {
        binding.pageFilter.recyclerFilterByPharma.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNewAdapter(createListPharma()) { data, position ->


                binding.pageFilter.txtFilterBySeller.text = data?.name?.lowercase()
                closePharma()
            }
            adapter = catAdapter
        }
    }

    private fun closeClearance() {
        binding.pageFilter.imgFilterClearance.rotation = 0f
        binding.pageFilter.cardFilterByClearanceHide.visibility = View.GONE
    }

    private fun closePharma() {
        binding.pageFilter.imgFilterBySeller.rotation = 0f
        binding.pageFilter.cardFilterByPharmaHide.visibility = View.GONE
    }

    private fun createListPharma(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Yes",
                R.drawable.dummy_image
            ),
            Cat(
                "No",
                R.drawable.dummy_image
            )
        )
    }

    private fun createListClearance(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Cleared",
                R.drawable.dummy_image
            ),
            Cat(
                "UnCleared",
                R.drawable.dummy_image
            )
        )
    }

    private fun manageFilterClick() {
        binding.pageFilter.cardFilterClearance.setOnClickListener {
            if (binding.pageFilter.imgFilterClearance.rotation == 0f) {
                binding.pageFilter.imgFilterClearance.rotation = 180f
                binding.pageFilter.cardFilterByClearanceHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgFilterClearance.rotation = 0f
                binding.pageFilter.cardFilterByClearanceHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardFilterPharma.setOnClickListener {
            if (binding.pageFilter.imgFilterBySeller.rotation == 0f) {
                binding.pageFilter.imgFilterBySeller.rotation = 180f
                binding.pageFilter.cardFilterByPharmaHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgFilterBySeller.rotation = 0f
                binding.pageFilter.cardFilterByPharmaHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardApplyFilter.setOnClickListener {
            isPharmaceutical = binding.pageFilter.txtFilterBySeller.text.toString()
            dateFilter = binding.pageFilter.txtFilterByDate.text.toString()
            binding.pageFilter.lytFilter.visibility = View.GONE

            callReportAPI()
        }
        binding.pageFilter.cardClear.setOnClickListener {
            dateFilter = ""
            binding.pageFilter.txtFilterByDate.text = null
            itemNameSearch = ""
            binding.editSearch.text = null
            isPharmaceutical = ""
            binding.pageFilter.txtFilterBySeller.text = null
            clearance = ""
            binding.pageFilter.txtFilterClearance.text = null

            binding.pageFilter.lytFilter.visibility = View.GONE

            callReportAPI()
        }
    }

    private fun manageSearch() {
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newValue = s.toString()
                if (newValue == itemNameSearch) return

                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    itemNameSearch = newValue
                    callReportAPI()
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })
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
                if (viewModel.isLastPageReport()) {
                    // No action for the last page
                } else {
                    viewModel.currentPageReport++
                    viewModel.loadNextPageReport(
                        dateFilter,
                        itemNameSearch,
                        isPharmaceutical,
                        clearance
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

    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    private fun handleDateFilter(type: String) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        var calendar = Calendar.getInstance()

        val date: String
        val rangeString: String

        when (type) {
            "Today" -> {
                date = dateFormat.format(calendar.time)
                rangeString = "$date to $date"
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "Yesterday" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                date = dateFormat.format(calendar.time)
                rangeString = "$date to $date"
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "Last 7 days" -> {
                val end = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, -6)
                val start = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "Last 30 days" -> {
                val end = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, -29)
                val start = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "This month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(calendar.time)
                calendar = Calendar.getInstance()
                val end = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            "Last month" -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(calendar.time)

                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
                val end = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                binding.pageFilter.txtFilterByDate.text = rangeString
            }

            else -> return // unsupported type
        }

    }

    private fun manageClick() {
        binding.imgMenu.setOnClickListener {
            (activity as? DoctorHomeActivity)?.toggleBottomNav()
        }
        binding.cardFilter.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.VISIBLE
        }
        binding.pageFilter.imgClose.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.GONE
        }
        binding.pageFilter.cardFilterDate.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.pageFilter.cardFilterDate)
            popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_today -> {
                        // handle "Today"
                        handleDateFilter("Today")
                        true
                    }

                    R.id.menu_yesterday -> {
                        handleDateFilter("Yesterday")
                        true
                    }

                    R.id.menu_last_7_days -> {
                        handleDateFilter("Last 7 days")
                        true
                    }

                    R.id.menu_last_30_days -> {
                        handleDateFilter("Last 30 days")
                        true
                    }

                    R.id.menu_this_month -> {
                        handleDateFilter("This month")
                        true
                    }

                    R.id.menu_last_month -> {
                        handleDateFilter("Last month")
                        true
                    }

                    R.id.menu_custom_range -> {
                        // show DatePickerDialog or Custom Range Picker
                        showCustomRangePicker()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
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


        viewModel.reportEvent.observe(viewLifecycleOwner) { message ->

            binding.progressBar.visibility = View.GONE
            if (viewModel.totalPageCountReport == 0) {
                println("0-0-0-0-0-                  11   "+viewModel.totalPageCountReport)
                binding.recyclerPres.visibility = View.GONE
                binding.txtNoData.visibility = View.VISIBLE

            } else {
                // Show list
                println("0-0-0-0-0-                  22    " + viewModel.totalPageCountReport)
                binding.recyclerPres.visibility = View.VISIBLE
                binding.txtNoData.visibility = View.GONE
            }

        }
        viewModel.reportData.observe(viewLifecycleOwner) { data ->

            binding.progressBarSmall.visibility = View.GONE
            if (viewModel.currentPageReport == 1) {

                    // Show list
                    println("0-0-0-0-0-                  22"+data?.totalPages)
                    binding.recyclerPres.visibility = View.VISIBLE
                    binding.txtNoData.visibility = View.GONE
                    productAdapter.setProducts(data?.prescriptions?.toMutableList() ?: mutableListOf())




            } else {
                productAdapter.addProducts(data?.prescriptions!!)
            }
            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountReport = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }

        }
    }

    private fun managePresRecyclerView() {

        productAdapter = ReportAdapter(
            mutableListOf()
        ) { product, type ->

        }

        binding.recyclerPres.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = productAdapter
        }

    }

    private fun callReportAPI() {

        viewModel.currentPageReport = 1
        viewModel.clearReportListData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.ReportRequest(
            page_no = "1",
            per_page = "5",
            date = dateFilter,
            itemNameSearch = itemNameSearch,
            isPharmaceutical = isPharmaceutical,
            clearance = clearance
        )
        binding.progressBar.visibility = View.VISIBLE

        viewModel.getReport(appPreferences.getToken().toString(), request)
    }
}