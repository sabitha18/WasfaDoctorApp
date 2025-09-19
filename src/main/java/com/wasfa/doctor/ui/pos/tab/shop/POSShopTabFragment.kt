package com.wasfa.doctor.ui.pos.tab.shop

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
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentPOSShopTabBinding
import com.wasfa.doctor.helper.Address
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.AddressListResponse
import com.wasfa.doctor.network.response.AreaResponse
import com.wasfa.doctor.network.response.CartList
import com.wasfa.doctor.network.response.GovernorateResponse
import com.wasfa.doctor.network.response.InfluencerListResponse
import com.wasfa.doctor.network.response.Orders
import com.wasfa.doctor.network.response.ProductListResponse
import com.wasfa.doctor.network.response.Products
import com.wasfa.doctor.network.response.UserDetails
import com.wasfa.doctor.ui.cart.adapter.AddressAdapter
import com.wasfa.doctor.ui.cart.adapter.CustListCartAdapter
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupAddressTitle
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupArea
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupGovernorate
import com.wasfa.doctor.ui.cart.adapter.POSShopCartAdapter
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.pos.adapter.DropdownPopupInfluencer
import com.wasfa.doctor.ui.pos.adapter.POSRXListAdapter
import com.wasfa.doctor.ui.pos.tab.adapter.OrderListAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class POSShopTabFragment : Fragment() {
    private var _binding: FragmentPOSShopTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: POSRXListAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var selectedInfluencerId: String = ""
    var searchValue: String = ""
    var searchValueCust: String = ""
    var patientId = ""
    private var selectedBrandId: String = ""
    private var selectedSellerId: String = ""
    private var selectedMedicalRepId: String = ""
    private var isLoading = false
    private val visibleThreshold = 5
    private lateinit var popupWindow: PopupWindow
    private lateinit var custAdapter: CustListCartAdapter
    var selectedGovernorateID: String = ""
    var selectedAreaID: String = ""
    var edit: String = "false"
    private val customerList = mutableListOf<UserDetails>()
    var editAddressID: String = ""
    private var keyboardVisibilityListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPOSShopTabBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        handleFilterClick()
        setUpRecyclerView()
        setViewModel()
        setUpPagination()
        callProductAPI()
        manageSearch()
        callOrderListApi()
        callCartApi()
        handleAddressClick()
        handleSeparateAddressClick()
        checkAddressAdded()

    }

    private fun checkAddressAdded() {
        if (AppPreferences.getInstance(requireContext()).getAddressId() == null){
            binding.addCust.setCardBackgroundColor(Color.parseColor("#A61C5C"))
        }else{
            binding.addCust.setCardBackgroundColor(Color.parseColor("#008000"))
        }
    }

    private fun handleSeparateAddressClick() {
        binding.pageAddressNew.imgCloseDate.setOnClickListener {
            binding.pageAddressNew.lytAddress.visibility = View.GONE
        }
        binding.pageAddressNew.cardAddAddress.setOnClickListener {
            validateDataNew()
        }
    }

    private fun validateDataNew() {
        if (binding.pageAddressNew.txtAddressTitle.text.isEmpty()) {
            showAlertCustom("Enter Address Title")
        } else if (binding.pageAddressNew.editFirstName.text.isEmpty()) {
            showAlertCustom("Enter First Name")
        } else if (binding.pageAddressNew.editLastName.text.isEmpty()) {
            showAlertCustom("Enter Last Name")
        } else if (binding.pageAddressNew.editEmail.text.isEmpty()) {
            showAlertCustom("Enter Email")
        } else if (binding.pageAddressNew.editPhone.text.isEmpty()) {
            showAlertCustom("Enter Phone")
        } else if (selectedGovernorateID == "") {
            showAlertCustom("Choose Governorate")
        } else if (selectedAreaID == "") {
            showAlertCustom("Choose Area")
        } else if (binding.pageAddressNew.editBlock.text.isEmpty()) {
            showAlertCustom("Enter Block")
        } else if (binding.pageAddressNew.editStreet.text.isEmpty()) {
            showAlertCustom("Enter Street")
        } else {

            addAddressApiNew()


        }
    }

    private fun addAddressApiNew() {
        val request = ApiService.AddAddressRequest(
            governorateId = selectedGovernorateID,
            areaId = selectedAreaID,
            phone = "+965" + binding.pageAddressNew.editPhone.text.toString(),
            firstName = binding.pageAddressNew.editFirstName.text.toString(),
            lastName = binding.pageAddressNew.editLastName.text.toString(),
            email = binding.pageAddressNew.editEmail.text.toString(),
            setDefault = "0",
            addressTitle = binding.pageAddressNew.txtAddressTitle.text.toString(),
            street = binding.pageAddressNew.editStreet.text.toString(),
            appartment = binding.pageAddressNew.editApartment.text.toString(),
            building = binding.pageAddressNew.editBuilding.text.toString(),
            floor = binding.pageAddressNew.editFloor.text.toString(),
            alternatePhone = binding.pageAddressNew.editAlterPhone.text.toString(),
            block = binding.pageAddressNew.editBlock.text.toString(),
            customerId = "0",
            mapLink = binding.pageAddressNew.editLocationUrl.text.toString()


        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.addAddress(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }

    private fun validateData() {
        if (binding.pageList.pageAddress.txtAddressTitle.text.isEmpty()) {
            showAlertCustom("Enter Address Title")
        } else if (binding.pageList.pageAddress.editFirstName.text.isEmpty()) {
            showAlertCustom("Enter First Name")
        } else if (binding.pageList.pageAddress.editLastName.text.isEmpty()) {
            showAlertCustom("Enter Last Name")
        } else if (binding.pageList.pageAddress.editEmail.text.isEmpty()) {
            showAlertCustom("Enter Email")
        } else if (binding.pageList.pageAddress.editPhone.text.isEmpty()) {
            showAlertCustom("Enter Phone")
        } else if (selectedGovernorateID == "") {
            showAlertCustom("Choose Governorate")
        } else if (selectedAreaID == "") {
            showAlertCustom("Choose Area")
        } else if (binding.pageList.pageAddress.editBlock.text.isEmpty()) {
            showAlertCustom("Enter Block")
        } else if (binding.pageList.pageAddress.editStreet.text.isEmpty()) {
            showAlertCustom("Enter Street")
        } else {
            if (edit == "true") {
                editAddressApi()
            } else {
                addAddressApi()
            }

        }
    }

    private fun editAddressApi() {
        val request = ApiService.UpdateAddressRequest(
            id = editAddressID,
            governorateId = selectedGovernorateID,
            areaId = selectedAreaID,
            phone = "+965" + binding.pageList.pageAddress.editPhone.text.toString(),
            firstName = binding.pageList.pageAddress.editFirstName.text.toString(),
            lastName = binding.pageList.pageAddress.editLastName.text.toString(),
            email = binding.pageList.pageAddress.editEmail.text.toString(),
            setDefault = "0",
            addressTitle = binding.pageList.pageAddress.txtAddressTitle.text.toString(),
            street = binding.pageList.pageAddress.editStreet.text.toString(),
            appartment = binding.pageList.pageAddress.editApartment.text.toString(),
            building = binding.pageList.pageAddress.editBuilding.text.toString(),
            floor = binding.pageList.pageAddress.editFloor.text.toString(),
            alternatePhone = binding.pageList.pageAddress.editAlterPhone.text.toString(),
            block = binding.pageList.pageAddress.editBlock.text.toString(),
            customerId = AppPreferences.getInstance(requireContext()).getCustId().toString(),
            mapLink = binding.pageList.pageAddress.editLocationUrl.text.toString()

        )
        binding.pageList.progressBar.visibility = View.VISIBLE
        viewModel.updateAddress(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }

    private fun addAddressApi() {
        val request = ApiService.AddAddressRequest(
            governorateId = selectedGovernorateID,
            areaId = selectedAreaID,
            phone = "+965" + binding.pageList.pageAddress.editPhone.text.toString(),
            firstName = binding.pageList.pageAddress.editFirstName.text.toString(),
            lastName = binding.pageList.pageAddress.editLastName.text.toString(),
            email = binding.pageList.pageAddress.editEmail.text.toString(),
            setDefault = "0",
            addressTitle = binding.pageList.pageAddress.txtAddressTitle.text.toString(),
            street = binding.pageList.pageAddress.editStreet.text.toString(),
            appartment = binding.pageList.pageAddress.editApartment.text.toString(),
            building = binding.pageList.pageAddress.editBuilding.text.toString(),
            floor = binding.pageList.pageAddress.editFloor.text.toString(),
            alternatePhone = binding.pageList.pageAddress.editAlterPhone.text.toString(),
            block = binding.pageList.pageAddress.editBlock.text.toString(),
            customerId = AppPreferences.getInstance(requireContext()).getCustId()
                ?.takeIf { it.isNotEmpty() } ?: "0",
            mapLink = binding.pageList.pageAddress.editLocationUrl.text.toString()


        )
        binding.pageList.progressBar.visibility = View.VISIBLE
        viewModel.addAddress(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }

    private fun handleAddressClick() {
        binding.pageList.pageAddress.imgCloseDate.setOnClickListener {
            binding.pageList.pageAddress.lytAddress.visibility = View.GONE
        }
        binding.pageList.pageAddress.cardAddAddress.setOnClickListener {
            validateData()
        }
        binding.pageList.cardAdd.setOnClickListener {
            setViewAdd()
            binding.pageList.pageAddress.lytAddress.visibility = View.VISIBLE
        }
        binding.pageList.imgBack.setOnClickListener {
            binding.pageList.lytAddress.visibility = View.GONE
        }
    }

    private fun showCustomerDropdown(anchorView: View) {
        val popupView = layoutInflater.inflate(R.layout.popup_customer_list, null)
        popupWindow =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        val recyclerView = popupView.findViewById<RecyclerView>(R.id.recyclerDropdownCustomerList)
        val editSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val progressSmall = popupView.findViewById<ProgressBar>(R.id.progressBarDropdownSmall)
        progressSmall.visibility = View.VISIBLE
        custAdapter = CustListCartAdapter(
            AppPreferences.getInstance(requireContext()).getCustId().toString()
        ) { customer, _ ->
            binding.txtCustomer.text = customer.name
            AppPreferences.getInstance(requireContext()).saveCustId(customer.id)
            AppPreferences.getInstance(requireContext()).saveCustName(customer.name)
            callAddressApi(customer.id.toString())
            popupWindow.dismiss()
        }
        recyclerView.layoutManager = LinearLayoutManager(anchorView.context)
        recyclerView.adapter = custAdapter
        custAdapter.submitList(customerList)
        progressSmall.visibility = View.GONE
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Load more when we reach the end
                if (lastVisibleItem >= totalItemCount - 1 && !viewModel.isLastPageCustomer()) {
                    viewModel.currentPageCustomer++
                    viewModel.loadNextPageCustomer(searchValueCust)
                }
            }
        })
        editSearch.addTextChangedListener(object : TextWatcher {
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
                    searchValueCust = s.toString()
                    callCustomerAPI()
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })
        popupWindow?.elevation = 10f
        popupWindow?.isOutsideTouchable = true
        popupWindow?.showAsDropDown(anchorView)
    }

    private fun callAddressApi(id: String) {
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.AddressListRequest(
            customerId = id
        )
        viewModel.getAddressList(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }

    private fun callCustomerAPI() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.currentPageCustomer = 1
        viewModel.clearCustomerData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.CustomerListRequest(
            keyword = searchValueCust,
            per_page = "5",
            page_no = "1"
        )
        viewModel.getCustomerList(appPreferences.getToken().toString(), request)
    }

    private fun manageAddressTitleNew() {
        val dropdownPopup =
            DropdownPopupAddressTitle(
                requireContext(),
                binding.pageAddressNew.cardAddressTitle,
                createAddressTitleList()
            ) { text ->
                binding.pageAddressNew.txtAddressTitle.text = text

            }

        binding.pageAddressNew.cardAddressTitle.setOnClickListener {
            dropdownPopup.show()
        }
    }

    private fun manageAddressTitle() {
        val dropdownPopup =
            DropdownPopupAddressTitle(
                requireContext(),
                binding.pageList.pageAddress.cardAddressTitle,
                createAddressTitleList()
            ) { text ->
                binding.pageList.pageAddress.txtAddressTitle.text = text

            }

        binding.pageList.pageAddress.cardAddressTitle.setOnClickListener {
            dropdownPopup.show()
        }
    }

    private fun createAddressTitleList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Home/Apartment",
                R.drawable.dummy_image
            ),
            Cat(
                "Office",
                R.drawable.dummy_image
            )
        )
    }

    private fun manageArea(data: List<AreaResponse>?) {
        val dropdownPopup =
            DropdownPopupArea(
                requireContext(),
                binding.pageList.pageAddress.cardArea,
                data
            ) { selectedItem, selectedId, data ->
                binding.pageList.pageAddress.txtArea.text = selectedItem
                selectedAreaID = data?.id.toString()
            }

        binding.pageList.pageAddress.cardArea.setOnClickListener {
            dropdownPopup.show()
        }
    }

    private fun manageAreaNew(data: List<AreaResponse>?) {
        val dropdownPopup =
            DropdownPopupArea(
                requireContext(),
                binding.pageAddressNew.cardArea,
                data
            ) { selectedItem, selectedId, data ->
                binding.pageAddressNew.txtArea.text = selectedItem
                selectedAreaID = data?.id.toString()
            }

        binding.pageAddressNew.cardArea.setOnClickListener {
            dropdownPopup.show()
        }
    }

    private fun manageGovernorate(data: List<GovernorateResponse>?) {
        val dropdownPopup =
            DropdownPopupGovernorate(
                requireContext(),
                binding.pageList.pageAddress.cardGovernorate,
                data
            ) { selectedItem, selectedId, data ->
                binding.pageList.pageAddress.txtGovernorate.text = selectedItem
                selectedGovernorateID = data?.id.toString()

                getAreaByGovernorate(data?.id.toString())
            }

        binding.pageList.pageAddress.cardGovernorate.setOnClickListener {
            dropdownPopup.show()
        }
    }

    private fun manageGovernorateNew(data: List<GovernorateResponse>?) {
        val dropdownPopup =
            DropdownPopupGovernorate(
                requireContext(),
                binding.pageAddressNew.cardGovernorate,
                data
            ) { selectedItem, selectedId, data ->
                binding.pageAddressNew.txtGovernorate.text = selectedItem
                selectedGovernorateID = data?.id.toString()

                getAreaByGovernorate(data?.id.toString())
            }

        binding.pageAddressNew.cardGovernorate.setOnClickListener {
            dropdownPopup.show()
        }
    }

    private fun getAreaByGovernorate(id: String) {
        val request = ApiService.AreaRequest(
            governorateId = id
        )
        viewModel.getAreaList(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )
    }

    private fun manageAddress(data: List<AddressListResponse>?) {

        binding.pageList.recyclerAddressList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val catAdapter = AddressAdapter(data) { Cust, type ->

                if (type == "edit") {
                    edit = "true"
                    selectedAreaID = Cust?.areaId.toString()
                    selectedGovernorateID = Cust?.governorateId.toString()
                    binding.pageList.pageAddress.lytAddress.visibility = View.VISIBLE
                    editAddressID = Cust?.id.toString()
                    setViewEdit(Cust)
                } else {
                    val address = Address(
                        id = Cust?.id.toString(),
                        addressTitle = Cust?.addressTitle.toString(),
                        firstName = Cust?.firstName.toString(),
                        lastName = Cust?.lastName.toString(),
                        email = Cust?.email.toString(),
                        phone = Cust?.phone.toString(),
                        alternatePhone = Cust?.alternatePhone.toString(),
                        areaId = Cust?.areaId.toString(),
                        governorateId = Cust?.governorateId.toString(),
                        block = Cust?.block.toString(),
                        street = Cust?.street.toString(),
                        building = Cust?.building.toString(),
                        appartment = Cust?.appartment.toString(),
                        floor = Cust?.floor.toString(),
                        areaName = Cust?.areaName.toString(),
                        governorateName = Cust?.governorateName.toString(),
                        mapLink = Cust?.mapLink.toString()
                    )
                    AppPreferences.getInstance(requireContext()).saveAddress(address)
                    AppPreferences.getInstance(requireContext()).saveAddressId(Cust?.id)
                    checkAddressAdded()
                    binding.pageList.lytAddress.visibility = View.GONE
                }

            }
            adapter = catAdapter
        }
    }

    private fun setViewEdit(data: AddressListResponse) {

        binding.pageList.pageAddress.txtButton.text = "Edit address"
        binding.pageList.pageAddress.txtAddressTitle.text = data?.addressTitle
        binding.pageList.pageAddress.editFirstName.setText(data?.firstName)
        binding.pageList.pageAddress.editLastName.setText(data?.lastName)
        binding.pageList.pageAddress.editEmail.setText(data?.email)
        binding.pageList.pageAddress.editPhone.setText(data?.phone)
        binding.pageList.pageAddress.editAlterPhone.setText(data?.alternatePhone)
        binding.pageList.pageAddress.txtGovernorate.text = data?.governorateName
        binding.pageList.pageAddress.txtArea.text = data?.areaName
        binding.pageList.pageAddress.editBlock.setText(data?.block)
        binding.pageList.pageAddress.editStreet.setText(data?.street)
        binding.pageList.pageAddress.editBuilding.setText(data?.building)
        binding.pageList.pageAddress.editApartment.setText(data?.appartment)
        binding.pageList.pageAddress.editFloor.setText(data?.floor)
        binding.pageList.pageAddress.editLocationUrl.setText(data?.mapLink)

    }

    private fun setViewAdd() {

        binding.pageList.pageAddress.txtButton.text = "Add address"
        binding.pageList.pageAddress.txtAddressTitle.text = null
        binding.pageList.pageAddress.editFirstName.setText(null)
        binding.pageList.pageAddress.editLastName.setText(null)
        binding.pageList.pageAddress.editEmail.setText(null)
        binding.pageList.pageAddress.editPhone.setText(null)
        binding.pageList.pageAddress.editAlterPhone.setText(null)
        binding.pageList.pageAddress.txtGovernorate.text = null
        binding.pageList.pageAddress.txtArea.text = null
        binding.pageList.pageAddress.editBlock.setText(null)
        binding.pageList.pageAddress.editStreet.setText(null)
        binding.pageList.pageAddress.editBuilding.setText(null)
        binding.pageList.pageAddress.editApartment.setText(null)
        binding.pageList.pageAddress.editFloor.setText(null)
        binding.pageList.pageAddress.editLocationUrl.setText(null)

    }
    private fun handleFilterClick() {
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
            binding.txtInfluencer.text = null
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
    private fun callCartApi() {
        binding.progressBarCart.visibility = View.VISIBLE
        viewModel.getCartList(AppPreferences.getInstance(requireContext()).getToken().toString())
    }

    private fun callOrderListApi() {
        binding.progressBarOrder.visibility = View.VISIBLE
        val request = ApiService.OrderListRequest(
            area = "",
            per_page = "3",
            page_no = "1",
            search = "",
            date = "",
            order_type = "",
            collected_by_seller = "",
            zones = "",
            payment_change = "",
            payment_method = "",
            payment_status = "",
            collected_by = "",
            delivery_boy = "",
            delivery_status = "",
            userId = "",
            IsPrescription = "0",
            driverCleared = ""
        )
        viewModel.getOrderList(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
    }


    private fun setUpPagination() {
        binding.recyclerProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!viewModel.isLastPage() && !isLoading &&
                    totalItemCount <= lastVisibleItemPosition + visibleThreshold) {

                    isLoading = true
                    viewModel.currentPage++
                    binding.progressBarSmall.visibility = View.VISIBLE

                    viewModel.loadNextPage(selectedMedicalRepId,selectedSellerId,searchValue,selectedInfluencerId,binding.pageFilter.editFilterSearchBySku.text.toString(),selectedBrandId,"","")

                }
            }
        })
    }


    private fun setUpRecyclerView() {
        productAdapter = POSRXListAdapter(
            mutableListOf(),
            "shop"
        ) { product, type ->

            if (type == "add"){


                    binding.progressBar.visibility = View.VISIBLE
                    val request = ApiService.AddCartRequest(
                        productId = product?.id.toString(),
                        quantity = "1"
                    )
                    viewModel.addCartShop(AppPreferences.getInstance(requireContext()).getToken().toString(),request)


            }else if(type == "info"){
                showPickUpDialog(product)
            }else if(type == "view"){
                showPosViewDialog(product)
            }else{
                AppPreferences.getInstance(requireContext()).saveProductId(product?.id.toString())
                findNavController().navigate(R.id.nav_pos_shop_details)
            }

        }

        binding.recyclerProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = productAdapter
        }
    }
    private fun showPickUpDialog(product: Products) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_pos_info, null)

        val txtHead = dialogView.findViewById<TextView>(R.id.txt_heading)
        val txtApixSolution = dialogView.findViewById<TextView>(R.id.txt_apix_solution)
        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)

        txtApixSolution.text = "Apix Solutions : "+product?.currentStock


        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        imgClose.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()

    }
    private fun showPosViewDialog(product: Products) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_pos_view, null)
1
        val txtItemLeastPriceSold = dialogView.findViewById<TextView>(R.id.txt_item_least_price_sold)
        val txtLastSoldPrice = dialogView.findViewById<TextView>(R.id.txt_last_sold_price)
        val txtSeller = dialogView.findViewById<TextView>(R.id.txt_seller)
        val txtDateOfSale = dialogView.findViewById<TextView>(R.id.txt_date_of_sale)
        val txtPurchaseForm = dialogView.findViewById<TextView>(R.id.txt_purchase_form)
        val txtLessPrice = dialogView.findViewById<TextView>(R.id.txt_less_price)
        val txtSellerLeastPriceSold = dialogView.findViewById<TextView>(R.id.txt_seller_least_price_sold)
        val imgClose = dialogView.findViewById<MaterialCardView>(R.id.card_close)

        txtItemLeastPriceSold.text = "Item Least Price Sold: "+product?.basePrice
        txtLastSoldPrice.text = "Item Least Price Sold: "+product?.basePrice
        txtSeller.text = "Seller: "+product?.sellerName
        txtDateOfSale.text = "Date of Sale: "+product?.createdAt
        txtPurchaseForm.text = "Purchase From: "
        txtLessPrice.text = "Less Price: "+product?.basePrice
        txtSellerLeastPriceSold.text = "Seller Least Price Sold: "+product?.basePrice



        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        imgClose.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()

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
        viewModel.customerListData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
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
                customerList.clear()
                customerList.addAll(data?.users!!)
            } else {
                customerList.addAll(data?.users!!)
            }

            if (::custAdapter.isInitialized) {
                custAdapter.submitList(customerList.toList()) // pass a new list to trigger diff
            }


        }
        viewModel.productEventCust.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.addAddressStatus.observe(viewLifecycleOwner) { message ->
            binding.pageList.progressBar.visibility = View.GONE
            binding.pageList.pageAddress.lytAddress.visibility = View.GONE
            val request = ApiService.AddressListRequest(
                customerId = AppPreferences.getInstance(requireContext()).getCustId().toString()
            )
            viewModel.getAddressList(
                AppPreferences.getInstance(requireContext()).getToken().toString(), request
            )

        }
        viewModel.addAddressData.observe(viewLifecycleOwner) { Cust ->
            binding.progressBar.visibility = View.GONE
            binding.pageAddressNew.lytAddress.visibility = View.GONE

            val appPreferences = AppPreferences.getInstance(requireContext())
            binding.txtCustomer.text = Cust?.get(0)?.firstName
            appPreferences.saveAddressId(Cust?.get(0)?.id)
            val address = Address(
                id = Cust?.get(0)?.id.toString(),
                addressTitle = Cust?.get(0)?.addressTitle.toString(),
                firstName = Cust?.get(0)?.firstName.toString(),
                lastName = Cust?.get(0)?.lastName.toString(),
                email = Cust?.get(0)?.email.toString(),
                phone = Cust?.get(0)?.phone.toString(),
                alternatePhone = Cust?.get(0)?.alternatePhone.toString(),
                areaId = Cust?.get(0)?.areaId.toString(),
                governorateId = Cust?.get(0)?.governorateId.toString(),
                block = Cust?.get(0)?.block.toString(),
                street = Cust?.get(0)?.street.toString(),
                building = Cust?.get(0)?.building.toString(),
                appartment = Cust?.get(0)?.appartment.toString(),
                floor = Cust?.get(0)?.floor.toString(),
                areaName = Cust?.get(0)?.areaName.toString(),
                governorateName = Cust?.get(0)?.governorateName.toString(),
                mapLink = Cust?.get(0)?.mapLink.toString()
            )
            appPreferences.saveAddress(address)
            checkAddressAdded()
        }
        viewModel.addressListData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE


            manageAddress(data)
        }
        viewModel.areaListData.observe(viewLifecycleOwner) { data ->

            manageArea(data)
            manageAreaNew(data)

        }
        viewModel.governorateListData.observe(viewLifecycleOwner) { data ->
            manageGovernorate(data)
            manageGovernorateNew(data)
            manageAddressTitle()
            manageAddressTitleNew()
        }



        viewModel.getGovernorateList(appPreferences.getToken().toString())

        viewModel.addUnitFailStatus.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarOrder.visibility = View.GONE

        }
        viewModel.deleteCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.VISIBLE
            println("me =--------------")
            viewModel.getCartList(appPreferences.getToken().toString())

        }
        viewModel.changeQuantityCartShopStatus.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
           binding.progressBarCart.visibility = View.VISIBLE
            viewModel.getCartList(appPreferences.getToken().toString())

        }
        viewModel.cartListData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarCart.visibility = View.GONE
            manageCart(data?.cartItems)


            binding.txtSubTotal.text = data?.subTotal
            binding.txtItemCount.text = data?.itemCount
            binding.txtShipping.text = data?.shippingCost
            binding.txtDiscount.text = data?.discount
            binding.txtTotal.text = data?.grandTotal
            binding.txtCartCount.text = data?.cartItems?.size.toString()

            AppPreferences.getInstance(requireContext()).saveCartCount(data?.cartItems?.size.toString())

            if (data?.itemCount == "0"){
                binding.rltCartCount.visibility = View.INVISIBLE
            }else{
                binding.rltCartCount.visibility = View.VISIBLE
            }
        }
        viewModel.apixChangeData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
         binding.progressBarCart.visibility = View.VISIBLE
            viewModel.getCartList(appPreferences.getToken().toString())

        }
        viewModel.orderListData.observe(viewLifecycleOwner) { data ->

            binding.progressBarOrder.visibility = View.GONE
            manageOrderList(data?.orders)

        }
        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE


        }

        viewModel.patientIdStatus.observe(viewLifecycleOwner) { patient_id ->
            binding.progressBar.visibility = View.GONE
           patientId = patient_id


        }
        viewModel.addCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
            viewModel.getCartList(appPreferences.getToken().toString())

        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

            isLoading = false
            binding.pageShimmer.shimmer.visibility = View.GONE
            binding.pageShimmer.shimmer.stopShimmer()

        }
        viewModel.productListData.observe(viewLifecycleOwner) { data ->

            binding.progressBar.visibility = View.GONE
            binding.progressBarSmall.visibility = View.GONE
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
            manageInfluencer(data)
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
        viewModel.getInfluencerList(appPreferences.getToken().toString())

    }
    private fun manageCart(cartItems: List<CartList>?) {

        binding.recyclerCart.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val catAdapter = POSShopCartAdapter(cartItems, { Cat, position ->

            }, { isKeyboardVisible ->

            },
                { cart, apix_value ->
                    val request = ApiService.ChangeApixRequest(
                        id = cart.id.toString(),
                        apix_discount = apix_value
                    )
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.changeApixCart(AppPreferences.getInstance(requireContext()).getToken().toString(), request)
                },
                { cart, count ->
                    val request = ApiService.ChangeCartRequest(
                        id = cart.id.toString(),
                        quantity = count
                    )
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.changeQuantityCartShop(AppPreferences.getInstance(requireContext()).getToken().toString(), request)
                },
                { cart,count ->
                    binding.progressBar.visibility = View.VISIBLE
                    val request = ApiService.ChangeCartRequest(
                        id = cart.id.toString(),
                        quantity = count
                    )
                    viewModel.changeQuantityCartShop(AppPreferences.getInstance(requireContext()).getToken().toString(), request)
                }
            ) { cart ->
                binding.progressBar.visibility = View.VISIBLE
                val request = ApiService.CartRemoveRequest(
                    id = cart.id.toString(),
                )
                viewModel.deleteCartShop(AppPreferences.getInstance(requireContext()).getToken().toString(), request)

            }
            adapter = catAdapter
        }
    }
    private fun manageOrderList(data: List<Orders>?){
        if (data.isNullOrEmpty()) {
            binding.txtNoDataOrder.visibility = View.VISIBLE
            binding.cardViewAllOrder.visibility = View.GONE
        } else {
            binding.cardViewAllOrder.visibility = View.VISIBLE
            binding.txtNoDataOrder.visibility = View.GONE
            binding.recyclerAllOrders.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                val catAdapter = OrderListAdapter(data!!,"admin") { data, position ->

                }
                adapter = catAdapter
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

    private fun manageProducts(data: ProductListResponse?) {

        binding.recyclerProducts.visibility = View.VISIBLE
        productAdapter.setProducts(data?.products?.toMutableList() ?: mutableListOf())
    }

    private fun manageInfluencer(data: List<InfluencerListResponse>?) {
        val dropdownPopup =
            DropdownPopupInfluencer(
                requireContext(),
                binding.cardInfluencer,
                data
            ) { selectedItem, selectedId, data ->
                binding.txtInfluencer.text = selectedItem
                selectedInfluencerId = data?.id.toString()
                callProductAPI()
            }

        binding.cardInfluencer.setOnClickListener {
            dropdownPopup.show()
        }
    }

    private fun callProductAPI() {

        binding.pageShimmer.shimmer.visibility = View.VISIBLE
        binding.pageShimmer.shimmer.startShimmer()

        binding.recyclerProducts.visibility = View.GONE
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
            isFavourite = "0",
            listFrom = ""
        )
        binding.progressBar.visibility = View.VISIBLE

        viewModel.getProductList(appPreferences.getToken().toString(), request)
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
    override fun onResume() {
        super.onResume()

        if (AppPreferences.getInstance(requireContext()).getCustName() != null) {
            binding.txtCustomer.text = AppPreferences.getInstance(requireContext()).getCustName()
        }
    }
    private fun handleClick() {
        binding.lytChooseCustomer.setOnClickListener {
            viewModel.currentPageCustomer = 1
            callCustomerAPI()
            showCustomerDropdown(it)
        }
        binding.addCust.setOnClickListener {
            val custId = AppPreferences.getInstance(requireContext()).getCustId()

            if (custId == null) {
                binding.pageAddressNew.lytAddress.visibility = View.VISIBLE
            }else{
                binding.pageList.lytAddress.visibility = View.VISIBLE
            }
        }

        binding.cardProceed.setOnClickListener {
            val addressId = AppPreferences.getInstance(requireContext()).getAddressId()

            if (addressId == null) {
                showAlertCustom("Add Shipping Details")
            } else {
                findNavController().navigate(R.id.nav_pos_cart_summery)
            }
        }

        binding.cardFilter.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.VISIBLE
        }
        binding.pageFilter.imgClose.setOnClickListener {
            binding.pageFilter.lytFilter.visibility = View.GONE
        }
        binding.cardViewAllOrder.setOnClickListener {
            findNavController().navigate(R.id.nav_sale_details)
        }

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imgCart.setOnClickListener {
            findNavController().navigate(R.id.nav_cart)
        }


    }

}