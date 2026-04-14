package com.wasfa.doctor.ui.med

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
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentMeditationBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.Brands
import com.wasfa.doctor.network.response.InfluencerListResponse
import com.wasfa.doctor.network.response.MedicalRepListResponse
import com.wasfa.doctor.network.response.ProductListResponse
import com.wasfa.doctor.network.response.SellerListResponse
import com.wasfa.doctor.ui.main.DoctorHomeActivity
import com.wasfa.doctor.ui.med.adapter.FilterBrandAdapter
import com.wasfa.doctor.ui.med.adapter.MedAdapter
import com.wasfa.doctor.ui.med.adapter.FilterInfluencerAdapter
import com.wasfa.doctor.ui.med.adapter.FilterMedicalRepAdapter
import com.wasfa.doctor.ui.med.adapter.FilterSellerAdapter
import com.wasfa.doctor.ui.details.MedDetailsFragment
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MeditationFragment : Fragment() {
    private var _binding: FragmentMeditationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: MedAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    var searchValue: String = ""
    private var selectedInfluencerId: String = ""
    private var selectedBrandId: String = ""
    private var selectedSellerId: String = ""
    private var selectedMedicalRepId: String = ""
    private val brandList = mutableListOf<Brands>()
    private lateinit var brandAdapter: FilterBrandAdapter
    private var currentSearchQuery = ""
    private var searchJob: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeditationBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        handleClick()
        setViewModel()
        setUpPagination()
        callProductAPI()
        manageSearch()
        handleFilterClick()

        setupBrandAdapter()

        viewModel.currentPageBrand = 1
        brandList.clear()
        brandAdapter.notifyDataSetChanged()

        setBrandPagination()
        loadNextPage(currentSearchQuery)

        if (AppPreferences.getInstance(requireContext()).getFavStatus() == "1"){
            binding.txtTitle!!.text = "My List"
            binding.cardMyList.setCardBackgroundColor(Color.parseColor("#A61C5C"))
            binding.cardAllProducts.setCardBackgroundColor(Color.parseColor("#FFFFFF"))

            binding.txtMyList.setTextColor((Color.parseColor("#FFFFFF")))
            binding.txtAllProducts.setTextColor((Color.parseColor("#A61C5C")))
        }else{
            binding.txtTitle!!.text = "Available Medicine"
            binding.cardMyList.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.cardAllProducts.setCardBackgroundColor(Color.parseColor("#A61C5C"))

            binding.txtMyList.setTextColor((Color.parseColor("#A61C5C")))
            binding.txtAllProducts.setTextColor((Color.parseColor("#FFFFFF")))
        }


    }

    private fun setBrandPagination() {

            binding.pageFilter.recyclerFilterByBrand.clearOnScrollListeners() // 🔥 FIX

            binding.pageFilter.recyclerFilterByBrand.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!viewModel.loadingBrand &&
                        !viewModel.isLastPageBrand() &&
                        (visibleItemCount + firstVisibleItemPosition >= totalItemCount)
                    ) {
                        loadNextPage(currentSearchQuery)
                    }
                }
            })

    }
    private fun loadNextPage(search: String = "") {

        if (viewModel.loadingBrand || viewModel.isLastPageBrand()) return

        val request = ApiService.BrandRequestNew(
            per_page = "20",
            page_no = viewModel.currentPageBrand.toString(),
            search = search

        )

        viewModel.getBrandList(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }
    private fun handleFilterClick() {
        binding.pageFilter.editBrandSearch.addTextChangedListener { text ->

            searchJob?.cancel()

            searchJob = lifecycleScope.launch {
                delay(1000) // wait for typing

                currentSearchQuery = text.toString().trim()

                viewModel.currentPageBrand = 1
                brandList.clear()
                brandAdapter.notifyDataSetChanged()

                loadNextPage(currentSearchQuery)
            }
        }
        binding.pageFilter.cardFilterSeller.visibility = View.GONE
        binding.pageFilter.cardFilterMedicalRep.visibility = View.GONE

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
            callProductAPI()
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

            callProductAPI()
        }
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
                    binding.pageFilter.editFilterByProducts.setText(searchValue)
                    callProductAPI()
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })
        binding.pageFilter.editFilterByProducts.addTextChangedListener(object : TextWatcher {
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
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })
    }

    private fun setUpRecyclerView() {
        productAdapter = MedAdapter(
            mutableListOf()
        ) { product, type ->

            if (type == "add"){
                binding.progressBar.visibility = View.VISIBLE

                val request = ApiService.AddCartRequest(
                    productId = product?.id.toString(),
                    quantity = "1"
                )
                viewModel.addCartShop(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
            } else if (type == "fav"){
            binding.progressBar.visibility = View.VISIBLE

            val request = ApiService.FavRequest(
                id = product?.id.toString(),
                status = "1"
            )
            viewModel.updateFav(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
        }else if (type == "unFav"){
                binding.progressBar.visibility = View.VISIBLE

                val request = ApiService.FavRequest(
                    id = product?.id.toString(),
                    status = "0"
                )
                viewModel.updateFav(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
            }else{
                AppPreferences.getInstance(requireContext()).saveProductId(product?.id.toString())
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,
                        MedDetailsFragment()
                    )
                    .addToBackStack(null)
                    .commit()
            }

        }

        binding.recyclerMed.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = productAdapter
        }
    }
    private fun callProductAPI() {

        binding.recyclerMed.visibility = View.GONE
        binding.txtNoData.visibility = View.GONE

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
            listFrom = ""
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
                    viewModel.loadNextPage(selectedMedicalRepId,selectedSellerId,searchValue,selectedInfluencerId,binding.pageFilter.editFilterSearchBySku.text.toString(),selectedBrandId,AppPreferences.getInstance(requireContext()).getFavStatus().toString(),"")

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
            showAlertCustom(message)

        }
        viewModel.favStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.addCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            viewModel.getCartCount(appPreferences.getToken().toString())

        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.productListData.observe(viewLifecycleOwner) { data ->


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
            if (!isTablet()){
                (activity as? DoctorHomeActivity)?.showBottomNav()
            }else{
                (activity as? DoctorHomeActivity)?.hideBottomNav()
            }
        }
        viewModel.influencerList.observe(viewLifecycleOwner) { data ->

            manageInfluencerFilter(data)
        }
        viewModel.brandList.observe(viewLifecycleOwner) { data ->

            val newList = data?.brands ?: emptyList()

            val start = brandList.size
            brandList.addAll(newList)

            brandAdapter.notifyItemRangeInserted(start, newList.size)
        }
        viewModel.sellerList.observe(viewLifecycleOwner) { data ->
            manageSellerFilter(data)
        }
        viewModel.medicalRepList.observe(viewLifecycleOwner) { data ->
            manageMedicalRepFilter(data)
        }


        viewModel.getInfluencerList(appPreferences.getToken().toString())
       // viewModel.getBrandList(appPreferences.getToken().toString(),request)
//        viewModel.getSellerList(appPreferences.getToken().toString())
//        viewModel.getMedicalRepList(appPreferences.getToken().toString())
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

    private fun closeBrand() {
        binding.pageFilter.imgFilterByBrandArrow.rotation = 0f
        binding.pageFilter.cardFilterByBrandHide.visibility = View.GONE
    }
    private fun setupBrandAdapter() {
        brandAdapter = FilterBrandAdapter(brandList) { data, position ->
            selectedBrandId = data.id.toString()
            binding.pageFilter.txtFilterByBrand.text = data.name
            closeBrand()
        }

        binding.pageFilter.recyclerFilterByBrand.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = brandAdapter
        }
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
    fun showAlertCustom(message: String) {
        val builder = AlertDialog.Builder(requireContext())


        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.validation_alert, null)
        builder.setView(customLayout)
        val text_validation = customLayout.findViewById<TextView>(R.id.text_validation)
        text_validation.text = message
        val continueShoppingButton = customLayout.findViewById<MaterialCardView>(R.id.view_cart)
        lateinit var dialog: AlertDialog
        continueShoppingButton.setOnClickListener {
            if (message == "Internal Server Error"){
                findNavController().popBackStack()
                dialog?.dismiss()
            }else{
                dialog?.dismiss()
            }

        }

        dialog = builder.create()
        dialog.show()
    }
    private fun handleClick() {
        binding.cardMyList.setOnClickListener {
            binding.cardMyList.setCardBackgroundColor(Color.parseColor("#A61C5C"))
            binding.cardAllProducts.setCardBackgroundColor(Color.parseColor("#FFFFFF"))

            binding.txtMyList.setTextColor((Color.parseColor("#FFFFFF")))
            binding.txtAllProducts.setTextColor((Color.parseColor("#A61C5C")))
            binding.progressBarSmall.visibility = View.GONE
            AppPreferences.getInstance(requireContext()).saveFavStatus("1")
            callProductAPI()
        }

        binding.cardAllProducts.setOnClickListener {
            binding.cardMyList.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.cardAllProducts.setCardBackgroundColor(Color.parseColor("#A61C5C"))

            binding.txtMyList.setTextColor((Color.parseColor("#A61C5C")))
            binding.txtAllProducts.setTextColor((Color.parseColor("#FFFFFF")))
            binding.progressBarSmall.visibility = View.GONE
            AppPreferences.getInstance(requireContext()).saveFavStatus("0")
            callProductAPI()
        }
        binding.imgMenu.setOnClickListener {
            (activity as? DoctorHomeActivity)?.toggleBottomNav()
        }
        binding.cardFilter.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.VISIBLE
        }
        binding.pageFilter.imgClose.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.GONE
        }
    }
    override fun onDestroyView() {
        // Safely check if _binding is not null before accessing it
        _binding?.let { binding ->
            scrollListener?.let { listener ->
                binding.nestedScrollView.viewTreeObserver.removeOnScrollChangedListener(listener)
            }
        }
        scrollListener = null
        _binding = null

        super.onDestroyView()
        searchRunnable?.let { searchHandler.removeCallbacks(it)}
    }

    override fun onResume() {
        super.onResume()
        AppPreferences.getInstance(requireContext()).saveAddressId(null)
        AppPreferences.getInstance(requireContext()).saveCustName(null)
        AppPreferences.getInstance(requireContext()).clearAddress()
    }

    private fun manageProducts(data: ProductListResponse?) {

        binding.recyclerMed.visibility = View.VISIBLE
        productAdapter.setProducts(data?.products?.toMutableList() ?: mutableListOf())
    }

}