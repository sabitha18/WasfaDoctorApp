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
import com.wasfa.doctor.databinding.FragmentPrescribedRxBinding
import com.wasfa.doctor.databinding.FragmentReportBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.doctor.main.DoctorHomeActivity
import com.wasfa.doctor.doctor.pres.add.AddMedNewFragment
import com.wasfa.doctor.doctor.pres.add.AddMedPOSNewEditFragment
import com.wasfa.doctor.doctor.pres.add.AddMedPOSNewFragment
import com.wasfa.doctor.doctor.report.adapter.FilterClearanceAdapter
import com.wasfa.doctor.doctor.report.adapter.FilterNewAdapter
import com.wasfa.doctor.doctor.report.adapter.ReportAdapter
import com.wasfa.doctor.doctor.report.adapter.ReportRXAdapter
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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
        } else {
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
            mutableListOf()
        ) { product, type ->
            AppPreferences.getInstance(requireContext()).saveNewRXStatus(product?.prescription_id)
            AppPreferences.getInstance(requireContext()).saveFavStatus("1")
            AppPreferences.getInstance(requireContext()).savePatientId(product?.customerId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddMedPOSNewEditFragment())
                    .addToBackStack(null)
                    .commit()

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