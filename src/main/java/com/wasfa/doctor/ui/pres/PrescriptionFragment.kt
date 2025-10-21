package com.wasfa.doctor.ui.pres

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentPrescriptionBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.ui.main.DoctorHomeActivity
import com.wasfa.doctor.ui.pres.adapter.PresListAdapter
import com.wasfa.doctor.ui.pres.add.AddPresFragment
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class PrescriptionFragment : Fragment() {

    private var _binding: FragmentPrescriptionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: PresListAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    var searchValue: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        manageClick()
        setViewModel()
        if (isTablet()){
            manageTabPresRecyclerView()
        }else{
            managePresRecyclerView()
        }
        setUpPagination()
        callPresAPI()
        manageSearch()
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
                    callPresAPI()
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })
    }

    private fun callPresAPI() {

        binding.recyclerPres.visibility = View.GONE
        binding.txtNoData.visibility = View.GONE

        viewModel.currentPagePres = 1
        viewModel.clearReportListData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.PresRequest(
            page_no = "1",
            per_page = "5",
            keyword = searchValue,
            userId = appPreferences.getPatientId().toString()
        )
        binding.progressBar.visibility = View.VISIBLE

        viewModel.getPresList(appPreferences.getToken().toString(), request)
    }
    private fun setUpPagination() {
        binding.recyclerPres.isNestedScrollingEnabled = false
        scrollListener = ViewTreeObserver.OnScrollChangedListener {
            if (_binding == null) return@OnScrollChangedListener

            val view = binding.nestedScrollView.getChildAt(binding.nestedScrollView.childCount - 1) as View
            val diff: Int = view.bottom - (binding.nestedScrollView.height + binding.nestedScrollView.scrollY)
            if (diff == 0) {
                if (viewModel.isLastPagePres()) {
                    // No action for the last page
                } else {
                    viewModel.currentPagePres++
                    viewModel.loadNextPagePres(searchValue,AppPreferences.getInstance(requireContext()).getPatientId().toString())
                    binding.progressBarSmall.visibility = View.VISIBLE
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
    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    private fun manageClick() {
        binding.imgMenu.setOnClickListener {
            val navController = runCatching { findNavController() }.getOrNull()
            if (navController?.currentDestination != null) {
                navController.popBackStack()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
        binding.cardAddNewPres.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddPresFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun managePresRecyclerView() {

        productAdapter = PresListAdapter(
            mutableListOf()
        ) { product, type ->
            AppPreferences.getInstance(requireContext()).savePresID(product?.id.toString())
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.wasfa.doctor.ui.pres.PresDetailsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerPres.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = productAdapter
        }

    }

    private fun manageTabPresRecyclerView() {

        productAdapter = PresListAdapter(
            mutableListOf()
        ) { product, type ->
            AppPreferences.getInstance(requireContext()).savePresID(product?.id.toString())
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.wasfa.doctor.ui.pres.PresDetailsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerPres.apply {
            layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
                flexWrap = FlexWrap.WRAP
            }


            adapter = productAdapter
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


        viewModel.presEvent.observe(viewLifecycleOwner) { message ->
            if (message == "0"){
                binding.txtNoData.visibility = View.VISIBLE
            }else{
                binding.txtNoData.visibility = View.GONE
            }
            binding.progressBar.visibility = View.GONE

        }
        viewModel.presListData.observe(viewLifecycleOwner) { data ->

            if (!isTablet()){
                (activity as? DoctorHomeActivity)?.showBottomNav()
            }else{
                (activity as? DoctorHomeActivity)?.hideBottomNav()
            }
            binding.progressBarSmall.visibility = View.GONE

            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountPres = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPagePres == 1) {

                binding.recyclerPres.visibility = View.VISIBLE
                productAdapter.setProducts(data?.prescriptions?.toMutableList() ?: mutableListOf())


            } else {
                productAdapter.addProducts(data?.prescriptions!!)
            }
        }
    }
}