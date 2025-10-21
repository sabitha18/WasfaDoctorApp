package com.wasfa.doctor.ui.med

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
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentPosShopCartBinding
import com.wasfa.doctor.helper.Address
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.AddressListResponse
import com.wasfa.doctor.network.response.AreaResponse
import com.wasfa.doctor.network.response.CartList
import com.wasfa.doctor.network.response.GovernorateResponse
import com.wasfa.doctor.network.response.UserDetails
import com.wasfa.doctor.ui.pres.cart.adapter.AddressAdapter
import com.wasfa.doctor.ui.med.adapter.CustListCartAdapter
import com.wasfa.doctor.ui.model.Cat
import com.wasfa.doctor.ui.pres.cart.adapter.DropdownPopupAddressTitle
import com.wasfa.doctor.ui.pres.cart.adapter.DropdownPopupArea
import com.wasfa.doctor.ui.pres.cart.adapter.DropdownPopupGovernorate
import com.wasfa.doctor.ui.pres.cart.adapter.POSShopCartAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class POSShopCartFragment : Fragment() {
    private var _binding: FragmentPosShopCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private var keyboardVisibilityListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private lateinit var popupWindow: PopupWindow
    private lateinit var custAdapter: CustListCartAdapter
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    var selectedGovernorateID: String = ""
    var selectedAreaID: String = ""
    var searchValue: String = ""
    var edit: String = "false"
    private val customerList = mutableListOf<UserDetails>()
    var editAddressID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPosShopCartBinding.inflate(inflater, container, false)
        return binding.root

    }
    private fun checkAddressAdded() {
        if (AppPreferences.getInstance(requireContext()).getAddressId() == null){
            binding.addCust.setCardBackgroundColor(Color.parseColor("#A61C5C"))
        }else{
            binding.addCust.setCardBackgroundColor(Color.parseColor("#008000"))
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        handleAddressClick()
        handleSeparateAddressClick()
        setViewModel()
        checkAddressAdded()

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
            mapLink = binding.pageAddressNew.editLocationUrl.text.toString()

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
                    viewModel.loadNextPageCustomer(searchValue)
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
                    searchValue = s.toString()
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
            keyword = searchValue,
            per_page = "5",
            page_no = "1"
        )
        viewModel.getCustomerList(appPreferences.getToken().toString(), request)
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


        viewModel.deleteCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.VISIBLE
            println("me =--------------")
            viewModel.getCartList(appPreferences.getToken().toString())

        }
        viewModel.changeQuantityCartShopStatus.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            viewModel.getCartList(appPreferences.getToken().toString())

        }
        viewModel.cartListData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            manageCart(data?.cartItems)


            binding.txtSubTotal.text = data?.subTotal
            binding.txtItemCount.text = data?.itemCount
            binding.txtShipping.text = data?.shippingCost
            binding.txtDiscount.text = data?.discount
            binding.txtTotal.text = data?.grandTotal


            AppPreferences.getInstance(requireContext())
                .saveCartCount(data?.cartItems?.size.toString())

        }
        viewModel.apixChangeData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            viewModel.getCartList(appPreferences.getToken().toString())

        }
        binding.progressBar.visibility = View.VISIBLE
        println("me =-------------- ----------------------")
        viewModel.getCartList(appPreferences.getToken().toString())
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

    private fun handleClick() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

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
                // Proceed if address is set
                findNavController().navigate(R.id.nav_pos_cart_summery)
            }
        }


    }

    private fun manageCart(cartItems: List<CartList>?) {
        if (cartItems.isNullOrEmpty()) {
            binding.txtNoData.visibility = View.VISIBLE
            binding.recyclerRxCart.visibility = View.GONE
        } else {
            binding.txtNoData.visibility = View.GONE
            binding.recyclerRxCart.visibility = View.VISIBLE
        }
        binding.recyclerRxCart.apply {
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
                    viewModel.changeApixCart(
                        AppPreferences.getInstance(requireContext()).getToken().toString(), request
                    )
                },
                { cart, count ->
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

    override fun onResume() {
        super.onResume()

        if (AppPreferences.getInstance(requireContext()).getCustName() != null) {
            binding.txtCustomer.text = AppPreferences.getInstance(requireContext()).getCustName()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        keyboardVisibilityListener?.let {
            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(it)
        }
        _binding = null
    }

}