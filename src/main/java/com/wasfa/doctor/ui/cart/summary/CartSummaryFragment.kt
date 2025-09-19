package com.wasfa.doctor.ui.cart.summary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentCartSummaryBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CartList
import com.wasfa.doctor.network.response.DeliveryBoyResponse
import com.wasfa.doctor.network.response.InfluencerListResponse
import com.wasfa.doctor.ui.cart.SucessActivity
import com.wasfa.doctor.ui.cart.adapter.BoyListAdapter
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupLeadName
import com.wasfa.doctor.ui.cart.adapter.InfluencerListAdapter
import com.wasfa.doctor.ui.cart.adapter.POSShopSummeryCartAdapter
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CartSummaryFragment : Fragment() {
    private var _binding: FragmentCartSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private var isDeliverChecked: String = "0"
    private var isApixChecked: String = "0"
    private var isCollectedByApixChecked: String = "1"
    private var deliveryId: String = ""
    private var influencerId: String = ""
    private lateinit var deliveryAdapter: BoyListAdapter
    private lateinit var influencerAdapter: InfluencerListAdapter
    private var popupWindow: PopupWindow? = null
    private var popupWindowInfluencer: PopupWindow? = null
    private val deliveryList = mutableListOf<DeliveryBoyResponse>()
    private val influencerList = mutableListOf<InfluencerListResponse>()
    private var debounceJob: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        setViewModel()
        setAddressView()
        manageCheckBox()
        manageLeadName()
        handleEditText()
    }

    private fun handleEditText() {
        binding.editBooklet.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val bookletText = s.toString()
                val prescriptionText = binding.editPrescription.text.toString()
                if (bookletText.isNotEmpty()) {
                    debounceCall(bookletText, prescriptionText)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.editPrescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val bookletText = binding.editPrescription.text.toString()
                val prescriptionText = s.toString()
                debounceCall(bookletText, prescriptionText)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }
    private fun debounceCall(booklet: String, prescription: String) {
        debounceJob?.cancel() // cancel any previous running job
        debounceJob = CoroutineScope(Dispatchers.Main).launch {
            delay(300) // wait for 300ms
            val request = ApiService.ChangePickupPointRequest(
                bookletNo = booklet,
                prescriptionNo = prescription
            )
            binding.progressBar.visibility = View.VISIBLE
            viewModel.searchInfluencer(AppPreferences.getInstance(requireContext()).getToken().toString(), request)
        }
    }

    private fun manageCheckBox() {
        binding.switchCollectedByApix.setOnCheckedChangeListener { _, isChecked ->

            isCollectedByApixChecked = if (isChecked) "1" else "0"

        }
        binding.checkDeliver.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkApix.isChecked = false
                isDeliverChecked = "1"
                isApixChecked = "0"
            } else {
                isDeliverChecked = "0"
            }

        }

        binding.checkApix.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkDeliver.isChecked = false
                isApixChecked = "1"
                isDeliverChecked = "0"
            } else {
                isApixChecked = "0"
            }
        }
    }

    private fun setAddressView() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        binding.txtCustomer.text = appPreferences.getCustName()
        binding.txtEmail.text = appPreferences.getAddress()?.email
        binding.txtPhone.text = appPreferences.getAddress()?.phone
        binding.txtAddress.text = appPreferences.getAddress()?.addressTitle + ", " +
                appPreferences.getAddress()?.governorateName + ", " +
                appPreferences.getAddress()?.areaName
        binding.txtAddress1.text = appPreferences.getAddress()?.street + ", " +
                appPreferences.getAddress()?.block

    }

    private fun setViewModel() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(requireContext())
        ).get(HomeViewModel::class.java)
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE


        }
        viewModel.deleteCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.VISIBLE
            println("me =--------------")
            viewModel.getCartList(appPreferences.getToken().toString())

        }
        viewModel.influencerStatus.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            val responseInfluencerId = data?.influencerId

            val selectedInfluencer = influencerList.find { it.id == responseInfluencerId }
            selectedInfluencer?.let {
                binding.txtInfluencer.text = it.name
                influencerId = it.id.toString()
            }

        }
        viewModel.changeQuantityCartShopStatus.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            binding.txtSubTotal.text = data?.subTotal
            binding.txtItemCount.text = data?.itemCount
            binding.txtShipping.text = data?.shippingCost
            binding.txtDiscount.text = data?.discount
            binding.txtTotal.text = data?.grandTotal

        }
        viewModel.cartListData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            manageCart(data?.cartItems)


            binding.txtSubTotal.text = data?.subTotal
            binding.txtItemCount.text = data?.itemCount
            binding.txtShipping.text = data?.shippingCost
            binding.txtDiscount.text = data?.discount
            binding.txtTotal.text = data?.grandTotal
            binding.txtCartCount.text = data?.itemCount

            AppPreferences.getInstance(requireContext()).saveCartCount(data?.itemCount)

            if (data?.itemCount == "0") {
                binding.rltCartCount.visibility = View.INVISIBLE
            } else {
                binding.rltCartCount.visibility = View.VISIBLE
            }
        }
        binding.progressBar.visibility = View.VISIBLE
        println("me =-------------- ----------------------")
        viewModel.getCartList(appPreferences.getToken().toString())
        viewModel.orderPlacedStatus.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = View.GONE
            val intent = Intent(requireContext(), SucessActivity::class.java)
            startActivity(intent)

        }
        viewModel.orderPlacedError.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.deliveryBoyData.observe(viewLifecycleOwner) { data ->

            deliveryList.addAll(data)


        }
        viewModel.influencerList.observe(viewLifecycleOwner) { data ->

            influencerList.addAll(data)


        }
        viewModel.getDeliveryBoys(appPreferences.getToken().toString())
        viewModel.getInfluencerList(appPreferences.getToken().toString())
    }

    private fun manageDeliveryBoys(data: List<DeliveryBoyResponse>?) {

    }

    private fun manageCart(cartItems: List<CartList>?) {
        binding.recyclerRxCart.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val catAdapter = POSShopSummeryCartAdapter(cartItems, { Cat, position ->

            }, { cart, count ->
                val request = ApiService.ChangeCartRequest(
                    id = cart.id.toString(),
                    quantity = count
                )
                binding.progressBar.visibility = View.VISIBLE
                viewModel.changeQuantityCartShop(
                    AppPreferences.getInstance(requireContext()).getToken().toString(), request
                )
            },
                { cart, count ->
                    binding.progressBar.visibility = View.VISIBLE
                    val request = ApiService.ChangeCartRequest(
                        id = cart.id.toString(),
                        quantity = count
                    )
                    viewModel.changeQuantityCartShop(
                        AppPreferences.getInstance(requireContext()).getToken().toString(), request
                    )
                }
            ) { cart ->
                binding.progressBar.visibility = View.VISIBLE
                val request = ApiService.CartRemoveRequest(
                    id = cart.id.toString(),
                )
                viewModel.deleteCartShop(
                    AppPreferences.getInstance(requireContext()).getToken().toString(), request
                )

            }
            adapter = catAdapter
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
            dialog?.dismiss()
        }

        dialog = builder.create()
        dialog.show()
    }
    private fun showDeliveryDropdown(anchorView: View, skuList: MutableList<DeliveryBoyResponse>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindow =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)


        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.visibility = View.GONE

        deliveryAdapter = BoyListAdapter() { data ->
            binding.txtDeliveryStaff.text = data.name
            deliveryId = data?.id.toString()
            popupWindow?.dismiss()

        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = deliveryAdapter
        deliveryAdapter.submitList(skuList)


        popupWindow?.elevation = 10f
        popupWindow?.isOutsideTouchable = true
        popupWindow?.showAsDropDown(anchorView)
    }

    private fun showInfluencerDropdown(anchorView: View, skuList: MutableList<InfluencerListResponse>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowInfluencer =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)


        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)



        influencerAdapter = InfluencerListAdapter() { data ->
            binding.txtInfluencer.text = data.name
            influencerId = data?.id.toString()
            popupWindowInfluencer?.dismiss()

        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = influencerAdapter
        influencerAdapter.submitList(skuList)

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                val filteredList = skuList.filter {
                    it.name.lowercase().contains(query)
                }
                influencerAdapter.submitList(filteredList)
            }
        })

        popupWindowInfluencer?.elevation = 10f
        popupWindowInfluencer?.isOutsideTouchable = true
        popupWindowInfluencer?.showAsDropDown(anchorView)
    }

    private fun handleClick() {

        binding.cardDeliveryStaff.setOnClickListener {
            showDeliveryDropdown(binding.cardDeliveryStaff, deliveryList)
        }
        binding.cardInfluencer.setOnClickListener {
            showInfluencerDropdown(binding.cardInfluencer, influencerList)
        }

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cardGoTap.setOnClickListener {
            binding.cardGoTapSelected.visibility = View.VISIBLE
            binding.cardCodSelected.visibility = View.GONE
            binding.cardQuickPaySelected.visibility = View.GONE
        }
        binding.cardQuickPay.setOnClickListener {
            binding.cardGoTapSelected.visibility = View.GONE
            binding.cardCodSelected.visibility = View.GONE
            binding.cardQuickPaySelected.visibility = View.VISIBLE
        }
        binding.cardCod.setOnClickListener {
            binding.cardGoTapSelected.visibility = View.GONE
            binding.cardCodSelected.visibility = View.VISIBLE
            binding.cardQuickPaySelected.visibility = View.GONE
        }
        binding.cardProceed.setOnClickListener {
            callOrderPlaceApi()
        }
    }

    private fun callOrderPlaceApi() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.OrderRequest(
            addressId = appPreferences.getAddressId().toString(),
            payment_type = "cod",
            customerId = appPreferences.getCustId().toString(),
            influencerId = "",
            medicalRepId = "",
            deliveryByApix = isDeliverChecked,
            pickupByApix = isApixChecked,
            leadName = binding.txtLeadName.text.toString(),
            device = "android",
            additionalSellerNote = binding.editAdditionalNotes.text.toString(),
            internalNotes = binding.editInternalNotes.text.toString(),
            deliverBoyId = deliveryId,
            bookletNo = binding.editBooklet.text.toString(),
            PrescriptionId = binding.editPrescription.text.toString(),
            collectedByApix = isCollectedByApixChecked
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.placeOrder(appPreferences.getToken().toString(), request)
    }

    private fun manageLeadName() {
        val dropdownPopup =
            DropdownPopupLeadName(
                requireContext(),
                binding.cardLeadName,
                createLeadNameList()
            ) { text ->
                binding.txtLeadName.text = text

            }

        binding.cardLeadName.setOnClickListener {
            dropdownPopup.show()
        }
    }

    private fun createLeadNameList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Social Media",
                R.drawable.dummy_image
            ),
            Cat(
                "Prescription",
                R.drawable.dummy_image
            ),
            Cat(
                "Gift",
                R.drawable.dummy_image
            )
        )
    }
}