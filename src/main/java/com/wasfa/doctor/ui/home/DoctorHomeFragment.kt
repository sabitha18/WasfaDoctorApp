package com.wasfa.doctor.ui.home

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentDoctorHomeBinding
import com.wasfa.doctor.ui.home.adapter.SliderAdapter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import com.wasfa.doctor.ui.details.MedDetailsFragment
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.GraphResponse
import com.wasfa.doctor.network.response.PresHome
import com.wasfa.doctor.network.response.TopSellProd
import com.wasfa.doctor.ui.home.adapter.RecentPresListAdapter
import com.wasfa.doctor.ui.home.adapter.TopSellingPDAdapter
import com.wasfa.doctor.ui.main.DoctorHomeActivity
import com.wasfa.doctor.ui.pres.PresDetailsFragment
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.response.UserPermissionsResponse
import com.wasfa.doctor.ui.login.LoginActivity
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DoctorHomeFragment : Fragment() {
    private var _binding: FragmentDoctorHomeBinding? = null
    private val binding get() = _binding!!
    private var currentPage = 0
    private val autoSlideDelay = 3000L
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var viewModel: HomeViewModel
    private val snapHelper = LinearSnapHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        manageClick()
        (activity as? DoctorHomeActivity)?.setDefault()


    }

    private fun manageClick() {
        binding.imgFilterReset.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.txtFilterByDate.text = null
            val request = ApiService.GraphRequest(
                date = ""
            )
            viewModel.getGraph(
                AppPreferences.getInstance(requireContext()).getToken().toString(),
                request
            )
            binding.imgFilterReset.visibility = View.GONE
        }
        binding.cardFilterByDate.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.cardFilterByDate)
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
        binding.imgMenu.setOnClickListener {
                (activity as? DoctorHomeActivity)?.toggleSideMenu()
        }
        binding.imgMenuTab.setOnClickListener {

                (activity as? DoctorHomeActivity)?.hideSideMenu()
                (activity as? DoctorHomeActivity)?.toggleBottomNav()
        }
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

                binding.txtFilterByDate.text = rangeString
            }

            "Yesterday" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                date = dateFormat.format(calendar.time)
                rangeString = "$date to $date"
                binding.txtFilterByDate.text = rangeString
            }

            "Last 7 days" -> {
                val end = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, -6)
                val start = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                binding.txtFilterByDate.text = rangeString
            }

            "Last 30 days" -> {
                val end = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, -29)
                val start = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                binding.txtFilterByDate.text = rangeString
            }

            "This month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(calendar.time)
                calendar = Calendar.getInstance()
                val end = dateFormat.format(calendar.time)
                rangeString = "$start to $end"
                binding.txtFilterByDate.text = rangeString
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
                binding.txtFilterByDate.text = rangeString
            }

            else -> return // unsupported type
        }
        binding.imgFilterReset.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.GraphRequest(
            date = rangeString
        )
        viewModel.getGraph(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
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
            binding.txtFilterByDate.text = rangeString

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
        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }

        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.emptyCartStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            println("-----------------   ****  0000000000000" + message)
            var email = ""
            var pass = ""
            var rememberStatus = ""
            if (appPreferences?.getRememberStatus().toString() == "true") {
                email = appPreferences.getEmail().toString()
                pass = appPreferences.getPass().toString()
                rememberStatus = "true"
            }

            appPreferences.clearAllPreferences()

            if (rememberStatus == "true") {
                appPreferences.saveEmail(email)
                appPreferences.savePass(pass)
                appPreferences.saveRememberStatus("true")
            }

            Toast.makeText(
                requireContext(),
                "Session Expired. Please Login Again !!  ",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

        }
        viewModel.graphData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            setupLineChart(data)
            binding.txtSalesAnalytics.text =
                "KD %.3f".format((data?.currentMonthAmount?.toString()?.toDoubleOrNull() ?: 0.0))
            val dashboardList = createDashboardListGraph(data)
            manageSlider(dashboardList)


        }


        viewModel.homeData.observe(viewLifecycleOwner) { data ->
            println("--------------------------")
            binding.progressBar.visibility = View.GONE


            binding.txtSalesAnalytics.text =
                "KD %.3f".format((data?.currentMonthAmount?.toString()?.toDoubleOrNull() ?: 0.0))

            if (!isTablet()) {
                (activity as? DoctorHomeActivity)?.showBottomNav()
            } else {
                (activity as? DoctorHomeActivity)?.hideBottomNav()
            }


//            val dashboardList = createDashboardList(data)
//            manageSlider(dashboardList)
            manageTopSelling(data?.topSellingProducts)
            managePres(data?.prescriptions)
        }

        binding.progressBar.visibility = View.VISIBLE
        val request1 = ApiService.HomeRequest(
            date = ""
        )
        viewModel.getHomeData(appPreferences.getToken().toString(), request1)
        val request = ApiService.GraphRequest(
            date = ""
        )
        viewModel.getGraph(appPreferences.getToken().toString(), request)

        viewModel.getUserPermissions(appPreferences.getToken().toString())

        viewModel.userPermissionList.observe(viewLifecycleOwner) { data ->
            appPreferences.saveStaffPermissions(data.permissions)
            appPreferences.saveDoctorType(data?.newPosRx)
            (activity as? DoctorHomeActivity)?.updatePermissionsUI()

        }
    }

    private fun createDashboardListGraph(data: GraphResponse): List<DashboardItem> {
        return listOf(
            DashboardItem("Points", data.amount.toString()),
            DashboardItem("RX Count", data.countOfSale.toString()),
            DashboardItem("Pending Points", data.pendingCommission),
            DashboardItem("Redeemed Points", data.completedPayment),
//            DashboardItem("Refund orders and Amount", data.refundOrders.toString()),
        )
    }

    private fun managePres(prescriptions: List<PresHome>?) {
        if (prescriptions.isNullOrEmpty()) {
            binding.txtPres.visibility = View.GONE
        } else {
            binding.txtPres.visibility = View.VISIBLE
        }
        binding.recyclerRecentPres.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val catAdapter = RecentPresListAdapter(prescriptions) { data, type ->
                AppPreferences.getInstance(requireContext()).savePresID(data?.id.toString())
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,
                        com.wasfa.doctor.ui.pres.PresDetailsFragment()
                    )
                    .addToBackStack(null)
                    .commit()
            }
            adapter = catAdapter
        }
    }

    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    private fun manageTopSelling(data: List<TopSellProd>?) {
        if (data.isNullOrEmpty()) {
            binding.txtTopSell.visibility = View.GONE
        } else {
            binding.txtTopSell.visibility = View.VISIBLE
        }
        binding.recyclerSellingPd.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val catAdapter = TopSellingPDAdapter(data) { data, type ->
                AppPreferences.getInstance(requireContext()).saveProductId(data?.id)
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        MedDetailsFragment()
                    )
                    .addToBackStack(null)
                    .commit()
            }
            adapter = catAdapter
        }
    }

    private fun setupLineChart(data: GraphResponse) {
        val lineChart = binding.chartNoOfSales // Ensure you have a LineChart in XML

        val entries = mutableListOf<Entry>()
        data.num_of_sale_value.forEachIndexed { index, value ->
            entries.add(Entry(index.toFloat(), value.toFloatOrNull() ?: 0f))
        }

        val dataSet = LineDataSet(entries, "Sample Data")

        // Enable cubic bezier mode for a smooth wave-like curve
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        // Enable gradient fill
        dataSet.setDrawFilled(true)

        // Set the gradient drawable
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient)
        dataSet.fillDrawable = drawable

        // Customizing line chart appearance
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.doctor_main_color)

        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 2f
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Configure X-Axis
        val xAxis = lineChart.xAxis
        xAxis.setDrawGridLines(false)  // No vertical grid lines
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true)  // ✅ Show X-axis labels
        xAxis.granularity = 1f // Ensure labels are shown for each point
        xAxis.textColor = Color.BLACK // Customize text color if needed
        xAxis.textSize = 12f

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index in data.weekdays.indices) data.weekdays[index].substring(5) else "" // e.g. "04-01"
            }
        }

        val yAxis = lineChart.axisLeft
        yAxis.setDrawLabels(true)
        yAxis.setDrawAxisLine(false)
        yAxis.setDrawGridLines(true)
        yAxis.gridColor = ContextCompat.getColor(requireContext(), R.color.grid_color)
        yAxis.gridLineWidth = 1f
        yAxis.enableGridDashedLine(10f, 10f, 0f)

// Set custom range and granularity
        yAxis.axisMinimum = 0f      // Start at 0.0
        yAxis.axisMaximum = data.num_of_sale_value.maxOrNull()?.toFloat()?.plus(0.5f) ?: 2f
        yAxis.granularity = 0.1f    // Step every 0.1
        yAxis.labelCount = 11       // Total labels: 0.0 to 1.0

// Optional: format to show 1 decimal place
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.1f", value)
            }
        }
        lineChart.axisRight.isEnabled = false

        val marker = CustomMarkerView(requireContext(), R.layout.custom_marker, data.weekdays)
        marker.chartView = lineChart
        lineChart.marker = marker


        // Hide Legend and Description
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false

        // Refresh the chart
        lineChart.invalidate()
    }

    private fun manageSlider(list: List<DashboardItem>) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerSlider.layoutManager = layoutManager
        val sliderAdapter = SliderAdapter(requireContext(), list)
        binding.recyclerSlider.adapter = sliderAdapter

        if (binding.recyclerSlider.onFlingListener == null) {
            snapHelper.attachToRecyclerView(binding.recyclerSlider)
        }


        // Setup dot indicators
        setupDotIndicators(list.size)

        // Scroll listener for updating dot indicator
        binding.recyclerSlider.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val snapView = snapHelper.findSnapView(layoutManager)
                val position = snapView?.let { layoutManager.getPosition(it) } ?: 0
                updateDotIndicator(position)
                currentPage = position
            }
        })

        // Auto-scroll setup
//        handler = Handler(Looper.getMainLooper())
//        runnable = Runnable {
//            if (currentPage >= list.size) {
//                currentPage = 0 // Reset to the first item
//            }
//            binding.recyclerSlider.smoothScrollToPosition(currentPage++)
//            handler.postDelayed(runnable, autoSlideDelay)
//        }
//
//        handler.postDelayed(runnable, autoSlideDelay)
    }

    private fun stopAutoSlider() {
        handler.removeCallbacks(runnable)
    }

    private fun setupDotIndicators(count: Int) {
        binding.dotContainer.removeAllViews()
        for (i in 0 until count) {
            val dot = ImageView(requireContext())
            dot.setImageResource(R.drawable.inactive_icon)

            val params = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.inactive_dot_width),  // Start with inactive width
                resources.getDimensionPixelSize(R.dimen.dot_height)
            )
            dot.setPadding(4, 0, 4, 0)
            binding.dotContainer.addView(dot, params)
        }

        // Make the first dot active by default
        if (binding.dotContainer.childCount > 0) {
            val firstDot = binding.dotContainer.getChildAt(0) as ImageView
            firstDot.setImageResource(R.drawable.active_icon)
            firstDot.layoutParams.width = resources.getDimensionPixelSize(R.dimen.active_dot_width)
            firstDot.requestLayout()
        }
    }

    private fun updateDotIndicator(position: Int) {
        for (i in 0 until binding.dotContainer.childCount) {
            val dot = binding.dotContainer.getChildAt(i) as ImageView

            // Adjust width based on active/inactive state
            val newWidth =
                if (i == position) R.dimen.active_dot_width else R.dimen.inactive_dot_width
            dot.layoutParams.width = resources.getDimensionPixelSize(newWidth)
            dot.requestLayout()

            // Update icon
            dot.setImageResource(if (i == position) R.drawable.active_icon else R.drawable.inactive_icon)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()


        if (this::runnable.isInitialized && this::handler.isInitialized) {
            handler.removeCallbacks(runnable)
        }

        if (this::handler.isInitialized) {
            handler.removeCallbacks(runnable)
        }
        _binding = null
    }
}