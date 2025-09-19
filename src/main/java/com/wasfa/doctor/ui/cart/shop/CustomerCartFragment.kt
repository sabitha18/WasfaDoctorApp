package com.wasfa.doctor.ui.cart.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.wasfa.doctor.databinding.FragmentCustomerCartBinding
import com.wasfa.doctor.helper.Address
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.AddressListResponse
import com.wasfa.doctor.network.response.AreaResponse
import com.wasfa.doctor.network.response.GovernorateResponse
import com.wasfa.doctor.ui.cart.adapter.AddressAdapter
import com.wasfa.doctor.ui.cart.adapter.CustListCartAdapter
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupAddressTitle
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupArea
import com.wasfa.doctor.ui.cart.adapter.DropdownPopupGovernorate
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class CustomerCartFragment : Fragment() {
    private var _binding: FragmentCustomerCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    var selectedGovernorateID: String = ""
    var selectedAreaID: String = ""
    var searchValue: String = ""
    var edit : String = "false"
    var editAddressID : String = ""
    private lateinit var custAdapter: CustListCartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerCartBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBarCust.visibility = View.VISIBLE
        handleClick()
        setViewModel()
        callCustomerAPI()
        setUpPagination()
    }


    private fun setUpPagination() {

        binding.recyclerCustomerList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val isNotLoadingAndNotLastPage = !viewModel.loadingCustomer && !viewModel.isLastPageCustomer()
                val isAtLastItem = (firstVisibleItemPosition + visibleItemCount) >= totalItemCount
                val isNotAtBeginning = firstVisibleItemPosition >= 0
                val isTotalMoreThanVisible = totalItemCount >= 10 // Use your page size here (e.g., 10 or 20)

                if (isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible) {
                    viewModel.loadingCustomer = true // Set loading flag
                    viewModel.currentPageCustomer++
                    viewModel.loadNextPageCustomer(searchValue)

                    if (binding.progressBarCust.visibility == View.VISIBLE){
                        binding.progressBarSmall.visibility = View.GONE
                    }else{
                        binding.progressBarSmall.visibility = View.VISIBLE
                    }

                }
            }
        })

    }
    private fun callCustomerAPI() {


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

            binding.progressBarCust.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
           binding.progressBarCust.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.addAddressStatus.observe(viewLifecycleOwner) { message ->
            binding.pageList.progressBar.visibility = View.GONE
            binding.pageList.pageAddress.lytAddress.visibility = View.GONE
            val request = ApiService.AddressListRequest(
                customerId = AppPreferences.getInstance(requireContext()).getCustId().toString()
            )
            viewModel.getAddressList(AppPreferences.getInstance(requireContext()).getToken().toString(),request)

        }
        viewModel.addressListData.observe(viewLifecycleOwner) { data ->
            binding.progressBarCust.visibility = View.GONE
            binding.pageList.lytAddress.visibility = View.VISIBLE

            manageAddress(data)
        }
        viewModel.areaListData.observe(viewLifecycleOwner) { data ->

            manageArea(data)

        }


        viewModel.governorateListData.observe(viewLifecycleOwner) { data ->
            manageGovernorate(data)
            manageAddressTitle()
        }



        viewModel.getGovernorateList(appPreferences.getToken().toString())
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
    private fun manageGovernorate(data: List<GovernorateResponse>?){
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
    private fun getAreaByGovernorate(id: String) {
        val request = ApiService.AreaRequest(
            governorateId = id
        )
        viewModel.getAreaList(
            AppPreferences.getInstance(requireContext()).getToken().toString(), request
        )
    }
    private fun manageAddress(data: List<AddressListResponse>?) {
        binding.pageList.recyclerAddressList.layoutAnimation =
            android.view.animation.AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        binding.pageList.recyclerAddressList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val catAdapter = AddressAdapter(data) { Cust, type ->

                if (type == "edit"){
                    edit = "true"
                    selectedAreaID = Cust?.areaId.toString()
                    selectedGovernorateID = Cust?.governorateId.toString()
                    binding.pageList.pageAddress.lytAddress.visibility = View.VISIBLE
                    editAddressID = Cust?.id.toString()
                    setViewEdit(Cust)
                }else{
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
                    findNavController().popBackStack()
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
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.pageList.pageAddress.imgCloseDate.setOnClickListener {
            binding.pageList.pageAddress.lytAddress.visibility = View.GONE
        }

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
        }else if (selectedAreaID == "") {
            showAlertCustom("Choose Area")
        } else if (binding.pageList.pageAddress.editBlock.text.isEmpty()) {
            showAlertCustom("Enter Block")
        } else if (binding.pageList.pageAddress.editStreet.text.isEmpty()) {
            showAlertCustom("Enter Street")
        } else {
            if (edit == "true"){
                editAddressApi()
            }else{
                addAddressApi()
            }

        }
    }
    private fun editAddressApi() {
        val request = ApiService.UpdateAddressRequest(
            id = editAddressID,
            governorateId = selectedGovernorateID,
            areaId = selectedAreaID,
            phone = "+965"+binding.pageList.pageAddress.editPhone.text.toString(),
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
            phone = "+965"+binding.pageList.pageAddress.editPhone.text.toString(),
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
        viewModel.addAddress(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }
}