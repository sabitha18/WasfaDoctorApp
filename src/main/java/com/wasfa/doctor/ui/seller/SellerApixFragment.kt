package com.wasfa.doctor.ui.seller

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
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentSellerApixBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.SCPLResponse
import com.wasfa.doctor.ui.seller.adapter.SellerProductAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class SellerApixFragment : Fragment() {
    private var _binding: FragmentSellerApixBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private var conditions = "all"
    private var nilConditions = "0"
    private lateinit var productAdapter: SellerProductAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    var searchValue: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setUpPagination()
        callProductAPI()
        manageSearch()
        setUpRecyclerView()
        handleClick()
        handleRadioClick()
        checkButtonStatus()
        setupTextWatchers()

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
                    callProductAPI()
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })

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
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }

    private fun callProductAPI() {

        binding.recyclerList.visibility = View.GONE

        viewModel.currentPageSellerProduct = 1
        viewModel.clearSellerCommissionProductListData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.SCPLRequest(
            pageNo = "1",
            perPage = "4",
            search = searchValue,
            sellerId = appPreferences.getSellerId().toString(),
        )
        binding.progressBar.visibility = View.VISIBLE

        viewModel.getSellerCommissionProductList(appPreferences.getToken().toString(), request)
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
                if (viewModel.isLastPageSellerProduct()) {
                    // No action for the last page
                } else {
                    viewModel.currentPageSellerProduct++
                    viewModel.loadNextPageSellerCommissionProduct(
                        searchValue,
                        AppPreferences.getInstance(requireContext()).getSellerId().toString()
                    )
                    binding.progressBarSmall.visibility = View.VISIBLE
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
        productAdapter = SellerProductAdapter(
            mutableListOf()
        ) { product, value ->

            binding.progressBar.visibility = View.VISIBLE
            val request = ApiService.CommissionRequest(
                id = product?.id.toString(),
                percentage = value
            )
            viewModel.changeCommission(AppPreferences.getInstance(requireContext()).getToken().toString(),request)

        }

        binding.recyclerList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = productAdapter
        }
    }

    private fun handleRadioClick() {
        binding.radioAll.setOnClickListener {

            conditions = "all"

            binding.cardRadioAll.visibility = View.VISIBLE
            binding.cardRadioNil.visibility = View.GONE
            binding.cardRadioPharma.visibility = View.GONE
            binding.cardRadioNonPharma.visibility = View.GONE
        }
        binding.radioPharma.setOnClickListener {

            conditions = "isPharmaceutical"

            binding.cardRadioAll.visibility = View.GONE
            binding.cardRadioNil.visibility = View.GONE
            binding.cardRadioPharma.visibility = View.VISIBLE
            binding.cardRadioNonPharma.visibility = View.GONE
        }
        binding.radioNonPharma.setOnClickListener {

            conditions = "nonPharma"

            binding.cardRadioAll.visibility = View.GONE
            binding.cardRadioNil.visibility = View.GONE
            binding.cardRadioPharma.visibility = View.GONE
            binding.cardRadioNonPharma.visibility = View.VISIBLE
        }

        binding.radioNil.setOnClickListener {

            conditions = "all"
            nilConditions = "1"

            binding.cardRadioAll.visibility = View.GONE
            binding.cardRadioNil.visibility = View.VISIBLE
            binding.cardRadioPharma.visibility = View.GONE
            binding.cardRadioNonPharma.visibility = View.GONE
        }
    }

    private fun setupTextWatchers() {
        binding.editCommisions.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                checkButtonStatus()
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        binding.editPurchasePrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                checkButtonStatus()
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        binding.editSellingPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                checkButtonStatus()
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun checkButtonStatus() {
        if (binding.editCommisions.text.isNullOrEmpty()) {
            binding.cardSubmitCondition.isEnabled = false
            binding.cardSubmitCondition.alpha = 0.5f
        } else {
            binding.cardSubmitCondition.isEnabled = true
            binding.cardSubmitCondition.alpha = 1f
        }
        if (binding.editPurchasePrice.text.isNullOrEmpty() || binding.editSellingPrice.text.isNullOrEmpty()) {
            binding.cardCalculate.isEnabled = false
            binding.cardCalculate.alpha = 0.5f
        } else {
            binding.cardCalculate.isEnabled = true
            binding.cardCalculate.alpha = 1f
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
        viewModel.commissionStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

        }
        viewModel.changeCommissionStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

        }
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE


        }


        viewModel.calculatedValue.observe(viewLifecycleOwner) { margin ->
            binding.progressBar.visibility = View.GONE
            binding.txtMargin.text = margin

        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.SCPLData.observe(viewLifecycleOwner) { data ->


            binding.progressBarSmall.visibility = View.GONE

            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountSellerProduct = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPageSellerProduct == 1) {

                manageProduct(data)
            } else {
                productAdapter.addProducts(data?.products!!)
            }

        }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerApixBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun handleClick() {
        binding.cardSubmitCondition.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val request = ApiService.ConditionRequest(
                sellerId = AppPreferences.getInstance(requireContext()).getSellerId().toString(),
                nilCondition = nilConditions,
                conditions = conditions,
                percentage = binding.editCommisions.text.toString()

            )
            viewModel.submitCommission(
                AppPreferences.getInstance(requireContext()).getToken().toString(), request
            )
        }
        binding.cardCalculate.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE
            val request = ApiService.CalcApixRequest(
                sellingPrice = binding.editSellingPrice.text.toString(),
                purchasePrice = binding.editPurchasePrice.text.toString()
            )
            viewModel.calculateApix(
                AppPreferences.getInstance(requireContext()).getToken().toString(), request
            )
        }
        binding.imgCart.setOnClickListener {
            findNavController().navigate(R.id.nav_cart)
        }
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun manageProduct(data: SCPLResponse?) {
        binding.recyclerList.visibility = View.VISIBLE
        productAdapter.setProducts(data?.products?.toMutableList() ?: mutableListOf())
    }
}