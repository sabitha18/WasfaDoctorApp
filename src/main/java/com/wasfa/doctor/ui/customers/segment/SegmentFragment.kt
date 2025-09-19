package com.wasfa.doctor.ui.customers.segment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentSegmentBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.SegmentResponse
import com.wasfa.doctor.network.response.Segments
import com.wasfa.doctor.ui.customers.adapter.SegmentListAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class SegmentFragment : Fragment() {
    private var _binding: FragmentSegmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var typeAdapter: SegmentListAdapter
    private var isLoading = false
    private val visibleThreshold = 5
    private var typeId: String = ""
    private var originalTypeName: String = ""
    private var originalArabicName: String = ""
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSegmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        setViewModel()
        callSegmentApi()
        setUpRecyclerView()
        setUpPagination()
        setViewAdd()
        if (AppPreferences.getInstance(requireContext()).getLoginType().toString() == "staff") {
            manageStaffViews()
        }
    }
    private fun manageStaffViews() = with(binding) {
        cardAdd.setVisibleIfPermission(PermissionKeys.ADD_SEGMANT)

    }

    private fun View.setVisibleIfPermission(permissionKey: String) {
        visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE

    }
    private fun setViewAdd() {
        binding.pageSegmentAdd.editTypeName.addTextChangedListener { checkForChangesAdd() }
        binding.pageSegmentAdd.editArabicName.addTextChangedListener { checkForChangesAdd() }

        binding.pageSegmentAdd.cardSave.isClickable = false
        binding.pageSegmentAdd.cardSave.alpha = 0.5f
    }
    private fun checkForChangesAdd() {
        val typeName = binding.pageSegmentAdd.editTypeName.text.toString().trim()
        val arabicName = binding.pageSegmentAdd.editArabicName.text.toString().trim()

        val hasChanged = typeName.isNotEmpty() && arabicName.isNotEmpty()

        binding.pageSegmentAdd.cardSave.isClickable = hasChanged
        binding.pageSegmentAdd.cardSave.alpha = if (hasChanged) 1f else 0.5f
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
                if (viewModel.isLastPageSegment()) {
                    // No action for the last page
                } else {
                    viewModel.currentPageSegment++
                    viewModel.loadNextPageSegment()
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
                binding.nestedScrollView.viewTreeObserver.removeOnScrollChangedListener(
                    scrollListener
                )
            }
        })
    }

    private fun setUpRecyclerView() {
        typeAdapter = SegmentListAdapter(
            mutableListOf(),
            { data, type ->
                when (type) {
                    "edit" -> {
                        binding.pageSegmentEdit.lytAddress.visibility = View.VISIBLE
                        setViewEdit(data)
                    }
                    "delete" -> {
                        showDeletePopup(data.id.toString())
                    }
                }
            },
            { data,status ,type-> // switch listener
                if (type == "switch") {
                    val request = ApiService.FavRequest(
                        id = data.id.toString(),
                        status = status
                    )
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.updateSegmentStatus(
                        AppPreferences.getInstance(requireContext()).getToken().toString(),
                        request
                    )
                }
            }
        )

        binding.recyclerList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = typeAdapter
        }
    }
    private fun setViewEdit(data: Segments) {
        // Set initial values
        binding.pageSegmentEdit.editTypeName.setText(data?.name?.takeIf { it.isNotBlank() } ?: "")
        binding.pageSegmentEdit.editArabicName.setText(data?.arabicName?.takeIf { it.isNotBlank() }
            ?: "")

        typeId = data?.id.toString()


        originalTypeName = data?.name ?: ""
        originalArabicName = data?.arabicName ?: ""





        binding.pageSegmentEdit.editTypeName.addTextChangedListener { checkForChanges() }
        binding.pageSegmentEdit.editArabicName.addTextChangedListener { checkForChanges() }


        binding.pageSegmentEdit.cardSave.isClickable = false
        binding.pageSegmentEdit.cardSave.alpha = 0.5f
    }

    private fun checkForChanges() {

        val currentTypeName = binding.pageSegmentEdit.editTypeName.text.toString()
        val currentArabicName = binding.pageSegmentEdit.editArabicName.text.toString()

        val hasChanged =
            currentTypeName != originalTypeName || currentArabicName != originalArabicName

        binding.pageSegmentEdit.cardSave.isClickable = hasChanged
        binding.pageSegmentEdit.cardSave.alpha = if (hasChanged) 1f else 0.5f

    }
    private fun callSegmentApi() {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.clearSegmentData()

            viewModel.currentPageSegment = 1
            val appPreferences = AppPreferences.getInstance(requireContext())
            val request = ApiService.BrandRequest(

                per_page = "10",
                page_no = "1"
            )
            viewModel.getSegmentListNew(appPreferences.getToken().toString(), request)

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
        viewModel.getUserPermissions(appPreferences.getToken().toString())
        viewModel.userPermissionList.observe(viewLifecycleOwner) { data ->

            if (AppPreferences.getInstance(requireContext()).getLoginType().toString() == "staff") {
                appPreferences.saveStaffPermissions(data.permissions)
                manageStaffViews()
            }
        }
        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE


        }
        viewModel.productEventCust.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            isLoading = false
        }
        viewModel.addTypeStatus.observe(viewLifecycleOwner) { message ->
            isLoading = false
            callSegmentApi()
        }
        viewModel.addTypeStatusFail.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)


        }

        viewModel.segmentListData.observe(viewLifecycleOwner) { data ->
            binding.progressBarSmall.visibility = View.GONE
            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountSegment = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPageSegment == 1) {

                manageSegment(data)
            } else {
                typeAdapter.addProducts(data?.segments!!)
            }

        }


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
            if (message == "Internal Server Error") {
                findNavController().popBackStack()
                dialog?.dismiss()
            } else {
                dialog?.dismiss()
            }

        }

        dialog = builder.create()
        dialog.show()
    }
    private fun handleClick() {

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cardAdd.setOnClickListener {
            binding.pageSegmentAdd.lytAddress.visibility = View.VISIBLE
        }
        binding.pageSegmentAdd.imgCloseDate.setOnClickListener {
            binding.pageSegmentAdd.lytAddress.visibility = View.GONE
        }

        binding.pageSegmentEdit.imgCloseDate.setOnClickListener {
            binding.pageSegmentEdit.lytAddress.visibility = View.GONE
        }
        binding.pageSegmentEdit.cardSave.setOnClickListener {
            binding.pageSegmentEdit.lytAddress.visibility = View.GONE

            callUpdateTypeApi()
        }
        binding.pageSegmentAdd.cardSave.setOnClickListener {
            binding.pageSegmentAdd.lytAddress.visibility = View.GONE

            callAddTypeApi()
        }
    }
    private fun callAddTypeApi() {

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.AddSegmentRequest(
            name = binding.pageSegmentAdd.editTypeName.text.toString(),
            arabicName = binding.pageSegmentAdd.editArabicName.text.toString()
        )
        viewModel.addSegment(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )

    }
    private fun callUpdateTypeApi() {

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.UpdateSegmentDetailsRequest(
            id = typeId,
            name = binding.pageSegmentEdit.editTypeName.text.toString(),
            arabicName = binding.pageSegmentEdit.editArabicName.text.toString()
        )
        viewModel.updateSegmentDetails(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )

    }
    private fun manageSegment(data: SegmentResponse) {

        binding.recyclerList.visibility = View.VISIBLE
        typeAdapter.setProducts(data?.segments?.toMutableList() ?: mutableListOf())
    }
    private fun showDeletePopup(id: String) {
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
            binding.progressBar.visibility = View.VISIBLE
            val request = ApiService.CartRemoveRequest(
                id = id
            )
            viewModel.deleteSegment(
                AppPreferences.getInstance(requireContext()).getToken().toString(),
                request
            )
            dialog?.dismiss()

        }
        dialog = builder.create()
        dialog.show()
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

    }

}
