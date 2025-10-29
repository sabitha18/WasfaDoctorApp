package com.wasfa.doctor.ui.report

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentPrescribedRxBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.ui.main.DoctorHomeActivity
import com.wasfa.doctor.ui.pres.add.AddMedPOSNewEditFragment
import com.wasfa.doctor.ui.pres.edit.EditRXFragment
import com.wasfa.doctor.ui.pres.view.PresViewFragment
import com.wasfa.doctor.ui.report.adapter.ReportRXAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class PrescribedRxFragment : Fragment() {
    private var _binding: FragmentPrescribedRxBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: ReportRXAdapter
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
        _binding = FragmentPrescribedRxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageClick()
        setViewModel()
        managePresRecyclerView()
        setUpPagination()
        callReportAPI()
        if (!isTablet()) {
            (activity as? DoctorHomeActivity)?.showBottomNav()
            binding.imgMenu.visibility = View.GONE
        } else {
            binding.imgMenu.visibility = View.VISIBLE
            (activity as? DoctorHomeActivity)?.hideBottomNav()
        }
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
                    viewModel.loadNextPagePrescribedRX()
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

    private fun manageClick() {
        binding.imgMenu.setOnClickListener {
            (activity as? DoctorHomeActivity)?.toggleBottomNav()
        }
        binding.cardBack.setOnClickListener {
            val navController = runCatching { findNavController() }.getOrNull()
            if (navController?.currentDestination != null) {
                navController.popBackStack()
            } else {
                parentFragmentManager.popBackStack()
            }
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
            binding.progressBarSmall.visibility = View.GONE


        }
        viewModel.rxData.observe(viewLifecycleOwner) { data ->
            val list = data?.list ?: emptyList()
            if (list.isNotEmpty()) {
                productAdapter.setProducts(list.toMutableList())
            }
        }


    }

    private fun managePresRecyclerView() {

        productAdapter = ReportRXAdapter(
            mutableListOf(),
            AppPreferences.getInstance(requireContext()).getEditStatus().toString()
        ) { product, type ->

            if (type == "view"){
                AppPreferences.getInstance(requireContext()).saveNewRXStatus(product?.prescription_id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PresViewFragment())
                    .addToBackStack(null)
                    .commit()
            }else{
                AppPreferences.getInstance(requireContext()).saveNewRXStatus(product?.prescription_id)
                AppPreferences.getInstance(requireContext()).saveFavStatus("1")
                AppPreferences.getInstance(requireContext()).savePatientId(product?.customerId)

                if (isTablet()){
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AddMedPOSNewEditFragment())
                        .addToBackStack(null)
                        .commit()
                }else{
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, EditRXFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        binding.recyclerPres.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = productAdapter
        }

    }

    private fun callReportAPI() {

        viewModel.currentPageReport = 1
        viewModel.clearRXListData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.PrescribedRXRequest(
            page_no = "1",
            per_page = "10"
        )
        binding.progressBar.visibility = View.VISIBLE

        viewModel.getPrescribedRX(appPreferences.getToken().toString(), request)
    }
}