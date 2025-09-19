package com.wasfa.doctor.ui.customers.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentEditCustomerBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.AddressList
import com.wasfa.doctor.network.response.AreaResponse
import com.wasfa.doctor.network.response.CustomerDetailsResponse
import com.wasfa.doctor.network.response.GovernorateResponse
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupAddressTitle
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupArea
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupGovernorate
import com.wasfa.doctor.ui.customers.adapter.CustAddressListAdapter
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class EditCustomerFragment : Fragment() {
    private var _binding: FragmentEditCustomerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    var selectedGovernorateID: String = ""
    var selectedAreaID: String = ""
    var edit : String = "false"
    var editAddressID : String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        setViewModel()
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
        viewModel.customerUpdateStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()

        }
        viewModel.customerUpdateStatusFail.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.customerDetails.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE

            manageData(data)

        }
        viewModel.areaListData.observe(viewLifecycleOwner) { data ->

            manageArea(data)

        }
        viewModel.addAddressStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            binding.pageAddress.lytAddress.visibility = View.GONE
            val request = ApiService.CartRemoveRequest(
                id = appPreferences.getCustId().toString()
            )
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getCustomerDetails(appPreferences.getToken().toString(),request)

        }

        viewModel.governorateListData.observe(viewLifecycleOwner) { data ->
            manageGovernorate(data)
            manageAddressTitle()
        }
        viewModel.deleteCustStatusFail.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.deleteCustStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
            val request = ApiService.CartRemoveRequest(
                id = appPreferences.getCustId().toString()
            )
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getCustomerDetails(appPreferences.getToken().toString(),request)
        }


        viewModel.getGovernorateList(appPreferences.getToken().toString())
        val request = ApiService.CartRemoveRequest(
            id = appPreferences.getCustId().toString()
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getCustomerDetails(appPreferences.getToken().toString(),request)

    }

    private fun manageData(data: List<CustomerDetailsResponse>?) {

        binding.editName.setText(data?.get(0)?.name
            ?.takeIf { it.isNotBlank() } ?: "")

        binding.editPhone.setText(data?.get(0)?.phone
            ?.takeIf { it.isNotBlank() } ?: "")

        binding.editEmail.setText(data?.get(0)?.email
            ?.takeIf { it.isNotBlank() } ?: "")

        manageAddress(data?.get(0)?.addressList)
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

    private fun manageAddress(addressList: List<AddressList>?) {

            binding.recyclerAddress.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                val catAdapter = CustAddressListAdapter(addressList) { Cust, type ->

                    if (type == "edit"){
                        edit = "true"
                        selectedAreaID = Cust?.areaId.toString()
                        selectedGovernorateID = Cust?.governorateId.toString()
                        binding.pageAddress.lytAddress.visibility = View.VISIBLE
                        editAddressID = Cust?.id.toString()
                        setViewEdit(Cust)
                    }else{
                        showDeletePopup(Cust?.id.toString())
                    }

                }
                adapter = catAdapter
            }

    }
    private fun setViewEdit(data: AddressList) {

        binding.pageAddress.txtButton.text = "Edit address"
        binding.pageAddress.txtAddressTitle.text = data?.addressTitle
        binding.pageAddress.editFirstName.setText(data?.firstName)
        binding.pageAddress.editLastName.setText(data?.lastName)
        binding.pageAddress.editEmail.setText(data?.email)
        binding.pageAddress.editPhone.setText(data?.phone)
        binding.pageAddress.editAlterPhone.setText(data?.alternatePhone)
        binding.pageAddress.txtGovernorate.text = data?.governorateName
        binding.pageAddress.txtArea.text = data?.areaName
        binding.pageAddress.editBlock.setText(data?.block)
        binding.pageAddress.editStreet.setText(data?.street)
        binding.pageAddress.editBuilding.setText(data?.building)
        binding.pageAddress.editApartment.setText(data?.appartment)
        binding.pageAddress.editFloor.setText(data?.floor)
        binding.pageAddress.editLocationUrl.setText(data?.mapLink)



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
            viewModel.deleteCustomerAddress(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
            dialog?.dismiss()

        }
        dialog = builder.create()
        dialog.show()
    }
    private fun handleClick() {

        binding.cardSave.setOnClickListener {
            validateData()
        }

        binding.cardAdd.setOnClickListener {
            binding.pageAddress.lytAddress.visibility = View.VISIBLE
        }
        binding.pageAddress.imgCloseDate.setOnClickListener {
            binding.pageAddress.lytAddress.visibility = View.GONE
        }
        binding.pageAddress.cardAddAddress.setOnClickListener {
            validateDataAddress()
        }


        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun validateDataAddress() {
        if (binding.pageAddress.txtAddressTitle.text.isEmpty()) {
            showAlertCustom("Enter Address Title")
        } else if (binding.pageAddress.editFirstName.text.isEmpty()) {
            showAlertCustom("Enter First Name")
        } else if (binding.pageAddress.editLastName.text.isEmpty()) {
            showAlertCustom("Enter Last Name")
        } else if (binding.pageAddress.editEmail.text.isEmpty()) {
            showAlertCustom("Enter Email")
        } else if (binding.pageAddress.editPhone.text.isEmpty()) {
            showAlertCustom("Enter Phone")
        } else if (selectedGovernorateID == "") {
            showAlertCustom("Choose Governorate")
        }else if (selectedAreaID == "") {
            showAlertCustom("Choose Area")
        } else if (binding.pageAddress.editBlock.text.isEmpty()) {
            showAlertCustom("Enter Block")
        } else if (binding.pageAddress.editStreet.text.isEmpty()) {
            showAlertCustom("Enter Street")
        } else {
            if (edit == "true"){
                editAddressApi()
            }else{
                addAddressApi()
            }

        }
    }
    private fun manageAddressTitle() {
        val dropdownPopup =
            DropdownPopupAddressTitle(
                requireContext(),
                binding.pageAddress.cardAddressTitle,
                createAddressTitleList()
            ) { text ->
                binding.pageAddress.txtAddressTitle.text = text

            }

        binding.pageAddress.cardAddressTitle.setOnClickListener {
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
                binding.pageAddress.cardArea,
                data
            ) { selectedItem, selectedId, data ->
                binding.pageAddress.txtArea.text = selectedItem
                selectedAreaID = data?.id.toString()
            }

        binding.pageAddress.cardArea.setOnClickListener {
            dropdownPopup.show()
        }
    }
    private fun manageGovernorate(data: List<GovernorateResponse>?){
        val dropdownPopup =
            DropdownPopupGovernorate(
                requireContext(),
                binding.pageAddress.cardGovernorate,
                data
            ) { selectedItem, selectedId, data ->
                binding.pageAddress.txtGovernorate.text = selectedItem
                selectedGovernorateID = data?.id.toString()

                getAreaByGovernorate(data?.id.toString())
            }

        binding.pageAddress.cardGovernorate.setOnClickListener {
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
    private fun validateData() {

        if (binding.editEmail.text.isNullOrEmpty()){
            showAlertCustom("Enter Valid Email Address")
        }else if(binding.editName.text.isNullOrEmpty()){
            showAlertCustom("Enter Valid Customer Name")
        }else if(binding.editPhone.text.isNullOrEmpty()){
            showAlertCustom("Enter Valid Phone Number")
        }else{
            binding.progressBar.visibility = View.VISIBLE
            val request = ApiService.CustomerUpdateRequest(
                id = AppPreferences.getInstance(requireContext()).getCustId().toString(),
                name = binding.editName.text.toString(),
                phone = binding.editPhone.text.toString(),
                email = binding.editEmail.text.toString()
            )
            viewModel.customerUpdate(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
        }
    }
    private fun editAddressApi() {
        val request = ApiService.UpdateAddressRequest(
            id = editAddressID,
            governorateId = selectedGovernorateID,
            areaId = selectedAreaID,
            phone = "+965"+binding.pageAddress.editPhone.text.toString(),
            firstName = binding.pageAddress.editFirstName.text.toString(),
            lastName = binding.pageAddress.editLastName.text.toString(),
            email = binding.pageAddress.editEmail.text.toString(),
            setDefault = "0",
            addressTitle = binding.pageAddress.txtAddressTitle.text.toString(),
            street = binding.pageAddress.editStreet.text.toString(),
            appartment = binding.pageAddress.editApartment.text.toString(),
            building = binding.pageAddress.editBuilding.text.toString(),
            floor = binding.pageAddress.editFloor.text.toString(),
            alternatePhone = binding.pageAddress.editAlterPhone.text.toString(),
            block = binding.pageAddress.editBlock.text.toString(),
            customerId = AppPreferences.getInstance(requireContext()).getCustId().toString(),
            mapLink = binding.pageAddress.editLocationUrl.text.toString()

        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.updateAddress(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }
    private fun addAddressApi() {
        val request = ApiService.AddAddressRequest(
            governorateId = selectedGovernorateID,
            areaId = selectedAreaID,
            phone = "+965"+binding.pageAddress.editPhone.text.toString(),
            firstName = binding.pageAddress.editFirstName.text.toString(),
            lastName = binding.pageAddress.editLastName.text.toString(),
            email = binding.pageAddress.editEmail.text.toString(),
            setDefault = "0",
            addressTitle = binding.pageAddress.txtAddressTitle.text.toString(),
            street = binding.pageAddress.editStreet.text.toString(),
            appartment = binding.pageAddress.editApartment.text.toString(),
            building = binding.pageAddress.editBuilding.text.toString(),
            floor = binding.pageAddress.editFloor.text.toString(),
            alternatePhone = binding.pageAddress.editAlterPhone.text.toString(),
            block = binding.pageAddress.editBlock.text.toString(),
            customerId = AppPreferences.getInstance(requireContext()).getCustId().toString(),
            mapLink = binding.pageAddress.editLocationUrl.text.toString()

        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.addAddress(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }
}