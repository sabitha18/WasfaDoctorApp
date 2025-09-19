package com.wasfa.doctor.ui.sales.details

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentOrderDetailsBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.DeliveryBoys
import com.wasfa.doctor.network.response.InfluencerListResponse
import com.wasfa.doctor.network.response.ItemsList
import com.wasfa.doctor.network.response.OrderDetailsResponse
import com.wasfa.doctor.ui.sales.adapter.DeliveryBoysAdapter
import com.wasfa.doctor.ui.sales.adapter.InfluencerAdapter
import com.wasfa.doctor.ui.sales.adapter.OrderDetailsAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class OrderDetailsFragment : Fragment() {
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private var deliveryBoys: List<DeliveryBoys>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        setViewModel()
        callOrderDetailsApi()


        if (AppPreferences.getInstance(requireContext()).getLoginType() == "delivery_boy") {
            binding.lytLead.visibility = View.GONE
            binding.lytAssignDeliveryStaff.visibility = View.GONE
            binding.lytAssignInfluencer.visibility = View.GONE
            binding.lytBlank.visibility = View.GONE
        } else {
            binding.lytLead.visibility = View.VISIBLE
            binding.lytAssignDeliveryStaff.visibility = View.VISIBLE
            binding.lytAssignInfluencer.visibility = View.VISIBLE
            binding.lytBlank.visibility = View.INVISIBLE
        }

    }

    private fun callOrderDetailsApi() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.CatDetailsRequest(
            id = appPreferences.getOrderID().toString()
        )
        viewModel.getOrderDetails(appPreferences.getToken().toString(), request)
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
        viewModel.collectedStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE


        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.updatePaymentStatus.observe(viewLifecycleOwner) { message ->
            callOrderDetailsApi()

        }
        viewModel.orderDetailsData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            manageData(data)
        }
        viewModel.influencerList.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            showAssignInfluencerDialog(data)
        }



        binding.progressBar.visibility = View.VISIBLE

    }

    private fun showDeliveryBoyDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_assign_influencer, null)

        val txtHead = dialogView.findViewById<TextView>(R.id.txt_heading)
        val txtNoData = dialogView.findViewById<TextView>(R.id.txt_no_data)
        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recycler_influencers)
        val btnChange = dialogView.findViewById<MaterialCardView>(R.id.card_change)
        val editSearch = dialogView.findViewById<EditText>(R.id.edit_search)
        txtHead.text = "Assign Delivery Staff"

        if (deliveryBoys.isNullOrEmpty()) {
            txtNoData.visibility = View.VISIBLE
        } else {
            txtNoData.visibility = View.GONE
        }


        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        var selectedInfluencer: DeliveryBoys? = null

        fun setButtonEnabled(enabled: Boolean) {
            btnChange.isEnabled = enabled
            btnChange.alpha = if (enabled) 1f else 0.5f
        }
        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        setButtonEnabled(false)
        val currentAssigned = binding.txtDeliveryStaff.text.toString().trim()

        val adapter = DeliveryBoysAdapter(
            deliveryBoys ?: emptyList(),
            onItemSelected = { influencer ->
                selectedInfluencer = influencer
                setButtonEnabled(true)
            },
            preSelectedName = currentAssigned
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filterList(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        btnChange.setOnClickListener {
            if (!btnChange.isEnabled) return@setOnClickListener

            selectedInfluencer?.let {
                dialog.dismiss()
                callAssignDeliveryBoyApi(it)
            }
        }

        dialog.show()

    }

    private fun callAssignDeliveryBoyApi(it: DeliveryBoys) {
        val request = ApiService.AssignDeliveryBoyRequest(
            id = AppPreferences.getInstance(requireContext()).getOrderID().toString(),
            deliveryBoy = it?.id.toString()
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.assignDeliveryBoy(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )
    }

    private fun showAssignInfluencerDialog(data: List<InfluencerListResponse>?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_assign_influencer, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val editSearch = dialogView.findViewById<EditText>(R.id.edit_search)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recycler_influencers)
        val btnChange = dialogView.findViewById<MaterialCardView>(R.id.card_change)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        var selectedInfluencer: InfluencerListResponse? = null

        fun setButtonEnabled(enabled: Boolean) {
            btnChange.isEnabled = enabled
            btnChange.alpha = if (enabled) 1f else 0.5f
        }
        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        setButtonEnabled(false)
        val currentAssigned = binding.txtAssignInfluencer.text.toString().trim()
        val adapter = InfluencerAdapter(
            data ?: emptyList(),
            onItemSelected = { influencer ->
                selectedInfluencer = influencer
                setButtonEnabled(true)
            },
            preSelectedName = currentAssigned
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filterList(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        btnChange.setOnClickListener {
            if (!btnChange.isEnabled) return@setOnClickListener

            selectedInfluencer?.let {
                dialog.dismiss()
                callAssignInfluencerApi(it)
            }
        }

        dialog.show()

    }

    private fun callAssignInfluencerApi(it: InfluencerListResponse) {
        val request = ApiService.AssignInfluencerStatusRequest(
            id = AppPreferences.getInstance(requireContext()).getOrderID().toString(),
            influencerId = it?.id.toString()
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.assignInfluencer(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )
    }

    private fun manageData(data: List<OrderDetailsResponse>?) {
        val orderData = data?.get(0)


        val status = orderData?.deliveryStatus


        binding.txtDeliveryStatus.text = orderData?.deliveryStatus
            ?.replace("_", " ")
            ?.replaceFirstChar { it.uppercase() }
            ?: ""


        binding.txtPaymentStatus.text =
            orderData?.paymentStatus?.replaceFirstChar { it.uppercase() }
        binding.txtLead.text = orderData?.lead?.replaceFirstChar { it.uppercase() }
        binding.txtAssignInfluencer.text =
            orderData?.influencerName?.replaceFirstChar { it.uppercase() }
        binding.txtDeliveryStaff.text =
            orderData?.deleiveryBoyName?.replaceFirstChar { it.uppercase() }


        if (orderData?.deliveryStatus == "delivered" ||
            orderData?.deliveryStatus == "cancelled") {

            binding.cardChangeDeliveryStatus.isClickable = false

        } else {
            binding.cardChangeDeliveryStatus.isClickable = true
            binding.cardChangeDeliveryStatus.alpha = 1f
        }
//        if (orderData?.paymentStatus == "paid") {
//            binding.cardChangePaymentStatus.isClickable = false
//
//        } else {
//            binding.cardChangePaymentStatus.isClickable = true
//            binding.cardChangePaymentStatus.alpha = 1f
//        }

        manageProduct(orderData?.itemsList)
        deliveryBoys = orderData?.deliveryBoys

        orderData?.deliveryBoys

        binding.txtOrderId.text = "Order " + orderData?.code
        binding.txtSubTotal.text = orderData?.grandTotal
        binding.txtCancelledTotal.text = "KD 0.000"
        binding.txtTotal.text = orderData?.grandTotal


        val addressData = orderData?.address

        binding.txtGovernorate.text = addressData?.governorate
            ?.takeIf { it.isNotBlank() }
            ?: ""

        binding.txtArea.text = addressData?.areaName
            ?.takeIf { it.isNotBlank() }
            ?: ""

        binding.txtBlock.text = addressData?.block
            ?.takeIf { it.isNotBlank() }
            ?: ""

        binding.txtStreet.text = addressData?.street
            ?.takeIf { it.isNotBlank() }
            ?: ""

        binding.txtBuilding.text = addressData?.building
            ?.takeIf { it.isNotBlank() }
            ?: ""

        binding.txtFloor.text = addressData?.floor
            ?.takeIf { it.isNotBlank() }
            ?: ""

        binding.txtLane.text = addressData?.lane
            ?.takeIf { it.isNotBlank() }
            ?: ""

        binding.txtFlat.text = addressData?.flat
            ?.takeIf { it.isNotBlank() }
            ?: ""

        binding.txtOrderDate.text = orderData?.date

    }

    private fun handleClick() {
        binding.imgBack.setOnClickListener {
            val navController = runCatching { findNavController() }.getOrNull()
            if (navController?.currentDestination != null) {
                navController.popBackStack()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
        binding.cardAssignDeliveryStaff.setOnClickListener {

            val appPrefs = AppPreferences.getInstance(requireContext())
            when (appPrefs.getLoginType()) {

                "staff" -> {
                    if (PermissionManager.hasPermission(PermissionKeys.UPDATE_ORDER_PAYMENT_STATUS)) {
                        showDeliveryBoyDialog()
                    } else {
                        return@setOnClickListener
                    }

            }
                else -> {

                    showDeliveryBoyDialog()
                }
            }
        }
        binding.cardChangeDeliveryStatus.setOnClickListener {
            val appPrefs = AppPreferences.getInstance(requireContext())
            when (appPrefs.getLoginType()) {
                "staff" -> {
                    if (PermissionManager.hasPermission(PermissionKeys.UPDATE_ORDER_DELIVERY_STATUS)) {
                        showAdminDeliveryStatusDialog()
                    } else {
                        return@setOnClickListener
                    }
                }

                "delivery_boy" -> {
                    showDeliveryStatusDialog()
                }

                else -> {
                    showAdminDeliveryStatusDialog()
                }
            }

        }
        binding.cardChangePaymentStatus.setOnClickListener {
            val appPrefs = AppPreferences.getInstance(requireContext())
            when (appPrefs.getLoginType()) {

                "staff" -> {
                    if (PermissionManager.hasPermission(PermissionKeys.UPDATE_ORDER_PAYMENT_STATUS)) {
                        showAdminPaymentStatusDialog()
                    } else {
                        return@setOnClickListener
                    }
                }

                "delivery_boy" -> {
                    showPaymentStatusDialog()
                }

                else -> {
                    showAdminPaymentStatusDialog()
                }
            }
        }

        binding.cardChangeLead.setOnClickListener {
            showLeadDialog()
        }
        binding.cardAssignInfluencer.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getInfluencerList(
                AppPreferences.getInstance(requireContext()).getToken().toString()
            )

        }
    }
    private fun showAdminDeliveryStatusDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_admin_change_delivery, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radio_group_status)
        val btnChange = dialogView.findViewById<MaterialCardView>(R.id.card_change)



        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        fun setButtonEnabled(enabled: Boolean) {
            btnChange.isEnabled = enabled
            btnChange.alpha = if (enabled) 1f else 0.5f
        }

        setButtonEnabled(false)

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        // 🔷 Pre-select based on txtDeliveryStatus
        val currentStatus = binding.txtDeliveryStatus.text.toString().trim()

        val checkedId = when (currentStatus) {
            "Pending" -> R.id.radio_pending
            "Confirmed" -> R.id.radio_confirmed
            "Picked Up" -> R.id.radio_picked_up
            "On The Way" -> R.id.radio_on_the_way
            "Delivered" -> R.id.radio_delivered
            "Cancel" -> R.id.radio_cancel
            "Closed" -> R.id.radio_closed
            else -> -1
        }

        if (checkedId != -1) {
            radioGroup.check(checkedId)
            setButtonEnabled(true)
        }

        radioGroup.setOnCheckedChangeListener { _, id ->
            setButtonEnabled(id != -1)
        }

        btnChange.setOnClickListener {
            if (!btnChange.isEnabled) return@setOnClickListener

            val selectedStatus = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_pending -> "pending"
                R.id.radio_confirmed -> "confirmed"
                R.id.radio_picked_up -> "picked_up"
                R.id.radio_on_the_way -> "on_the_way"
                R.id.radio_delivered -> "delivered"
                R.id.radio_cancel -> "cancelled"
                R.id.radio_closed -> "closed"
                else -> null
            }

            selectedStatus?.let {
                dialog.dismiss()
                callDeliveryStatusApi(it)
            }
        }

        dialog.show()
    }
    private fun showDeliveryStatusDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_change_delivery, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radio_group_status)
        val btnChange = dialogView.findViewById<MaterialCardView>(R.id.card_change)



        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        fun setButtonEnabled(enabled: Boolean) {
            btnChange.isEnabled = enabled
            btnChange.alpha = if (enabled) 1f else 0.5f
        }

        setButtonEnabled(false)

        imgClose.setOnClickListener {
            dialog.dismiss()
        }


        val currentStatus = binding.txtDeliveryStatus.text.toString().trim()

        val checkedId = when (currentStatus) {
            "pending" -> R.id.radio_pending
            "confirmed" -> R.id.radio_confirmed
            "picked_up" -> R.id.radio_picked_up
            "on_the_way" -> R.id.radio_on_the_way
            "delivered" -> R.id.radio_delivered
            "cancelled" -> R.id.radio_cancel
            "closed" -> R.id.radio_closed
            else -> -1
        }

        if (checkedId != -1) {
            radioGroup.check(checkedId)
            setButtonEnabled(true)
        }

        radioGroup.setOnCheckedChangeListener { _, id ->
            setButtonEnabled(id != -1)
        }

        btnChange.setOnClickListener {
            if (!btnChange.isEnabled) return@setOnClickListener

            val selectedStatus = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_pending -> "pending"
                R.id.radio_confirmed -> "confirmed"
                R.id.radio_picked_up -> "picked_up"
                R.id.radio_on_the_way -> "on_the_way"
                R.id.radio_delivered -> "delivered"
                R.id.radio_cancel -> "cancelled"
                R.id.radio_closed -> "closed"
                else -> null
            }

            selectedStatus?.let {
                dialog.dismiss()
                callDeliveryStatusApi(it)
            }
        }

        dialog.show()
    }
    private fun showAdminPaymentStatusDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_admin_payment_status, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radio_group_status)
        val btnChange = dialogView.findViewById<MaterialCardView>(R.id.card_change)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        fun setButtonEnabled(enabled: Boolean) {
            btnChange.isEnabled = enabled
            btnChange.alpha = if (enabled) 1f else 0.5f
        }

        setButtonEnabled(false)

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        // 🔷 Pre-select based on txtDeliveryStatus
        val currentStatus =
            binding.txtPaymentStatus.text.toString().trim().lowercase().replace("-", "")


        val checkedId = when (currentStatus) {
            "unpaid" -> R.id.radio_un_paid
            "processed" -> R.id.radio_processed
            "paid" -> R.id.radio_paid
            else -> -1
        }

        if (checkedId != -1) {
            radioGroup.check(checkedId)
            setButtonEnabled(true)
        }

        radioGroup.setOnCheckedChangeListener { _, id ->
            setButtonEnabled(id != -1)
        }

        btnChange.setOnClickListener {
            if (!btnChange.isEnabled) return@setOnClickListener

            val selectedStatus = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_un_paid -> "unpaid"
                R.id.radio_processed -> "processed"
                R.id.radio_paid -> "paid"
                else -> null
            }

            selectedStatus?.let {
                dialog.dismiss()
                callUpdateStatusApi(it)
            }
        }

        dialog.show()
    }
    private fun showPaymentStatusDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_payment_status, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radio_group_status)
        val btnChange = dialogView.findViewById<MaterialCardView>(R.id.card_change)

        val radioPaid = dialogView.findViewById<RadioButton>(R.id.radio_paid)
        val radioCash = dialogView.findViewById<RadioButton>(R.id.radio_cash)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        fun setButtonEnabled(enabled: Boolean) {
            btnChange.isEnabled = enabled
            btnChange.alpha = if (enabled) 1f else 0.5f
        }

        setButtonEnabled(false)

        radioCash.isEnabled = false
        radioCash.alpha = 0.5f

        imgClose.setOnClickListener {
            dialog.dismiss()
        }


        val currentStatus =
            binding.txtPaymentStatus.text.toString().trim().lowercase().replace("-", "")

        val checkedId = when (currentStatus) {
            "unpaid" -> R.id.radio_un_paid
            "processed" -> R.id.radio_processed
            "paid" -> R.id.radio_paid
            "cash" -> R.id.radio_cash
            else -> -1
        }

        if (checkedId != -1) {
            radioGroup.check(checkedId)
            setButtonEnabled(true)

            // if already "paid", enable Cash option
            if (checkedId == R.id.radio_paid) {
                radioCash.isEnabled = true
                radioCash.alpha = 1f
            }
        }

        radioGroup.setOnCheckedChangeListener { _, id ->
            setButtonEnabled(id != -1)

            if (id == R.id.radio_paid) {
                radioCash.isEnabled = true
                radioCash.alpha = 1f
            } else {
                radioCash.isEnabled = false
                radioCash.alpha = 0.5f
                radioCash.isChecked = false
            }
        }

        btnChange.setOnClickListener {
            if (!btnChange.isEnabled) return@setOnClickListener

            val isPaidChecked = radioGroup.checkedRadioButtonId == R.id.radio_paid
            val isCashChecked = radioCash.isChecked

            if (isPaidChecked && isCashChecked) {
                dialog.dismiss()
                callUpdateStatusApi("paid")
                callUpdateCashStatusApi("1")
            } else {

                when (radioGroup.checkedRadioButtonId) {
                    R.id.radio_un_paid -> {
                        dialog.dismiss()
                        callUpdateStatusApi("unpaid")
                    }
                    R.id.radio_processed -> {
                        dialog.dismiss()
                        callUpdateStatusApi("processed")
                    }
                    R.id.radio_paid -> {
                        dialog.dismiss()
                        callUpdateStatusApi("paid")
                    }
                }

                if (isCashChecked) {
                    dialog.dismiss()
                    callUpdateCashStatusApi("1")
                }
            }
        }

        dialog.show()
    }

    private fun callUpdateCashStatusApi(status: String) {
        val request = ApiService.UpdateDeliveryStatusRequest(
            id = AppPreferences.getInstance(requireContext()).getOrderID().toString(),
            status = status
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.updateCashReceived(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )
    }


    private fun showLeadDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_lead, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radio_group_status)
        val btnChange = dialogView.findViewById<MaterialCardView>(R.id.card_change)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        fun setButtonEnabled(enabled: Boolean) {
            btnChange.isEnabled = enabled
            btnChange.alpha = if (enabled) 1f else 0.5f
        }

        setButtonEnabled(false)

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        // 🔷 Pre-select based on txtDeliveryStatus
        val currentStatus = binding.txtLead.text.toString().trim()

        val checkedId = when (currentStatus) {
            "social_media" -> R.id.radio_social_media
            "prescription" -> R.id.radio_prescription
            "gift" -> R.id.radio_gift
            else -> -1
        }

        if (checkedId != -1) {
            radioGroup.check(checkedId)
            setButtonEnabled(true)
        }

        radioGroup.setOnCheckedChangeListener { _, id ->
            setButtonEnabled(id != -1)
        }

        btnChange.setOnClickListener {
            if (!btnChange.isEnabled) return@setOnClickListener

            val selectedStatus = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_social_media -> "social_media"
                R.id.radio_prescription -> "prescription"
                R.id.radio_gift -> "gift"
                else -> null
            }

            selectedStatus?.let {
                dialog.dismiss()
                callUpdateLeadApi(it)
            }
        }

        dialog.show()
    }

    private fun callUpdateLeadApi(it: String) {
        val request = ApiService.UpdateLeadRequest(
            id = AppPreferences.getInstance(requireContext()).getOrderID().toString(),
            lead = it
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.updateLead(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }
    private fun callDeliveryStatusApi(deliveryStatus: String) {

        val request = ApiService.UpdateDeliveryStatusRequest(
            id = AppPreferences.getInstance(requireContext()).getOrderID().toString(),
            status = deliveryStatus
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.updateDeliveryStatus(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )
    }
    private fun callUpdateStatusApi(deliveryStatus: String) {

        val request = ApiService.UpdateDeliveryStatusRequest(
            id = AppPreferences.getInstance(requireContext()).getOrderID().toString(),
            status = deliveryStatus
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.updatePaymentStatus(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )
    }

    private fun manageProduct(itemsList: List<ItemsList>?) {

        binding.recyclerProduct.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val catAdapter = OrderDetailsAdapter(itemsList) { data, position ->
                if (position == 1111) {
                    showImagePopup(data?.thumbnailImage)
                }else if (position == 1) {
                   callCollectedApi(position,data?.id.toString())
                }else if (position == 0) {
                    callCollectedApi(position,data?.id.toString())
                }  else {
                    findNavController().navigate(R.id.nav_order_details)
                }

            }
            adapter = catAdapter
        }
    }

    private fun callCollectedApi(position: Int, id: String) {
        val appPreferences = AppPreferences.getInstance(requireContext())
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.ChangePickupStatusRequest(
            id = id,
            status = position.toString()
        )
        viewModel.collectedByDeliveryBoy(appPreferences.getToken().toString(),request)
    }

    private fun showImagePopup(imageUrl: String?) {
        val context = requireContext()
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_image_preview, null)

        val photoView =
            dialogView.findViewById<PhotoView>(R.id.photo_view)

        val imgClose =
            dialogView.findViewById<ImageView>(R.id.btn_close)


        val dialog =
            AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

        dialogView.setOnClickListener { dialog.dismiss() }
        dialog.show()


        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.9).toInt()
        dialog.window?.setLayout(width, height)
        photoView.minimumWidth = (resources.displayMetrics.widthPixels * 0.9).toInt()
        photoView.minimumHeight = (resources.displayMetrics.heightPixels * 0.9).toInt()
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.wasfa_logo)
                .into(photoView)
        } else {
            photoView.setImageResource(R.drawable.wasfa_logo)
        }
    }
}