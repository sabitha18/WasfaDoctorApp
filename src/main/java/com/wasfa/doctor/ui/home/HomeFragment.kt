package com.wasfa.doctor.ui.home

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.datepicker.MaterialDatePicker
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentHomeBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.HomeDataResponse
import com.wasfa.doctor.network.response.HourSales
import com.wasfa.doctor.network.response.Influencer
import com.wasfa.doctor.network.response.LastSearchTerm
import com.wasfa.doctor.network.response.MonthData
import com.wasfa.doctor.network.response.MostViewed
import com.wasfa.doctor.network.response.NewCustomers
import com.wasfa.doctor.network.response.NewDoctors
import com.wasfa.doctor.network.response.NewVendors
import com.wasfa.doctor.network.response.RecentOrders
import com.wasfa.doctor.network.response.TopSearches
import com.wasfa.doctor.network.response.TopSellProd
import com.wasfa.doctor.ui.home.adapter.BestSeller2HomeAdapter
import com.wasfa.doctor.ui.home.adapter.BestSellerHomeAdapter
import com.wasfa.doctor.ui.home.adapter.InfluencerHomeAdapter
import com.wasfa.doctor.ui.home.adapter.LastSearchHomeAdapter
import com.wasfa.doctor.ui.home.adapter.NewCustHomeAdapter
import com.wasfa.doctor.ui.home.adapter.NewVendorHomeAdapter
import com.wasfa.doctor.ui.home.adapter.RecentOrderHomeAdapter
import com.wasfa.doctor.ui.home.adapter.TopSearchHomeAdapter
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.main.MainActivity
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        AppPreferences.getInstance(requireContext()).saveAddressId(null)
        AppPreferences.getInstance(requireContext()).saveCustName(null)
        AppPreferences.getInstance(requireContext()).clearAddress()
        val appPreferences = AppPreferences.getInstance(requireContext())
        appPreferences.saveCatImgId(null)
        appPreferences.saveOrderType(null)
        appPreferences.saveOrderID(null)
        appPreferences.saveCatImgName(null)
        appPreferences.saveCatIconImgId(null)
        appPreferences.saveCatIconImgName(null)
        appPreferences.saveImgStatus(null)
        appPreferences.saveCategoryId(null)
        appPreferences.saveCustId(null)
        appPreferences.saveCustName(null)
        appPreferences.clearAddProduct()

        if (appPreferences.getLoginType().toString() == "staff"){
            println("98989     "+appPreferences.getStaffPermissions())
            println("98989  8585   "+appPreferences.getAllPermissions())
            manageStaffRestrictions()
            manageDailyOperations()
            manageDataManagement()
            manageGeneral()
        }
        setViewModel()
        setConversionChart()
        setUpRevenueBar()
        setUpPieChart()
        createList()
        handleClick()
        setBgGradient()

    }

    private fun manageGeneral() = with(binding.pageGeneral){
        cardTotalSales.setVisibleIfPermission(PermissionKeys.GENERAL_TOTAL_SALE_WIDGET)
        cardTotalClient.setVisibleIfPermission(PermissionKeys.GENERAL_TOTAL_CLIENT_WIDGET)
        cardTotalSalesAmount.setVisibleIfPermission(PermissionKeys.GENERAL_TOTAL_SALE_AMOUNT_WIDGET)
        cardTotalOrders.setVisibleIfPermission(PermissionKeys.GENERAL_TOTAL_ORDERS_WIDGET)
        cardOrderAmount.setVisibleIfPermission(PermissionKeys.GENERAL_ORDER_AMOUNT_WIDGET)
        cardTotalDoctors.setVisibleIfPermission(PermissionKeys.GENERAL_TOTAL_DOCTORS_WIDGET)
        cardTotalVendors.setVisibleIfPermission(PermissionKeys.GENERAL_TOTAL_VENDORS_WIDGET)
        cardStockCount.setVisibleIfPermission(PermissionKeys.GENERAL_STOCK_COUNT_WIDGET)
        cardDoctorOutstanding.setVisibleIfPermission(PermissionKeys.GENERAL_DOCTOR_OUTSTANDING_WIDGET)
        cardPaidToDoctor.setVisibleIfPermission(PermissionKeys.GENERAL_PAID_TO_DOCTOR_WIDGET)
        cardSellerOutstanding.setVisibleIfPermission(PermissionKeys.GENERAL_SELLER_OUTSTANDING_WIDGET)
        cardPaidToSeller.setVisibleIfPermission(PermissionKeys.GENERAL_PAID_TO_SELLER_WIDGET)
        cardGrossMargin.setVisibleIfPermission(PermissionKeys.GENERAL_GROSS_MARGIN_WIDGET)
        cardNetMargin.setVisibleIfPermission(PermissionKeys.GENERAL_NET_MARGIN_WIDGET)
        cardPendingDelivery.setVisibleIfPermission(PermissionKeys.GENERAL_PENDING_DELIVERY_WIDGET)
        cardSalesReturn.setVisibleIfPermission(PermissionKeys.GENERAL_SALES_RETURN_WIDGET)
        cardApprovedRequest.setVisibleIfPermission(PermissionKeys.GENERAL_APPROVED_REQUEST_WIDGET)
        cardDeliveryCost.setVisibleIfPermission(PermissionKeys.GENERAL_DELIVERY_COST_WIDGET)
        cardDeliveryCount.setVisibleIfPermission(PermissionKeys.GENERAL_DELIVERY_COUNT_WIDGET)
        cardCancelledOrder.setVisibleIfPermission(PermissionKeys.GENERAL_TOTAL_CANCELLED_WIDGET)
        cardClosedOrders.setVisibleIfPermission(PermissionKeys.GENERAL_TOTAL_CLOSED_WIDGET)

        if (cardClosedOrders.visibility == View.GONE){
            cardInvisibleOne.visibility = View.GONE
            cardInvisibleTwo.visibility = View.GONE
            cardInvisibleThree.visibility = View.GONE
        }

        binding.lytGeneral.visibility =
            if (listOf(
                    cardTotalSales,
                    cardTotalClient,
                    cardTotalSalesAmount,
                    cardTotalOrders,
                    cardOrderAmount,
                    cardTotalDoctors,
                    cardTotalVendors,
                    cardStockCount,
                    cardDoctorOutstanding,
                    cardPaidToDoctor,
                    cardSellerOutstanding,
                    cardPaidToSeller,
                    cardGrossMargin,
                    cardNetMargin,
                    cardPendingDelivery,
                    cardSalesReturn,
                    cardApprovedRequest,
                    cardDeliveryCost,
                    cardDeliveryCount,
                    cardCancelledOrder,
                    cardClosedOrders
                ).all { it.visibility == View.GONE }
            ) View.GONE else View.VISIBLE
    }

    private fun manageDataManagement() = with(binding.pageDataManagement) {
        cardSellerMissing.setVisibleIfPermission(PermissionKeys.DATA_MANAGEMENT_SELLER_MISSING_WIDGET)
        cardSellerCommission.setVisibleIfPermission(PermissionKeys.DATA_MANAGEMENT_SELLER_COMMISSION_WIDGET)
        cardDoctorCommission.setVisibleIfPermission(PermissionKeys.DATA_MANAGEMENT_DOCTOR_COMMISSION_WIDGET)
        cardDoctorNillCommission.setVisibleIfPermission(PermissionKeys.DATA_MANAGEMENT_DOCTOR_COMMISSION_NILL_WIDGET)
        cardDoctorPharmaWithOutZero.setVisibleIfPermission(PermissionKeys.DATA_MANAGEMENT_DOCTOR_COMMISSION_PHARMA_WITH_OUT_ZERO)
        cardDoctorNonPharma.setVisibleIfPermission(PermissionKeys.INFLUENCER_NOT_PHARMA_PRODUCT_REPORT)

        if (cardDoctorNonPharma.visibility == View.GONE && cardDoctorPharmaWithOutZero.visibility == View.GONE){
            cardInvisibleTwo.visibility = View.GONE
            cardInvisibleOne.visibility = View.GONE
        }

        binding.lytDailyManagement.visibility =
            if (listOf(
                    cardSellerMissing,
                    cardSellerCommission,
                    cardDoctorCommission,
                    cardDoctorNillCommission,
                    cardDoctorPharmaWithOutZero,
                    cardDoctorNonPharma
                ).all { it.visibility == View.GONE }
            ) View.GONE else View.VISIBLE
    }

    private fun manageDailyOperations() = with(binding.pageDailyOperation) {
        cardDailySales.setVisibleIfPermission(PermissionKeys.DAILY_SALES_WIDGET)
        cardTotalOrders.setVisibleIfPermission(PermissionKeys.DAILY_ORDERS_WIDGET)
        cardPendingDelivery.setVisibleIfPermission(PermissionKeys.PENDING_DELIVERY_WIDGET)
        cardGrossMargin.setVisibleIfPermission(PermissionKeys.GROSS_MARGIN_WIDGET)
        cardNetMargin.setVisibleIfPermission(PermissionKeys.NET_MARGIN_WIDGET)
        cardRefundRequest.setVisibleIfPermission(PermissionKeys.REFUND_REQUEST_WIDGET)
        cardApproveOrder.setVisibleIfPermission(PermissionKeys.APPROVE_ORDERS_WIDGET)
        cardCancelledOrder.setVisibleIfPermission(PermissionKeys.CANCELLED_ORDERS_WIDGET)
        cardClosedOrders.setVisibleIfPermission(PermissionKeys.CLOSED_ORDERS_WIDGET)

        if (cardClosedOrders.visibility == View.GONE){
            cardInvisibleOne.visibility = View.GONE
            cardInvisibleTwo.visibility = View.GONE
            cardInvisibleThree.visibility = View.GONE
        }

        binding.lytDailyOperation.visibility =
            if (listOf(
                    cardDailySales,
                    cardTotalOrders,
                    cardPendingDelivery,
                    cardGrossMargin,
                    cardNetMargin,
                    cardRefundRequest,
                    cardApproveOrder,
                    cardCancelledOrder,
                    cardClosedOrders
                ).all { it.visibility == View.GONE }
            ) View.GONE else View.VISIBLE
    }

    private fun manageStaffRestrictions() = with(binding) {
        lytMostViewedProducts.setVisibleIfPermission(PermissionKeys.MOST_VIEWED_PRODUCTS)
        cardConversionRate.setVisibleIfPermission(PermissionKeys.CONVERSION_RATE)
        lytNewlyAddedVendors.setVisibleIfPermission(PermissionKeys.NEWLY_ADDED_VENDORS)
        lytNewlyAddedDoctors.setVisibleIfPermission(PermissionKeys.NEWLY_ADDED_DOCTORS)
        lytLastSearchTerm.setVisibleIfPermission(PermissionKeys.LAST_SEARCH_TERM)
        lytNewCustomers.setVisibleIfPermission(PermissionKeys.NEW_CUSTOMERS)
        lytRecentOrders.setVisibleIfPermission(PermissionKeys.RECENT_ORDERS)
        lytTopSearchTerm.setVisibleIfPermission(PermissionKeys.TOP_SEARCH_TERM)
        lytBestSellingProducts.setVisibleIfPermission(PermissionKeys.BEST_SELLING_PRODUCTS)
        cardRevenuePieChart.setVisibleIfPermission(PermissionKeys.REVENUE_AN_SALES_PIECHART)
        cardRevenueBarChart.setVisibleIfPermission(PermissionKeys.REVENUE_AN_SALES_BARCHART)
        cardMonthlyWithCogVsSales.setVisibleIfPermission(PermissionKeys.MONTHLY_SALES_COG_VS_SALES)
        cardMonthlyWithCogProductSales.setVisibleIfPermission(PermissionKeys.MONTHLY_SALES_COG_VS_PRODUCT_SALES)
        cardMonthly3Item.setVisibleIfPermission(PermissionKeys.MONTHLY_SALES_SALES_VS_APIXMARGIN_VS_NETMARGIN)
        cardMonthlyNoOfSalesItem.setVisibleIfPermission(PermissionKeys.MONTHLY_SALES_NOOFSALES_VS_AMOUNTSALES_VS_PRODUCTSALES)
        cardDoctorPerformance.setVisibleIfPermission(PermissionKeys.DOCTOR_PERFORMANCE)
        cardSalesHourlyBasics.setVisibleIfPermission(PermissionKeys.SALES_HOURLY_BASIS)
        cardSalesAnalyticsNoOfSale.setVisibleIfPermission(PermissionKeys.SALES_ANALYTICS_NO_OF_SALE)
        cardSalesAnalyticsApix.setVisibleIfPermission(PermissionKeys.SALES_ANALYTICS_NO_OF_SALE_GROSS_APIX)
    }

    private fun View.setVisibleIfPermission(permissionKey: String) {
        Log.d("StaffPerms", "check  :"+permissionKey)

        if (isTablet()){
            visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE

        }else{
            visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE

        }
    }
    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
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
        viewModel.getAllPermissions(appPreferences.getToken().toString())
        viewModel.allPermissionList.observe(viewLifecycleOwner) { data ->
            appPreferences.saveAllPermissions(data.permissions)
            if (appPreferences.getLoginType().toString() == "staff"){
                println("98989     "+appPreferences.getStaffPermissions())
                println("98989  8585   "+appPreferences.getAllPermissions())
                manageStaffRestrictions()
                manageDailyOperations()
                manageDataManagement()
                manageGeneral()
            }

            println("-----------------hhh---------   "+appPreferences.getAllPermissions())
        }
        viewModel.getUserPermissions(appPreferences.getToken().toString())
        viewModel.userPermissionList.observe(viewLifecycleOwner) { data ->
            appPreferences.saveStaffPermissions(data.permissions)
            if (appPreferences.getLoginType().toString() == "staff"){
                println("98989     "+appPreferences.getStaffPermissions())
                println("98989  8585   "+appPreferences.getAllPermissions())
                manageStaffRestrictions()
                manageDailyOperations()
                manageDataManagement()
                manageGeneral()
            }

            println("-----------------hhh---------   "+appPreferences.getAllPermissions())
        }
        viewModel.homeData.observe(viewLifecycleOwner) { data ->

            binding.progressBar.visibility = View.GONE

            manageCards(data)
            manageRecentOrder(data?.recentOrders)
            manageNewCust(data?.newCustomers)
            manageTopSearch(data?.topSearches)
            manageLastSearch(data?.lastSearchTerm)
            manageBestSeller(data?.topSellingProducts)
            manageNewDoctor(data?.newDoctors)
            manageNewVendor(data?.newVendors)
            manageMostViewed(data?.mostViewedProducts)


        }
        viewModel.graphData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE

            setUpSalesNoChart(data?.num_of_sale_value,data?.weekdays)
            setUpLineChart(data?.weekdays,data?.num_of_sale_amt,data?.num_of_gross_margin,data?.num_of_apix_margin)
            setupDocterChart(data?.weekdays,data?.influencers)
            val orderedMonths = LinkedHashMap<String, MonthData>()
            data?.months?.forEach { (monthName, monthData) ->
                orderedMonths[monthName] = monthData
            }
            setUpMonthlyNoOfSales(orderedMonths)
            setUpMonthly(orderedMonths)
            setUpBarChart(orderedMonths)
            setUpMonthly2(orderedMonths)
            setupSalesHourlyChart(data?.hourSales)

        }

        binding.progressBar.visibility = View.VISIBLE
        val request1 = ApiService.HomeRequest(
            date = ""
        )
        viewModel.getHomeData(appPreferences.getToken().toString(),request1)
        val request = ApiService.GraphRequest(
            date = ""
        )
        viewModel.getGraph(appPreferences.getToken().toString(), request)
        viewModel.emptyCart(appPreferences.getToken().toString())

    }

    private fun manageCards(data: HomeDataResponse?){

        binding.pageDailyOperation.txtTotalSales.text = data?.totalSales
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        binding.pageDailyOperation.txtTotalSalesNos.text = data?.totalSalesCount
            ?.takeIf { it.isNotBlank() } ?: "0 Nos"

        binding.pageDailyOperation.txtTotalSalesSum.text = data?.totalSalesSum
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        binding.pageDailyOperation.txtTotalOrders.text = data?.totalOrders
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        binding.pageDailyOperation.txtTotalOrdersNos.text = data?.totalOrdersNo
            ?.takeIf { it.isNotBlank() } ?: "0 Nos"

        binding.pageDailyOperation.txtTotalOrdersCount.text = data?.totalOrdersCount
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        // pending delivery

        binding.pageDailyOperation.txtPendingDelivery.text = data?.pendingDelivery
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        binding.pageDailyOperation.txtPendingDeliveryNos.text = data?.pendingDeliveryNo
            ?.takeIf { it.isNotBlank() } ?: "0 Nos"


        // gross margin

        binding.pageDailyOperation.txtGrossMargin.text = data?.grossMargin
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        binding.pageDailyOperation.txtConversionRate.text = data?.conversionRate
            ?.takeIf { it.isNotBlank() } ?: "0 %"

        // net margin

        binding.pageDailyOperation.txtNetMargin.text = data?.netMargin
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        // refund request

        binding.pageDailyOperation.txtRefundRequest.text = data?.refundRequest
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        binding.pageDailyOperation.txtRefundRequestNos.text = data?.refundRequestNo
            ?.takeIf { it.isNotBlank() } ?: "0 Nos"


        // approve request

        binding.pageDailyOperation.txtApproveRequest.text = data?.approveRequest
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        binding.pageDailyOperation.txtApproveRequestNos.text = data?.approveRequestNo
            ?.takeIf { it.isNotBlank() } ?: "0 Nos"

        // cancelled orders

        binding.pageDailyOperation.txtCancelledOrders.text = data?.cancelledOrders
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        binding.pageDailyOperation.txtCancelledOrdersNos.text = data?.cancelledOrdersNo
            ?.takeIf { it.isNotBlank() } ?: "0 Nos"

        // closed orders

        binding.pageDailyOperation.txtClosedOrders.text = data?.closedOrders
            ?.takeIf { it.isNotBlank() } ?: "0 KD"

        binding.pageDailyOperation.txtClosedOrdersNos.text = data?.closedOrdersNo
            ?.takeIf { it.isNotBlank() } ?: "0 Nos"

        // data management

        binding.pageDataManagement.txtSellerMissionProductCount.text = data?.sellerMissingProductCount
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageDataManagement.txtSellerCommissionWithNil.text = data?.sellerCommissionWithNIL
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageDataManagement.txtDoctorCommissionTableCount.text = data?.doctorCommissionTableCount
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageDataManagement.txtDocCommissionWithNil.text = data?.doctorCommissionWithNil
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageDataManagement.txtPharmaWithOutZero.text = data?.doctorCommissionPharmaceuticalWithZero
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageDataManagement.txtDocCommPharWithZero.text = data?.doctorCommissionNonPharmaceuticalWithZero
            ?.takeIf { it.isNotBlank() } ?: ""

        // general

        binding.pageGeneral.txtGeneralTotalSales.text = data?.generalTotalSales
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtGeneralTotalClient.text = data?.totalPatients
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtGeneralTotalSalesAmount.text = data?.totalSaleAmount
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtGeneralTotalOrders.text = data?.generalTotalOrders
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtGeneralOrderAmount.text = data?.totalOrderAmount
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtGeneralTotalDoctor.text = data?.totalDoctors
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtTotalVendors.text = data?.totalVendors
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtStockCount.text = data?.stockCount
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtProductNos.text = data?.productsCount
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtDocOutstanding.text = data?.doctorOutstanding
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtPaidToDoctor.text = data?.paidToDoctor
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtSellerOutstanding.text = data?.sellerOutstanding
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtPaidToSeller.text = data?.paidToSeller
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtGrossMargin.text = data?.grossMargin
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtNetMargin.text = data?.netMargin
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtPendingDelivery.text = data?.pendingDelivery
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtSalesReturnRequest.text = data?.returnRequests
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtApprovedRequest.text = data?.approveRequest
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtTotalDeliveryCost.text = data?.deliveryCost
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtTotalDeliveryCount.text = data?.deliveryCount
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtTotalCancelledOrders.text = data?.generalCancelledOrders
            ?.takeIf { it.isNotBlank() } ?: ""

        binding.pageGeneral.txtTotalClosedOrders.text = data?.generalClosedOrders
            ?.takeIf { it.isNotBlank() } ?: ""

    }

    private fun setUpRevenueBar() {
        val entries = listOf(
            BarEntry(0f, floatArrayOf(30f, 20f, 50f)),
            BarEntry(1f, floatArrayOf(40f, 30f, 30f)),
            BarEntry(2f, floatArrayOf(25f, 50f, 25f))
        )

        val dataSet = BarDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#d947bf"),
            Color.parseColor("#684440"),
            Color.parseColor("#be83ab")
        )
        dataSet.setDrawValues(false)

        val data = BarData(dataSet)
        data.barWidth = 0.3f
        binding.chartBarRevenue.data = data

        binding.chartBarRevenue.description.isEnabled = false
        binding.chartBarRevenue.setFitBars(true)
        binding.chartBarRevenue.animateY(1000)
        binding.chartBarRevenue.legend.isEnabled = true

        binding.chartBarRevenue.invalidate()
    }

    private fun setUpPieChart() {
        val entries = listOf(
            PieEntry(75f),
            PieEntry(8.33f),
            PieEntry(5.33f),
            PieEntry(3f)
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#d947bf"),
            Color.parseColor("#684440"),
            Color.parseColor("#be83ab"),
            Color.parseColor("#d81f9c")
        )
        dataSet.setDrawValues(false)


        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.isDrawHoleEnabled = false
        binding.pieChart.setDrawEntryLabels(false)
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.animateY(1000)
    }

    private fun setupSalesHourlyChart(hourSales: List<HourSales>?) {
        if (hourSales.isNullOrEmpty()) return

        // Prepare entries for LineChart
        val greenEntries = mutableListOf<Entry>()
        hourSales.forEachIndexed { index, data ->
            val salesValue = data.sales.replace(",", "").toFloatOrNull() ?: 0f
            greenEntries.add(Entry(index.toFloat(), salesValue))
        }

        val greenDataSet = LineDataSet(greenEntries, "").apply {
            color = Color.parseColor("#90CDBB")
            setDrawValues(false)
            setCircleColor(Color.parseColor("#90CDBB"))
            circleRadius = 4f
            lineWidth = 2f
        }

        val lineData = LineData(greenDataSet)
        binding.chartSalesHourlyBasics.data = lineData

        // Disable right axis and description
        binding.chartSalesHourlyBasics.apply {
            description.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
        }

        // Left axis setup
        val leftAxis = binding.chartSalesHourlyBasics.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = (hourSales.maxOfOrNull { it.sales.replace(",", "").toFloatOrNull() ?: 0f } ?: 100f) * 1.2f
        leftAxis.labelCount = 5
        leftAxis.granularity = leftAxis.axisMaximum / 5f

        // X-axis setup with hour labels
        val xAxis = binding.chartSalesHourlyBasics.xAxis
        xAxis.apply {
            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setCenterAxisLabels(false)
            setAvoidFirstLastClipping(true)
            labelCount = hourSales.size
            axisMinimum = -0.5f
            axisMaximum = hourSales.size - 0.5f
            valueFormatter = IndexAxisValueFormatter(hourSales.map { it.hour })  // <--- Hour labels
        }

        binding.chartSalesHourlyBasics.invalidate()
    }


    private fun setBgGradient() {
        applyGradientBackground(
            binding.pageDailyOperation.lytTotalSales,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF875FC0.toInt(), 0xFF5346BA.toInt())
        )

        applyGradientBackground(
            binding.pageDailyOperation.lytTotalOrders,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF47C5F4.toInt(), 0xFF6791D9.toInt())
        )

        applyGradientBackground(
            binding.pageDailyOperation.lytPendingDelivery,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFFEB4786.toInt(), 0xFFB854A6.toInt())
        )

        applyGradientBackground(
            binding.pageDailyOperation.lytTotalDelivery,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFFFFB72C.toInt(), 0xFFF57F59.toInt())
        )

        applyGradientBackground(
            binding.pageDailyOperation.lytGrossMargin,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF56FF2C.toInt(), 0xFF1D761F.toInt())
        )

        applyGradientBackground(
            binding.pageDailyOperation.lytNetMargin,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF00008B.toInt(), 0xFF00008B.toInt())
        )

        applyGradientBackground(
            binding.pageDailyOperation.lytRefundRequest,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF1E8B89.toInt(), 0xFF1E8B89.toInt())
        )

        applyGradientBackground(
            binding.pageDailyOperation.lytApproveRequest,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF1E5611.toInt(), 0xFF1E5611.toInt())
        )

        applyGradientBackground(
            binding.pageDailyOperation.lytCancelledOrder,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFFFF4500.toInt(), 0xFFFF4500.toInt())
        )
        applyGradientBackground(
            binding.pageDailyOperation.lytClosedOrder,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF3D5959.toInt(), 0xFF3D5959.toInt())
        )


        /* data management */

        applyGradientBackground(
            binding.pageDataManagement.lytSellerOne,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF1B0A48.toInt(), 0xFF1B0A48.toInt())
        )
        binding.pageDataManagement.lytDocPharmWithZero.setBackgroundColor(Color.parseColor("#1e5611"))
        binding.pageDataManagement.lytDocPharmWithOutZero.setBackgroundColor(Color.parseColor("#7e6846"))
        binding.pageDataManagement.lytSellerTwo.setBackgroundColor(Color.parseColor("#540327"))
        binding.pageDataManagement.lytInfluencerOne.setBackgroundColor(Color.parseColor("#45139c"))
        binding.pageDataManagement.lytInfluencerTwo.setBackgroundColor(Color.parseColor("#111f1f"))


        binding.pageGeneral.lytTotalSales.setBackgroundColor(Color.parseColor("#ffc107"))

        applyGradientBackground(
            binding.pageGeneral.lytTotalPatient,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF875FC0.toInt(), 0xFF5346BA.toInt())
        )
        binding.pageGeneral.lytTotalSalesAmount.setBackgroundColor(Color.parseColor("#ff0781"))

        applyGradientBackground(
            binding.pageGeneral.lytTotalVendors,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF56FF2C.toInt(), 0xFF1D761F.toInt())
        )
        applyGradientBackground(
            binding.pageGeneral.lytTotalOrders,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF47C5F4.toInt(), 0xFF6791D9.toInt())
        )
        applyGradientBackground(
            binding.pageGeneral.lytOrderAmount,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFFEB4786.toInt(), 0xFFB854A6.toInt())
        )
        applyGradientBackground(
            binding.pageGeneral.lytTotalInfluencer,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFFFFB72C.toInt(), 0xFFF57F59.toInt())
        )

        binding.pageGeneral.lytStockCount.setBackgroundColor(Color.parseColor("#1e8b89"))
        binding.pageGeneral.lytInfluencerOutstanding.setBackgroundColor(Color.parseColor("#dc3545"))
        binding.pageGeneral.lytPaidToInfluencer.setBackgroundColor(Color.parseColor("#0c3b50"))
        binding.pageGeneral.lytSellerOutstanding.setBackgroundColor(Color.parseColor("#007bff"))
        binding.pageGeneral.lytPaidToSeller.setBackgroundColor(Color.parseColor("#ea2cff"))
        binding.pageGeneral.lytGrossMargin.setBackgroundColor(Color.parseColor("#1e5611"))
        binding.pageGeneral.lytNetMargin.setBackgroundColor(Color.parseColor("#00008b"))
        binding.pageGeneral.lytPendingDelivery.setBackgroundColor(Color.parseColor("#ff69b4"))
        binding.pageGeneral.lytSalesReturnRequest.setBackgroundColor(Color.parseColor("#7e2513"))
        binding.pageGeneral.lytApprovedRequest.setBackgroundColor(Color.parseColor("#132d7b"))
        binding.pageGeneral.lytTotalDeliveryCost.setBackgroundColor(Color.parseColor("#0c0d10"))
        binding.pageGeneral.lytDeliveryCount.setBackgroundColor(Color.parseColor("#4f3b3b"))
        binding.pageGeneral.lytTotalCancelledOrders.setBackgroundColor(Color.parseColor("#ff4500"))
        binding.pageGeneral.lytClosedOrder.setBackgroundColor(Color.parseColor("#3d5959"))

    }

   private fun applyGradientBackground(view: View, orientation: GradientDrawable.Orientation, colors: IntArray) {
        val gradientDrawable = GradientDrawable(orientation, colors).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
        view.background = gradientDrawable
    }
    private fun setupDocterChart(
        weekdays: List<String>?,
        influencers: List<Influencer>?
    ) {
        if (weekdays.isNullOrEmpty() || influencers.isNullOrEmpty()) {
            binding.chartDocter.clear()
            binding.chartDocter.setNoDataText("No doctor data available")
            return
        }

        val dataSets = mutableListOf<ILineDataSet>()

        influencers.forEach { influencer ->

            // Skip if border color is null or blank
            if (influencer.borderColor.isNullOrBlank()) return@forEach

            // Build entries, convert safely to float
            val entries = influencer.data.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toString().toFloatOrNull() ?: 0f)
            }

            // Skip if all values are zero
            val hasNonZero = entries.any { it.y != 0f }
            if (!hasNonZero) return@forEach

            val color = Color.parseColor(influencer.borderColor)

            val dataSet = LineDataSet(entries, influencer.label).apply {
                this.color = color
                setCircleColor(color)
                circleRadius = 3f
                lineWidth = 2f
                setDrawValues(true)
                valueTextSize = 9f
                valueTextColor = Color.BLACK
                setDrawFilled(true)
                fillColor = color
                fillAlpha = 40
            }

            dataSets.add(dataSet)
        }

        if (dataSets.isEmpty()) {
            binding.chartDocter.clear()
            binding.chartDocter.setNoDataText("No doctor data available")
            return
        }

        binding.chartDocter.data = LineData(dataSets)

        // Chart styling
        binding.chartDocter.description.isEnabled = false
        binding.chartDocter.axisRight.isEnabled = false
        binding.chartDocter.legend.isEnabled = true

        binding.chartDocter.axisLeft.apply {
            axisMinimum = 0f
            granularity = 1f
        }

        binding.chartDocter.xAxis.apply {
            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            valueFormatter = IndexAxisValueFormatter(weekdays)
            labelCount = weekdays.size
        }

        binding.chartDocter.invalidate()
    }



    private fun setUpSalesNoChart(numOfSaleValue: List<String>?, weekdays: List<String>?) {



        if (numOfSaleValue.isNullOrEmpty() || weekdays.isNullOrEmpty()) return

        val entries = mutableListOf<Entry>()
        numOfSaleValue.forEachIndexed { index, value ->
            entries.add(Entry(index.toFloat(), value.toFloat()))
        }

        val dataSet = LineDataSet(entries, "")
        dataSet.color = Color.parseColor("#77806d")
        dataSet.setDrawValues(false)
        dataSet.setCircleColor(Color.parseColor("#77806d"))
        dataSet.circleRadius = 4f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.GRAY
        dataSet.fillAlpha = 80

        val lineData = LineData(dataSet)
        binding.chartNoOfSales.data = lineData

        // Chart config
        binding.chartNoOfSales.description.isEnabled = false
        binding.chartNoOfSales.axisRight.isEnabled = false

        // Y Axis
        val leftAxis = binding.chartNoOfSales.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.granularity = 1f

        // X Axis
        val xAxis = binding.chartNoOfSales.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(weekdays)
        xAxis.labelRotationAngle = 0f
        xAxis.labelCount = weekdays.size
        xAxis.isGranularityEnabled = true

        binding.chartNoOfSales.legend.isEnabled = false
        binding.chartNoOfSales.invalidate()
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
        val request1 = ApiService.HomeRequest(
            date = rangeString
        )
        viewModel.getHomeData(AppPreferences.getInstance(requireContext()).getToken().toString(),request1)
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
            val request1 = ApiService.HomeRequest(
                date = rangeString
            )
            viewModel.getHomeData(AppPreferences.getInstance(requireContext()).getToken().toString(),request1)
        }
    }
    private fun handleClick() {
        binding.imgFilterReset.setOnClickListener{
            binding.progressBar.visibility = View.VISIBLE
            binding.txtFilterByDate.text = null
            val request = ApiService.GraphRequest(
                date = ""
            )
            viewModel.getGraph(
                AppPreferences.getInstance(requireContext()).getToken().toString(),
                request
            )
            val request1 = ApiService.HomeRequest(
                date = ""
            )
            viewModel.getHomeData(AppPreferences.getInstance(requireContext()).getToken().toString(),request1)
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

        binding.txtCustViewAll.setOnClickListener {
            findNavController().navigate(R.id.nav_customer)
        }
        binding.txtAllOrders.setOnClickListener {
            AppPreferences.getInstance(requireContext()).saveOrderType("All Orders")
            findNavController().navigate(R.id.nav_sale_details)
        }
        binding.imgHomeMenu.setOnClickListener {
            (activity as MainActivity).toggleMenuOverlay(true)
        }

        binding.newVendorViewAll.setOnClickListener {
            findNavController().navigate(R.id.nav_seller)
        }
        binding.influencerViewAll.setOnClickListener {
            findNavController().navigate(R.id.nav_influencer)
        }
    }
    private fun manageNewDoctor(newDoctors: List<NewDoctors>?) {
        binding.recyclerInfluencer.apply {
            layoutManager = GridLayoutManager(context, 2)
            val catAdapter = InfluencerHomeAdapter(newDoctors) { data, position -> }
            adapter = catAdapter
        }
    }
    private fun manageNewVendor(newVendors: List<NewVendors>?) {
        binding.recyclerNewVendor.apply {
            layoutManager = GridLayoutManager(context, 2)
            val catAdapter = NewVendorHomeAdapter(newVendors) { data, position -> }
            adapter = catAdapter
        }
    }
    private fun manageLastSearch(lastSearchTerm: List<LastSearchTerm>?) {
        binding.recyclerLastSearch.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val catAdapter = LastSearchHomeAdapter(lastSearchTerm) { data, position ->

            }
            adapter = catAdapter
        }
    }
    private fun manageTopSearch(topSearches: List<TopSearches>?) {
        binding.recyclerTopSearch.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val catAdapter = TopSearchHomeAdapter(topSearches) { data, position ->

            }
            adapter = catAdapter
        }
    }
    private fun manageNewCust(newCustomers: List<NewCustomers>?) {
        binding.recyclerNewCustomers.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = NewCustHomeAdapter(newCustomers) { data, position ->

            }
            adapter = catAdapter
        }
    }

    private fun manageRecentOrder(recentOrders: List<RecentOrders>?) {
        if (recentOrders.isNullOrEmpty()){
            binding.recyclerRecentOrders.visibility = View.GONE
        }else{
            binding.recyclerRecentOrders.visibility = View.VISIBLE
        }
        binding.recyclerRecentOrders.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = RecentOrderHomeAdapter(recentOrders) { data, position ->

            }
            adapter = catAdapter
        }
    }
    private fun manageMostViewed(mostViewedProducts: List<MostViewed>?) {
        binding.recyclerMostSellingProduct.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val catAdapter = BestSeller2HomeAdapter(mostViewedProducts) { data, position ->

            }
            adapter = catAdapter
        }
    }
    private fun manageBestSeller(topSellingProducts: List<TopSellProd>?) {
        binding.recyclerBestSellingProduct.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val catAdapter = BestSellerHomeAdapter(topSellingProducts) { data, position ->

            }
            adapter = catAdapter
        }
    }

    private fun createList(): ArrayList<Cat> {
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
    private fun setUpBarChart(months: Map<String, MonthData>?) {
        if (months.isNullOrEmpty()) return


        fun safeToFloat(value: String?): Float = value?.replace(",", "")?.toFloatOrNull() ?: 0f

        val orderedMonths = LinkedHashMap<String, MonthData>()
        months.forEach { (monthName, monthData) -> orderedMonths[monthName] = monthData }

        val monthNames = orderedMonths.keys.toList()

        val numEntries = mutableListOf<BarEntry>()
        val productEntries = mutableListOf<BarEntry>()

        monthNames.forEachIndexed { index, month ->
            val x = index.toFloat()
            val data = orderedMonths[month]
            data?.let {
                val amount = safeToFloat(it.amount_sales)
                val product = safeToFloat(it.product_sales)

                numEntries.add(BarEntry(x, amount))
                productEntries.add(BarEntry(x, product))
            }
        }


        if (numEntries.all { it.y == 0f } && productEntries.all { it.y == 0f }) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No chart data available")
            return
        }

        val numDataSet = BarDataSet(numEntries, "Amount of Sales").apply {
            color = Color.parseColor("#fa7000")
            setDrawValues(false)
        }

        val productDataSet = BarDataSet(productEntries, "Product Sales").apply {
            color = Color.parseColor("#dc3545")
            setDrawValues(false)
        }

        val barData = BarData(numDataSet, productDataSet)


        val numberOfBars = 2
        val barSpace = 0.02f
        val groupSpace = 0.3f
        val barWidth = (1f - groupSpace) / numberOfBars - barSpace
        barData.barWidth = barWidth

        val groupCount = monthNames.size
        val startX = 0f
        val groupWidth = barData.getGroupWidth(groupSpace, barSpace)

        binding.barChart.apply {
            data = barData
            setDragEnabled(true)
            setScaleEnabled(false)
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = (numEntries + productEntries).maxOfOrNull { it.y }?.times(1.2f) ?: 100f
            description.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(monthNames)
                granularity = 1f
                isGranularityEnabled = true
                setCenterAxisLabels(true)
                setAvoidFirstLastClipping(false)
                labelCount = monthNames.size
                axisMinimum = startX
                axisMaximum = startX + groupWidth * groupCount
            }


            groupBars(startX, groupSpace, barSpace)

            setVisibleXRangeMaximum(groupCount.toFloat())


            barData.notifyDataChanged()
            notifyDataSetChanged()
            invalidate()
        }
    }

    private fun setUpMonthlyNoOfSales(months: Map<String, MonthData>?) {
        if (months.isNullOrEmpty()) return

        val orderedMonths = LinkedHashMap<String, MonthData>()
        months.forEach { (monthName, monthData) -> orderedMonths[monthName] = monthData }

        val monthNames = orderedMonths.keys.toList()

        val numEntries = mutableListOf<BarEntry>()
        val amountEntries = mutableListOf<BarEntry>()
        val productEntries = mutableListOf<BarEntry>()

        // ✅ Use index.toFloat() instead of index + 0.5f
        monthNames.forEachIndexed { index, month ->
            val x = index.toFloat()  // <-- start from 0f
            val data = orderedMonths[month]
            data?.let {
                numEntries.add(BarEntry(x, it.num_sales.replace(",", "").toFloatOrNull() ?: 0f))
                amountEntries.add(BarEntry(x, it.amount_sales.replace(",", "").toFloatOrNull() ?: 0f))
                productEntries.add(BarEntry(x, it.product_sales.replace(",", "").toFloatOrNull() ?: 0f))
            }
        }

        val numDataSet = BarDataSet(numEntries, "Number of Sales").apply {
            color = Color.parseColor("#fa7000")
            setDrawValues(false)
        }
        val amountDataSet = BarDataSet(amountEntries, "Amount of Sales").apply {
            color = Color.parseColor("#0060f0")
            setDrawValues(false)
        }
        val productDataSet = BarDataSet(productEntries, "Product Sales").apply {
            color = Color.parseColor("#dc3545")
            setDrawValues(false)
        }

        val barData = BarData(numDataSet, amountDataSet, productDataSet)

        val groupSpace = 0.4f
        val barSpace = 0.02f
        val barWidth = 0.18f
        barData.barWidth = barWidth

        binding.chartMonthlySalesNoOfSales.data = barData
        binding.chartMonthlySalesNoOfSales.setDragEnabled(true)
        binding.chartMonthlySalesNoOfSales.setScaleEnabled(false)

        val xAxis = binding.chartMonthlySalesNoOfSales.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(monthNames)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setCenterAxisLabels(true)
        xAxis.setAvoidFirstLastClipping(false)
        xAxis.labelRotationAngle = 0f
        xAxis.labelCount = monthNames.size

        binding.chartMonthlySalesNoOfSales.axisRight.isEnabled = false
        binding.chartMonthlySalesNoOfSales.axisLeft.axisMinimum = 0f
        binding.chartMonthlySalesNoOfSales.description.isEnabled = false

        val groupCount = monthNames.size
        val groupWidth = barData.getGroupWidth(groupSpace, barSpace)

        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 0f + groupWidth * groupCount

        binding.chartMonthlySalesNoOfSales.groupBars(0f, groupSpace, barSpace)

        binding.chartMonthlySalesNoOfSales.setVisibleXRangeMaximum(groupCount.toFloat())

        binding.chartMonthlySalesNoOfSales.invalidate()
    }
    private fun setUpMonthly(months: Map<String, MonthData>?) {
        if (months.isNullOrEmpty()) return

        val orderedMonths = LinkedHashMap<String, MonthData>()
        months.forEach { (monthName, monthData) -> orderedMonths[monthName] = monthData }

        val monthNames = orderedMonths.keys.toList()

        val numEntries = mutableListOf<BarEntry>()
        val amountEntries = mutableListOf<BarEntry>()
        val productEntries = mutableListOf<BarEntry>()

        // ✅ Use index.toFloat() instead of index + 0.5f
        monthNames.forEachIndexed { index, month ->
            val x = index.toFloat()  // <-- start from 0f
            val data = orderedMonths[month]
            data?.let {
                numEntries.add(BarEntry(x, it.amount_sales.replace(",", "").toFloatOrNull() ?: 0f))
                amountEntries.add(BarEntry(x, it.apix_margin.replace(",", "").toFloatOrNull() ?: 0f))
                productEntries.add(BarEntry(x, it.net_margin.replace(",", "").toFloatOrNull() ?: 0f))
            }
        }

        val numDataSet = BarDataSet(numEntries, "Amount of Sales").apply {
            color = Color.parseColor("#fa7000")
            setDrawValues(false)
        }
        val amountDataSet = BarDataSet(amountEntries, "Apix Margin").apply {
            color = Color.parseColor("#0060f0")
            setDrawValues(false)
        }
        val productDataSet = BarDataSet(productEntries, "Net Margin").apply {
            color = Color.parseColor("#dc3545")
            setDrawValues(false)
        }

        val barData = BarData(numDataSet, amountDataSet, productDataSet)

        val groupSpace = 0.4f
        val barSpace = 0.02f
        val barWidth = 0.18f
        barData.barWidth = barWidth

        binding.chartMonthlySales.data = barData
        binding.chartMonthlySales.setDragEnabled(true)
        binding.chartMonthlySales.setScaleEnabled(false)

        val xAxis = binding.chartMonthlySales.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(monthNames)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setCenterAxisLabels(true)
        xAxis.setAvoidFirstLastClipping(false)
        xAxis.labelRotationAngle = 0f
        xAxis.labelCount = monthNames.size

        binding.chartMonthlySales.axisRight.isEnabled = false
        binding.chartMonthlySales.axisLeft.axisMinimum = 0f
        binding.chartMonthlySales.description.isEnabled = false

        val groupCount = monthNames.size
        val groupWidth = barData.getGroupWidth(groupSpace, barSpace)

        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 0f + groupWidth * groupCount

        binding.chartMonthlySales.groupBars(0f, groupSpace, barSpace)

        binding.chartMonthlySales.setVisibleXRangeMaximum(groupCount.toFloat())

        binding.chartMonthlySales.invalidate()
    }
    private fun setUpMonthly2(months: Map<String, MonthData>?) {
        if (months.isNullOrEmpty()) return


        fun safeToFloat(value: String?): Float = value?.replace(",", "")?.toFloatOrNull() ?: 0f

        val orderedMonths = LinkedHashMap<String, MonthData>()
        months.forEach { (monthName, monthData) -> orderedMonths[monthName] = monthData }

        val monthNames = orderedMonths.keys.toList()

        val numEntries = mutableListOf<BarEntry>()
        val productEntries = mutableListOf<BarEntry>()

        monthNames.forEachIndexed { index, month ->
            val x = index.toFloat()
            val data = orderedMonths[month]
            data?.let {
                val amount = safeToFloat(it.amount_sales)
                val product = safeToFloat(it.cog)

                numEntries.add(BarEntry(x, amount))
                productEntries.add(BarEntry(x, product))
            }
        }


        if (numEntries.all { it.y == 0f } && productEntries.all { it.y == 0f }) {
            binding.chartMonthlySales2.clear()
            binding.chartMonthlySales2.setNoDataText("No chart data available")
            return
        }

        val numDataSet = BarDataSet(numEntries, "Amount of Sales").apply {
            color = Color.parseColor("#0060f0")
            setDrawValues(false)
        }

        val productDataSet = BarDataSet(productEntries, "COG").apply {
            color = Color.parseColor("#dc3545")
            setDrawValues(false)
        }

        val barData = BarData(numDataSet, productDataSet)


        val numberOfBars = 2
        val barSpace = 0.02f
        val groupSpace = 0.3f
        val barWidth = (1f - groupSpace) / numberOfBars - barSpace
        barData.barWidth = barWidth

        val groupCount = monthNames.size
        val startX = 0f
        val groupWidth = barData.getGroupWidth(groupSpace, barSpace)

        binding.chartMonthlySales2.apply {
            data = barData
            setDragEnabled(true)
            setScaleEnabled(false)
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = (numEntries + productEntries).maxOfOrNull { it.y }?.times(1.2f) ?: 100f
            description.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(monthNames)
                granularity = 1f
                isGranularityEnabled = true
                setCenterAxisLabels(true)
                setAvoidFirstLastClipping(false)
                labelCount = monthNames.size
                axisMinimum = startX
                axisMaximum = startX + groupWidth * groupCount
            }


            groupBars(startX, groupSpace, barSpace)

            setVisibleXRangeMaximum(groupCount.toFloat())


            barData.notifyDataChanged()
            notifyDataSetChanged()
            invalidate()
        }
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    private fun setUpLineChart(
        weekdays: List<String>?,
        numOfSaleAmt: List<String>?,
        numOfGrossMargin: List<String>?,
        numOfApixMargin: List<String>?
    ) {

        Log.d("ChartData 2", "Weekdays Doctor  : $weekdays")
        Log.d("ChartData 2", "numOfSaleAmt: $numOfSaleAmt")
        Log.d("ChartData 2", "numOfGrossMargin  : $numOfGrossMargin")
        Log.d("ChartData 2", "numOfApixMargin: $numOfApixMargin")


        if (weekdays.isNullOrEmpty()) {
            binding.lineChart.clear()
            binding.lineChart.setNoDataText("No data available")
            return
        }

        val blueEntries = mutableListOf<Entry>()
        (numOfSaleAmt ?: emptyList()).forEachIndexed { index, value ->
            val yValue = value.toFloatOrNull() ?: 0f
            blueEntries.add(Entry(index.toFloat(), yValue))
        }

        val greenEntries = mutableListOf<Entry>()
        (numOfGrossMargin ?: emptyList()).forEachIndexed { index, value ->
            val yValue = value.toFloatOrNull() ?: 0f
            greenEntries.add(Entry(index.toFloat(), yValue))
        }

        val pinkEntries = mutableListOf<Entry>()
        (numOfApixMargin ?: emptyList()).forEachIndexed { index, value ->
            val yValue = value.toFloatOrNull() ?: 0f
            pinkEntries.add(Entry(index.toFloat(), yValue))
        }

        // Blue line → numOfSaleAmt
        val blueDataSet = LineDataSet(blueEntries, "Sales Amt").apply {
            color = Color.parseColor("#61BAF2")
            setDrawValues(false)
            setCircleColor(Color.parseColor("#61BAF2"))
            circleRadius = 4f
            lineWidth = 2f
            setDrawFilled(true)
            fillColor = Color.parseColor("#61BAF2")
            fillAlpha = 60
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        // Green line → gross margin
        val greenDataSet = LineDataSet(greenEntries, "Gross Margin").apply {
            color = Color.parseColor("#7E3439")
            setDrawValues(false)
            setCircleColor(Color.parseColor("#7E3439"))
            circleRadius = 4f
            lineWidth = 2f
            setDrawFilled(true)
            fillColor = Color.parseColor("#7E3439")
            fillAlpha = 60
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        // Pink line → apix margin
        val pinkDataSet = LineDataSet(pinkEntries, "Apix Margin").apply {
            color = Color.parseColor("#DB2FB5")
            setDrawValues(false)
            setCircleColor(Color.parseColor("#DB2FB5"))
            circleRadius = 4f
            lineWidth = 2f
            setDrawFilled(true)
            fillColor = Color.parseColor("#DB2FB5")
            fillAlpha = 60
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        // Assign datasets to chart
        binding.lineChart.data = LineData(blueDataSet, greenDataSet, pinkDataSet)

        // Chart setup
        binding.lineChart.description.isEnabled = false
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.legend.isEnabled = true

        val leftAxis = binding.lineChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.granularity = 1f

        val xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(weekdays)

        binding.lineChart.invalidate()
    }

    private fun setConversionChart() {
        // Data for the first line (blue)
        val blueEntries = mutableListOf<Entry>()
        blueEntries.add(Entry(1f, 20f))
        blueEntries.add(Entry(2f, 40f))
        blueEntries.add(Entry(3f, 10f))
        blueEntries.add(Entry(4f, 60f))
        blueEntries.add(Entry(5f, 80f))

        // Data for the second line (green)
        val greenEntries = mutableListOf<Entry>()
        greenEntries.add(Entry(1f, 30f))
        greenEntries.add(Entry(2f, 50f))
        greenEntries.add(Entry(3f, 25f))
        greenEntries.add(Entry(4f, 70f))
        greenEntries.add(Entry(5f, 90f))


        val blueDataSet = LineDataSet(blueEntries, "")
        blueDataSet.color = Color.parseColor("#0966FF")
        blueDataSet.setDrawValues(false)
        blueDataSet.setCircleColor(Color.parseColor("#0966FF"))
        blueDataSet.circleRadius = 4f


        val greenDataSet = LineDataSet(greenEntries, "")
        greenDataSet.color = Color.parseColor("#90CDBB")
        greenDataSet.setDrawValues(false)
        greenDataSet.setCircleColor(Color.parseColor("#90CDBB"))
        greenDataSet.circleRadius = 4f

        val lineData = LineData(blueDataSet, greenDataSet)
        binding.lineChartConversion.data = lineData

        binding.lineChartConversion.description.isEnabled = false
        binding.lineChartConversion.axisRight.isEnabled = false
        binding.lineChartConversion.xAxis.isEnabled = true


        val leftAxis = binding.lineChartConversion.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 100f
        leftAxis.labelCount = 5
        leftAxis.granularity = 25f


        val xAxis = binding.lineChartConversion.xAxis
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 6f
        xAxis.labelCount = 7
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        binding.lineChartConversion.legend.isEnabled = false
        binding.lineChartConversion.invalidate()
    }
}