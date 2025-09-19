package com.wasfa.doctor.doctor.pres.add

import android.content.res.Configuration
import android.graphics.Color
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentAddMedBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.Brands
import com.wasfa.doctor.network.response.InfluencerListResponse
import com.wasfa.doctor.network.response.MedicalRepListResponse
import com.wasfa.doctor.network.response.ProductListResponse
import com.wasfa.doctor.network.response.Products
import com.wasfa.doctor.network.response.SellerListResponse
import com.wasfa.doctor.doctor.main.DoctorHomeActivity
import com.wasfa.doctor.doctor.pres.adapter.MedicationAdapter
import com.wasfa.doctor.ui.pos.adapter.filter.FilterBrandAdapter
import com.wasfa.doctor.ui.pos.adapter.filter.FilterInfluencerAdapter
import com.wasfa.doctor.ui.pos.adapter.filter.FilterMedicalRepAdapter
import com.wasfa.doctor.ui.pos.adapter.filter.FilterSellerAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class AddMedFragment : Fragment() {
    private var _binding: FragmentAddMedBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: MedicationAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private lateinit var cardDose: MaterialCardView
    private lateinit var txtSelectedDose: TextView

    private lateinit var cardDoseDay: MaterialCardView
    private lateinit var txtDoseSelectedDay: TextView

    private lateinit var cardDoseTime: MaterialCardView
    private lateinit var txtDoseTime: TextView

    private lateinit var cardCourseDuration: MaterialCardView
    private lateinit var txtCourseDuration: TextView

    private lateinit var cardCourseDay: MaterialCardView
    private lateinit var txtCourseDay: TextView

    private lateinit var editNotes: EditText
    var searchValue: String = ""
    private var selectedInfluencerId: String = ""
    private var selectedBrandId: String = ""
    private var selectedSellerId: String = ""
    private var selectedMedicalRepId: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddMedBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageClick()
        setViewModel()
        setUpRecyclerView()
        setUpPagination()
        AppPreferences.getInstance(requireContext()).saveFavStatus("1")
        callProductAPI("POS")
        manageSearch()
        handleFilterClick()
    }
    private fun handleFilterClick() {

        if (AppPreferences.getInstance(requireContext()).getLoginType() == "influencer"){
            binding.pageFilter.cardFilterInfluencer.visibility = View.GONE
        }else{
            binding.pageFilter.cardFilterInfluencer.visibility = View.VISIBLE
        }


        binding.pageFilter.editFilterByProducts.setText(searchValue)
        binding.pageFilter.cardFilterInfluencer.setOnClickListener {
            if (binding.pageFilter.imgInfluencerArrow.rotation == 0f) {
                binding.pageFilter.imgInfluencerArrow.rotation = 180f
                binding.pageFilter.cardInfluencerHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgInfluencerArrow.rotation = 0f
                binding.pageFilter.cardInfluencerHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardFilterByBrand.setOnClickListener {
            if (binding.pageFilter.imgFilterByBrandArrow.rotation == 0f) {
                binding.pageFilter.imgFilterByBrandArrow.rotation = 180f
                binding.pageFilter.cardFilterByBrandHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgFilterByBrandArrow.rotation = 0f
                binding.pageFilter.cardFilterByBrandHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardFilterSeller.setOnClickListener {
            if (binding.pageFilter.imgFilterBySeller.rotation == 0f) {
                binding.pageFilter.imgFilterBySeller.rotation = 180f
                binding.pageFilter.cardFilterBySellerHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgFilterBySeller.rotation = 0f
                binding.pageFilter.cardFilterBySellerHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardFilterMedicalRep.setOnClickListener {
            if (binding.pageFilter.imgFilterMedicalRep.rotation == 0f) {
                binding.pageFilter.imgFilterMedicalRep.rotation = 180f
                binding.pageFilter.cardFilterByMedicalRepHide.visibility = View.VISIBLE
            } else {
                binding.pageFilter.imgFilterMedicalRep.rotation = 0f
                binding.pageFilter.cardFilterByMedicalRepHide.visibility = View.GONE
            }
        }
        binding.pageFilter.cardApplyFilter.setOnClickListener {
            callProductAPI("")
            binding.pageFilter.lytFilter.visibility = View.GONE
        }
        binding.pageFilter.cardClear.setOnClickListener {
            selectedInfluencerId = ""
            binding.pageFilter.txtFilterInfluencer.text = null

            binding.pageFilter.editFilterSearchBySku.text = null
            binding.pageFilter.editFilterByProducts.text = null
            binding.editSearch.text = null
            searchValue = ""

            selectedBrandId = ""
            binding.pageFilter.txtFilterByBrand.text = null

            selectedSellerId = ""
            binding.pageFilter.txtFilterBySeller.text = null

            selectedMedicalRepId = ""
            binding.pageFilter.txtFilterMedicalRep.text = null

            binding.pageFilter.lytFilter.visibility = View.GONE

            callProductAPI("")
        }
    }
    private fun manageSearch() {
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newValue = s.toString()
                if (newValue == searchValue) return  // 👈 Avoid unnecessary call if value hasn't changed

                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    searchValue = newValue
                    callProductAPI("")
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })
    }
    private fun callProductAPI(status: String) {

        binding.progressBar.visibility = View.GONE
        binding.progressBarSmall.visibility = View.GONE
        binding.txtNoData.visibility = View.GONE
        binding.recyclerMed.visibility = View.GONE

        viewModel.currentPage = 1
        viewModel.clearProductListData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.ProductRequest(
            page_no = "1",
            per_page = "4",
            category = "",
            brand = selectedBrandId,
            sku = binding.pageFilter.editFilterSearchBySku.text.toString(),
            seller = selectedSellerId,
            medical_rep_id = selectedMedicalRepId,
            keyword = searchValue,
            influencer_id = selectedInfluencerId,
            isFavourite = appPreferences.getFavStatus().toString(),
            listFrom = status
        )
        binding.progressBar.visibility = View.VISIBLE

        viewModel.getProductList(appPreferences.getToken().toString(), request)
    }
    private fun setUpPagination() {
        binding.recyclerMed.isNestedScrollingEnabled = false
        scrollListener = ViewTreeObserver.OnScrollChangedListener {
            if (_binding == null) return@OnScrollChangedListener

            val view = binding.nestedScrollView.getChildAt(binding.nestedScrollView.childCount - 1) as View
            val diff: Int = view.bottom - (binding.nestedScrollView.height + binding.nestedScrollView.scrollY)
            if (diff == 0) {
                if (viewModel.isLastPage()) {
                    // No action for the last page
                } else {
                    viewModel.currentPage++
                    val posStatus = if (AppPreferences.getInstance(requireContext()).getFavStatus() == "1") "POS" else ""
                    viewModel.loadNextPage(selectedMedicalRepId,selectedSellerId,searchValue,selectedInfluencerId,binding.pageFilter.editFilterSearchBySku.text.toString(),selectedBrandId,AppPreferences.getInstance(requireContext()).getFavStatus().toString(),posStatus)

                  if (binding.progressBar.visibility == View.VISIBLE){
                      binding.progressBarSmall.visibility = View.GONE
                  }else{
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
                binding.nestedScrollView.viewTreeObserver.removeOnScrollChangedListener(scrollListener)
            }
        })
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


        viewModel.addCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message, Toast.LENGTH_LONG).show()


        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.productListData.observe(viewLifecycleOwner) { data ->

            if (!isTablet()){
                (activity as? DoctorHomeActivity)?.showBottomNav()
            }else{
                (activity as? DoctorHomeActivity)?.hideBottomNav()
            }
            binding.progressBarSmall.visibility = View.GONE

            if (data?.totalProductsCount == "0"){
                binding.txtNoData.visibility = View.VISIBLE
            }else{
                binding.txtNoData.visibility = View.GONE
            }
            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCount = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPage == 1) {

                manageProducts(data)
            } else {
                productAdapter.addProducts(data?.products!!)
            }
        }
        viewModel.influencerList.observe(viewLifecycleOwner) { data ->

            manageInfluencerFilter(data)
        }
        viewModel.brandList.observe(viewLifecycleOwner) { data ->
            manageBrandFilter(data?.brands)
        }
        viewModel.sellerList.observe(viewLifecycleOwner) { data ->
            manageSellerFilter(data)
        }
        viewModel.medicalRepList.observe(viewLifecycleOwner) { data ->
            manageMedicalRepFilter(data)
        }
        val request = ApiService.BrandRequest(
            per_page = "1000",
            page_no = "1"
        )
        viewModel.getInfluencerList(appPreferences.getToken().toString())
        viewModel.getBrandList(appPreferences.getToken().toString(),request)
        viewModel.getSellerList(appPreferences.getToken().toString())
        viewModel.getMedicalRepList(appPreferences.getToken().toString())

    }
    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    private fun manageMedicalRepFilter(data: List<MedicalRepListResponse>?) {
        binding.pageFilter.recyclerFilterByMedicalRep.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterMedicalRepAdapter(data) { data, position ->

                selectedMedicalRepId = data?.id.toString()
                binding.pageFilter.txtFilterMedicalRep.text = data?.name
                closeMedicalRep()
            }
            adapter = catAdapter
        }
    }
    private fun closeMedicalRep() {
        binding.pageFilter.imgFilterMedicalRep.rotation = 0f
        binding.pageFilter.cardFilterByMedicalRepHide.visibility = View.GONE
    }
    private fun manageSellerFilter(data: List<SellerListResponse>?) {
        binding.pageFilter.recyclerFilterBySeller.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterSellerAdapter(data) { data, position ->

                selectedSellerId = data?.id.toString()
                binding.pageFilter.txtFilterBySeller.text = data?.name
                closeSeller()
            }
            adapter = catAdapter
        }
    }
    private fun closeSeller() {
        binding.pageFilter.imgFilterBySeller.rotation = 0f
        binding.pageFilter.cardFilterBySellerHide.visibility = View.GONE
    }
    private fun manageBrandFilter(data: List<Brands>?) {
        binding.pageFilter.recyclerFilterByBrand.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterBrandAdapter(data) { data, position ->

                selectedBrandId = data?.id.toString()
                binding.pageFilter.txtFilterByBrand.text = data?.name
                closeBrand()
            }
            adapter = catAdapter
        }
    }
    private fun closeBrand() {
        binding.pageFilter.imgFilterByBrandArrow.rotation = 0f
        binding.pageFilter.cardFilterByBrandHide.visibility = View.GONE
    }
    private fun manageInfluencerFilter(data: List<InfluencerListResponse>?) {
        binding.pageFilter.recyclerInfluencer.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterInfluencerAdapter(data,selectedInfluencerId) { data, position ->

                selectedInfluencerId = data?.id.toString()
                binding.pageFilter.txtFilterInfluencer.text = data?.name
                closeInfluencer()
            }
            adapter = catAdapter
        }
    }
    private fun closeInfluencer() {
        binding.pageFilter.imgInfluencerArrow.rotation = 0f
        binding.pageFilter.cardInfluencerHide.visibility = View.GONE
    }
    private fun manageProducts(data: ProductListResponse?) {

        binding.recyclerMed.visibility = View.VISIBLE
        productAdapter.setProducts(data?.products?.toMutableList() ?: mutableListOf())
    }
    private fun setUpRecyclerView() {
        productAdapter = MedicationAdapter(
            mutableListOf()
        ) { product, type ->

            if (type == "add"){
                showAddPopup(product)
            }
            else{
                AppPreferences.getInstance(requireContext()).saveProductId(product?.id.toString())
                findNavController().navigate(R.id.nav_pos_shop_details)
            }

        }

        binding.recyclerMed.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = productAdapter
        }
    }
    private fun manageClick() {
        binding.cardMyList.setOnClickListener {
            binding.cardMyList.setCardBackgroundColor(Color.parseColor("#A61C5C"))
            binding.cardAllProducts.setCardBackgroundColor(Color.parseColor("#FFFFFF"))

            binding.txtMyList.setTextColor((Color.parseColor("#FFFFFF")))
            binding.txtAllProducts.setTextColor((Color.parseColor("#A61C5C")))
            binding.progressBarSmall.visibility = View.GONE
            AppPreferences.getInstance(requireContext()).saveFavStatus("1")
            callProductAPI("POS")
        }

        binding.cardAllProducts.setOnClickListener {
            binding.cardMyList.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.cardAllProducts.setCardBackgroundColor(Color.parseColor("#A61C5C"))

            binding.txtMyList.setTextColor((Color.parseColor("#A61C5C")))
            binding.txtAllProducts.setTextColor((Color.parseColor("#FFFFFF")))
            binding.progressBarSmall.visibility = View.GONE
            AppPreferences.getInstance(requireContext()).saveFavStatus("0")
            callProductAPI("")
        }

        binding.cardFilter.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.VISIBLE
        }
        binding.pageFilter.imgClose.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.GONE
        }
        binding.cardBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }



    private fun showAddPopup(data: Products) {
        val builder = AlertDialog.Builder(requireContext())


        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.popup_add_med, null)
        builder.setView(customLayout)
        val imgProduct = customLayout.findViewById<ImageView>(R.id.img_product)
        val txtProductName = customLayout.findViewById<TextView>(R.id.txt_product_name)
        val txtStoreName = customLayout.findViewById<TextView>(R.id.txt_store_name)

        txtProductName.text = data?.name
        txtStoreName.text = data?.sellerName

        Glide.with(requireContext())
            .load(data?.thumbnail_image)
            .into(imgProduct)


        cardDose = customLayout.findViewById<MaterialCardView>(R.id.card_dose)
        txtSelectedDose = customLayout.findViewById<TextView>(R.id.txt_selected_dose)
        cardDoseDay = customLayout.findViewById<MaterialCardView>(R.id.card_dose_day)
        txtDoseSelectedDay = customLayout.findViewById<TextView>(R.id.txt_dose_selected_day)
        cardDoseTime = customLayout.findViewById<MaterialCardView>(R.id.card_dose_time)
        txtDoseTime = customLayout.findViewById<TextView>(R.id.txt_dose_time)
        cardCourseDuration = customLayout.findViewById<MaterialCardView>(R.id.card_course_duration)
        txtCourseDuration = customLayout.findViewById<TextView>(R.id.txt_course_duration)
        cardCourseDay = customLayout.findViewById<MaterialCardView>(R.id.card_course_day)
        txtCourseDay= customLayout.findViewById<TextView>(R.id.txt_course_day)
        editNotes= customLayout.findViewById<EditText>(R.id.edit_notes)

        cardDose.setOnClickListener {
            showDoseDropdown()
        }
        cardCourseDuration.setOnClickListener {
            showCourseDurationDropdown()
        }
        cardDoseDay.setOnClickListener {
            showDoseDayDropdown()
        }
        cardDoseTime.setOnClickListener {
            showDoseTimeDropdown()
        }
        cardCourseDay.setOnClickListener {
            showCourseDayDropdown()
        }
        val cardCancel = customLayout.findViewById<MaterialCardView>(R.id.card_cancel)
        val cardAdd = customLayout.findViewById<MaterialCardView>(R.id.card_add)
        lateinit var dialog: AlertDialog
        cardCancel.setOnClickListener {
            dialog?.dismiss()
        }
        cardAdd.setOnClickListener {
            dialog?.dismiss()
            binding.progressBar.visibility = View.VISIBLE
            val request = ApiService.AddCartRXRequest(
                productId = data?.id.toString(),
                quantity = "1",
                patientId = AppPreferences.getInstance(requireContext()).getPatientId().toString(),
                dose_day = txtDoseSelectedDay.text.toString().takeIf { it != "Select" } ?: "",
                description = editNotes.text.toString(),
                dose = txtSelectedDose.text.toString().takeIf { it != "Select" } ?: "",
                course_day = txtCourseDay.text.toString().takeIf { it != "Select" } ?: "",
                dose_time = txtDoseTime.text.toString().takeIf { it != "Select" } ?: "",
                course_duration = txtCourseDuration.text.toString().takeIf { it != "Select" } ?: ""
            )

            viewModel.addCartRX(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
        }

        dialog = builder.create()
        dialog.show()
    }

    private fun showDoseDropdown() {
        val doses = listOf("Daily", "Weekly", "Monthly")
        val popup = android.widget.PopupMenu(requireContext(), cardDose)
        doses.forEachIndexed { index, dose ->
            popup.menu.add(0, index, index, dose)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedDose = doses[menuItem.itemId]

            txtSelectedDose.text = selectedDose
            true
        }
        popup.show()
    }

    private fun showCourseDurationDropdown() {
        val doses = listOf("1", "2", "3","4","5","6","7","8","9","10")
        val popup = android.widget.PopupMenu(requireContext(), cardCourseDuration)
        doses.forEachIndexed { index, dose ->
            popup.menu.add(0, index, index, dose)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedDose = doses[menuItem.itemId]

            txtCourseDuration.text = selectedDose

            true
        }
        popup.show()
    }

    private fun showDoseDayDropdown() {
        val doses = listOf("1", "2", "3","4","5","6","7","8","9","10")
        val popup = android.widget.PopupMenu(requireContext(), cardDoseDay)
        doses.forEachIndexed { index, dose ->
            popup.menu.add(0, index, index, dose)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedDose = doses[menuItem.itemId]

            txtDoseSelectedDay.text = selectedDose

            true
        }
        popup.show()
    }

    private fun showDoseTimeDropdown() {
        val doses = listOf("Before Meal", "After Meal", "Morning","Night","Any Time")
        val popup = android.widget.PopupMenu(requireContext(), cardDoseTime)
        doses.forEachIndexed { index, dose ->
            popup.menu.add(0, index, index, dose)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedDose = doses[menuItem.itemId]

            txtDoseTime.text = selectedDose
            true
        }
        popup.show()
    }

    private fun showCourseDayDropdown() {
        val doses = listOf("Day", "Weak", "Month")
        val popup = android.widget.PopupMenu(requireContext(), cardCourseDay)
        doses.forEachIndexed { index, dose ->
            popup.menu.add(0, index, index, dose)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedDose = doses[menuItem.itemId]

            txtCourseDay.text = selectedDose
            true
        }
        popup.show()
    }
}