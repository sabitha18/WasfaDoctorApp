package com.wasfa.doctor.ui.customers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
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
import com.wasfa.doctor.databinding.FragmentCustomerBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CustomerListResponse
import com.wasfa.doctor.network.response.Segments
import com.wasfa.doctor.network.response.Types
import com.wasfa.doctor.network.response.UserDetails
import com.wasfa.doctor.ui.customers.adapter.CustomerListAdapter
import com.wasfa.doctor.ui.customers.adapter.SegmentListingAdapter
import com.wasfa.doctor.ui.customers.adapter.TypeListingAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class CustomerFragment : Fragment() {
    private var _binding: FragmentCustomerBinding? = null
    private val binding get() = _binding!!
    private lateinit var segmentAdapter: SegmentListingAdapter
    private lateinit var typeAdapter: TypeListingAdapter
    private lateinit var viewModel: HomeViewModel
    private val customerList = mutableListOf<UserDetails>()
    private var typeList: MutableList<Types> = mutableListOf()
    private var segmentList: MutableList<Segments> = mutableListOf()


    private lateinit var custAdapter: CustomerListAdapter
    private var isLoading = false
    private val visibleThreshold = 5
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        setViewModel()
        callCustomerAPI()
        setUpRecyclerView()
        setUpPagination()
    }

    private fun setUpPagination() {
        binding.recyclerList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!viewModel.isLastPageCustomer() && !isLoading &&
                    totalItemCount <= lastVisibleItemPosition + visibleThreshold
                ) {

                    isLoading = true
                    viewModel.currentPageCustomer++
                    binding.progressBarSmall.visibility = View.VISIBLE

                    viewModel.loadNextPageCustomer("")

                }
            }
        })

    }

    private fun manageCustomers(data: CustomerListResponse) {
        binding.recyclerList.visibility = View.VISIBLE
        custAdapter.setProducts(data?.users?.toMutableList() ?: mutableListOf())

    }

    private fun setUpRecyclerView() {
        custAdapter = CustomerListAdapter(
            mutableListOf(),
            emptyList(),
            emptyList()
        ) { product, type ->

            if (type == "edit") {
                AppPreferences.getInstance(requireContext()).saveCustId(product?.id)
                findNavController().navigate(R.id.nav_customer_edit)
            } else if (type == "delete") {
                showDeletePopup(product?.id.toString())
            } else if (type == "ban") {
                showBanPopup(product?.id.toString(),product?.banned.toString())
            } else if (type == "segment") {
                showSegmentDialog(product?.id.toString())

            } else if (type == "type") {
                showTypeDialog(product?.id.toString())

            }

        }

        binding.recyclerList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = custAdapter
        }
    }
    private fun showTypeDialog(id: String) {
        viewModel.getTypeData(AppPreferences.getInstance(requireContext()).getToken().toString())
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_segments, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val edtSearch = dialogView.findViewById<EditText>(R.id.edit_search)
        val rvSku = dialogView.findViewById<RecyclerView>(R.id.rvSkuList)
        val progressBar = dialogView.findViewById<RelativeLayout>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        edtSearch.visibility = View.VISIBLE

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        typeAdapter = TypeListingAdapter { data ->
            callTypeSegment(data?.id.toString(), id)
            dialog.dismiss()
        }
        viewModel.typeData.observe(viewLifecycleOwner) { data ->
            progressBar.visibility = View.GONE
            typeList.clear()
            data?.types?.let {
                typeList.addAll(it)   // add list of Segments
            }

            typeAdapter.submitList(typeList.toList())

        }
        rvSku.layoutManager = LinearLayoutManager(requireContext())
        rvSku.adapter = typeAdapter
        typeAdapter.submitList(typeList)


        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val filteredList = if (query.isEmpty()) {
                    typeList
                } else {
                    typeList.filter { it.name.contains(query, ignoreCase = true) }
                }
                typeAdapter.submitList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun callTypeSegment(typeId: String, id: String) {
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.UpdateTypeRequest(
            user_id = id,
            type_id = typeId
        )

        viewModel.updateType(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }

    private fun showSegmentDialog(id: String) {
        viewModel.getSegmentData(AppPreferences.getInstance(requireContext()).getToken().toString())
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_segments, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val edtSearch = dialogView.findViewById<EditText>(R.id.edit_search)
        val rvSku = dialogView.findViewById<RecyclerView>(R.id.rvSkuList)
        val progressBar = dialogView.findViewById<RelativeLayout>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        edtSearch.visibility = View.VISIBLE

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        segmentAdapter = SegmentListingAdapter { data ->
            callUpdateSegment(data?.id.toString(), id)
            dialog.dismiss()
        }
        viewModel.segmentData.observe(viewLifecycleOwner) { data ->
            progressBar.visibility = View.GONE
            segmentList.clear()
            data?.segments?.let {
                segmentList.addAll(it)   // add list of Segments
            }

            segmentAdapter.submitList(segmentList.toList())

        }
        rvSku.layoutManager = LinearLayoutManager(requireContext())
        rvSku.adapter = segmentAdapter
        segmentAdapter.submitList(segmentList)


        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val filteredList = if (query.isEmpty()) {
                    segmentList
                } else {
                    segmentList.filter { it.name.contains(query, ignoreCase = true) }
                }
                segmentAdapter.submitList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun callUpdateSegment(segmentId: String, id: String) {

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.UpdateSegmantRequest(
            user_id = id,
            segment_id = segmentId
        )

        viewModel.updateSegment(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
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
        viewModel.deleteCustStatusFail.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.deleteCustStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
            callCustomerAPI()
        }
        viewModel.custBanStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
            callCustomerAPI()
        }
        viewModel.segmentStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            callCustomerAPI()
        }

        viewModel.typeStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            callCustomerAPI()
        }
        viewModel.customerListData.observe(viewLifecycleOwner) { data ->

            binding.progressBarSmall.visibility = View.GONE

            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountCustomer = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPageCustomer == 1) {

                manageCustomers(data)
            } else {
                custAdapter.addProducts(data?.users!!)
            }

        }
        viewModel.productEventCust.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            isLoading = false
        }



        viewModel.getTypeData(appPreferences.getToken().toString())
        viewModel.getSegmentData(appPreferences.getToken().toString())

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
            dialog?.dismiss()
        }

        dialog = builder.create()
        dialog.show()
    }
    private fun callCustomerAPI() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.currentPageCustomer = 1
        viewModel.clearCustomerData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.CustomerListRequest(
            keyword = "",
            per_page = "10",
            page_no = "1"
        )
        viewModel.getCustomerList(appPreferences.getToken().toString(), request)
    }

    private fun handleClick() {

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun showBanPopup(customerId: String, banStatus: String) {
        val builder = AlertDialog.Builder(requireContext())
        val customLayout: View = layoutInflater.inflate(R.layout.ban_cust_popup, null)
        builder.setView(customLayout)

        val btnCancel = customLayout.findViewById<MaterialCardView>(R.id.card_cancel)
        val btnDelete = customLayout.findViewById<MaterialCardView>(R.id.card_proceed)
        val txtConfirmText = customLayout.findViewById<TextView>(R.id.txt_confirm_msg)


        if (banStatus == "0"){
            txtConfirmText.text = "Do you really want to ban this Customer?"
        }else{
            txtConfirmText.text = "Do you really want to Unban this Customer?"
        }
        lateinit var dialog: AlertDialog
        btnCancel.setOnClickListener {
            dialog?.dismiss()

        }
        btnDelete.setOnClickListener {
            if (banStatus == "0") {
                callBanApi(customerId, "1")
            }else{
                callBanApi(customerId, "0")
            }
            dialog?.dismiss()

        }
        dialog = builder.create()
        dialog.show()
    }

    private fun callBanApi(customerId: String, banStatus: String) {
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.FavRequest(
            id = customerId,
            status = banStatus
        )
        viewModel.customerUpdateBanStatus(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
    }

    private fun showDeletePopup(customerId: String) {
        val builder = AlertDialog.Builder(requireContext())
        val customLayout: View = layoutInflater.inflate(R.layout.delete_product_popup, null)
        builder.setView(customLayout)

        val btnCancel = customLayout.findViewById<MaterialCardView>(R.id.card_cancel)
        val btnDelete = customLayout.findViewById<MaterialCardView>(R.id.card_delete)
        lateinit var dialog: AlertDialog
        btnCancel.setOnClickListener {
            dialog?.dismiss()

        }
        btnDelete.setOnClickListener {
            callDeleteCustomerApi(customerId)
            dialog?.dismiss()

        }
        dialog = builder.create()
        dialog.show()
    }

    private fun callDeleteCustomerApi(customerId: String) {

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.CartRemoveRequest(
            id = customerId
        )
        viewModel.deleteCustomer(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
    }

}