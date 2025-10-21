package com.wasfa.doctor.ui.pres.add

import android.app.DatePickerDialog
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
import com.wasfa.doctor.databinding.FragmentAddPresNewBinding
import com.wasfa.doctor.ui.main.DoctorHomeActivity
import com.wasfa.doctor.ui.pres.PresDetailsFragment
import com.wasfa.doctor.ui.pres.PrescriptionFragment
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CountryListResponse
import com.wasfa.doctor.network.response.Orders
import com.wasfa.doctor.network.response.UserDetails
import com.wasfa.doctor.ui.model.Cat
import com.wasfa.doctor.ui.pres.adapter.CountryAdapter
import com.wasfa.doctor.ui.pres.adapter.POSRXListAdapter
import com.wasfa.doctor.ui.pres.adapter.OrderListAdapter
import com.wasfa.doctor.ui.pres.adapter.ProductStockAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.util.Calendar

class AddPresNewFragment : Fragment() {
    private var _binding: FragmentAddPresNewBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: POSRXListAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var selectedInfluencerId: String = ""
    var searchValue: String = ""
    var patientId = ""
    private var popupWindowGender: PopupWindow? = null
    private var popupWindowNationality: PopupWindow? = null
    private lateinit var productStockAdapter: ProductStockAdapter
    private lateinit var countryAdapter: CountryAdapter
    var countryId: String = ""
    private val countryList = mutableListOf<CountryListResponse>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPresNewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? DoctorHomeActivity)?.hideBottomNav()
        handleClick()
        setViewModel()
        handleEditTexts()
    }

    private fun callOrderListApi() {
        binding.cardNext.visibility = View.VISIBLE
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
            userId = patientId,
            IsPrescription = "1",
            driverCleared = ""
        )
        viewModel.getOrderList(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
    }

    private fun handleEditTexts() {
        binding.editPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.txtPhoneEmpty.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s.toString()
                if (phoneNumber.length == 8) {
                    checkCustomerExistCall(phoneNumber)
                }
            }
        })
        binding.editCivilId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.txtCivilEmpty.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.editFullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.txtNameEmpty.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.txtEmailEmpty.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun checkCustomerExistCall(phoneNumber: String) {

        binding.progressBar.visibility = View.VISIBLE
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.CheckPatientRequest(
            keyword = "+965$phoneNumber"
        )
        viewModel.checkCustomerExist(appPreferences.getToken().toString(),request)
        viewModel.customerListData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            manageCustomer(data?.users)
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
        viewModel.addUnitFailStatus.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarOrder.visibility = View.GONE

        }
        viewModel.deleteCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.VISIBLE
            println("me =--------------")
            viewModel.getCart(appPreferences.getToken().toString())

        }
        viewModel.cartUpdateStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            viewModel.getCart(appPreferences.getToken().toString())

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
            showAlertCustom(message)

        }
        viewModel.createdStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            if (message == "Patient saved successfully"){
                binding.cardCustBox.strokeColor = Color.parseColor("#00BCD7")
                binding.rltBtn.visibility = View.VISIBLE
                binding.cardNext.visibility = View.VISIBLE
                binding.cardCreate.visibility = View.GONE
                binding.cardUpdate.visibility = View.VISIBLE

            }else{
                binding.cardNext.visibility = View.GONE
                binding.rltBtn.visibility = View.VISIBLE
            }
           Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
            //56567876

        }
        viewModel.patientIdStatus.observe(viewLifecycleOwner) { patient_id ->
            binding.progressBar.visibility = View.GONE
           patientId = patient_id
            callOrderListApi()

        }
        viewModel.addCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
            viewModel.getCart(appPreferences.getToken().toString())

        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE



        }
        viewModel.influencerList.observe(viewLifecycleOwner) { data ->
            //manageInfluencer(data)
        }

        viewModel.countryList.observe(viewLifecycleOwner) { data ->

            countryList.addAll(data)
        }
        viewModel.getCountryList(appPreferences.getToken().toString())
        viewModel.getInfluencerList(appPreferences.getToken().toString())
        viewModel.emptyCart(appPreferences.getToken().toString())

    }
    private fun manageOrderList(data: List<Orders>?){
        if (data.isNullOrEmpty()) {
            binding.txtNoDataOrder.visibility = View.VISIBLE
            binding.cardViewAllOrder.visibility = View.GONE
            binding.recyclerAllOrders.visibility = View.GONE
        } else {
            binding.recyclerAllOrders.visibility = View.VISIBLE
            binding.cardViewAllOrder.visibility = View.VISIBLE
            binding.txtNoDataOrder.visibility = View.GONE
            binding.recyclerAllOrders.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                val catAdapter = OrderListAdapter(data!!,"doctor") { data, position ->

                    AppPreferences.getInstance(requireContext()).savePresID(data?.prescriptionNo)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,
                            com.wasfa.doctor.ui.pres.PresDetailsFragment()
                        )
                        .addToBackStack(null)
                        .commit()
                }
                adapter = catAdapter
            }
        }
    }

    private fun manageCustomer(data: List<UserDetails>?) {


        if (data.isNullOrEmpty()){
            binding.cardCustBox.strokeColor = Color.parseColor("#A61C5C")
            binding.rltBtn.visibility = View.VISIBLE
            binding.cardCreate.visibility = View.VISIBLE
            binding.cardUpdate.visibility = View.GONE
            binding.cardNext.visibility = View.GONE
            binding.imgTick.visibility = View.GONE

            binding.editCivilId.setText(null)
            binding.editAltPhone.setText(null)
            binding.editFullName.setText(null)
            binding.editEmail.setText(null)
            binding.txtDob.setText(null)
            binding.txtGender.setText(null)
            binding.txtNationality.setText(null)
        }else{
            binding.imgTick.visibility = View.VISIBLE
            binding.cardCustBox.strokeColor = Color.parseColor("#00BCD7")
            patientId = data?.get(0)?.id.toString()
            callOrderListApi()
            binding.rltBtn.visibility = View.VISIBLE
            binding.cardCreate.visibility = View.GONE
            binding.cardUpdate.visibility = View.VISIBLE

            binding.editCivilId.setText(data.get(0)?.civilId)
            binding.editAltPhone.setText(data.get(0)?.alternateNumber)
            binding.editFullName.setText(data.get(0)?.name)
            binding.editEmail.setText(data.get(0)?.email)
            binding.txtDob.setText(data.get(0)?.dob)
            binding.txtGender.setText(data.get(0)?.gender)
            binding.txtNationality.setText(data.get(0)?.nationality)

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


    private fun genderList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Male",
                R.drawable.dummy_image
            ),
            Cat(
                "Female",
                R.drawable.dummy_image
            ),
            Cat(
                "Other",
                R.drawable.dummy_image
            )
        )
    }


    private fun showGenderDropdown(anchorView: View, skuList: MutableList<Cat>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowGender =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val edtSearch = popupView.findViewById<MaterialCardView>(R.id.card_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)

        edtSearch.visibility = View.GONE
        productStockAdapter = ProductStockAdapter { selectedSku ->
            // Handle selection
            binding.txtGender.text = selectedSku.name
            popupWindowGender?.dismiss()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = productStockAdapter
        productStockAdapter.submitList(skuList)

        popupWindowGender?.elevation = 10f
        popupWindowGender?.isOutsideTouchable = true
        popupWindowGender?.showAsDropDown(anchorView)
    }
    private fun showNationalityDropdown(anchorView: View, skuList: MutableList<CountryListResponse>) {
        val popupView =
            LayoutInflater.from(anchorView.context).inflate(R.layout.popup_cat_dropdown, null)
        popupWindowNationality =
            PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val edtSearch = popupView.findViewById<EditText>(R.id.edit_search)
        val rvSku = popupView.findViewById<RecyclerView>(R.id.rvSkuList)


        countryAdapter = CountryAdapter { selectedSku ->
            // Handle selection
            binding.txtNationality.text = selectedSku.name
            countryId = selectedSku.id
            popupWindowNationality?.dismiss()
        }

        rvSku.layoutManager = LinearLayoutManager(anchorView.context)
        rvSku.adapter = countryAdapter
        countryAdapter.submitList(skuList)

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                val filteredList = skuList.filter {
                    it.name.lowercase().contains(query)
                }
                countryAdapter.submitList(filteredList)
            }
        })

        popupWindowNationality?.elevation = 10f
        popupWindowNationality?.isOutsideTouchable = true
        popupWindowNationality?.showAsDropDown(anchorView)
    }


    private fun handleClick() {
        binding.imgMenu.setOnClickListener {
            (activity as? DoctorHomeActivity)?.toggleBottomNav()
        }
        binding.cardGender.setOnClickListener {
            showGenderDropdown(binding.cardGender, genderList())
        }
        binding.cardNationality.setOnClickListener {
            showNationalityDropdown(binding.cardNationality, countryList)
        }
        binding.cardNext.setOnClickListener {
            AppPreferences.getInstance(requireContext()).savePatientId(patientId)
            AppPreferences.getInstance(requireContext()).saveFavStatus("1")

//            if(AppPreferences.getInstance(requireContext()).getNewRXStatus() == "new"){
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, AddMedPOSNewFragment())
//                    .addToBackStack(null)
//                    .commit()
//            }else{
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddMedNewFragment())
                    .addToBackStack(null)
                    .commit()


        }
        binding.cardViewAllOrder.setOnClickListener {
            AppPreferences.getInstance(requireContext()).savePatientId(patientId)
            AppPreferences.getInstance(requireContext()).saveIsPres("true")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PrescriptionFragment())
                .addToBackStack(null)
                .commit()
        }


        binding.cardCreate.setOnClickListener {
            validateFields("create")
        }

        binding.cardUpdate.setOnClickListener {
            validateFields("update")
        }
        binding.lytCalendar.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
            binding.txtDob.text = formattedDate
            binding.txtDobEmpty.visibility = View.GONE
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun validateFields(type: String) {
        if (binding.editPhone.text.isEmpty()){
            binding.txtPhoneEmpty.visibility = View.VISIBLE
        }else if (binding.editFullName.text.isEmpty()){
            binding.txtNameEmpty.visibility = View.VISIBLE
        }else{

            if (type == "create"){
                callCreatePatientApi()
            }else{
                callUpdatePatientApi()
            }

        }
    }
    private fun callUpdatePatientApi() {

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.CreatePatientRequest(
            email = binding.editEmail.text.toString(),
            phone ="+965"+ binding.editPhone.text.toString(),
            name = binding.editFullName.text.toString(),
            dob = binding.txtDob.text.toString(),
            altMobileNo = binding.editAltPhone.text.toString(),
            civilId = binding.editCivilId.text.toString(),
            id = patientId,
            gender = binding.txtGender.text.toString(),
            nationality = countryId
        )
        viewModel.createPatient(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
    }
    private fun callCreatePatientApi() {

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.CreatePatientRequest(
            email = binding.editEmail.text.toString(),
            phone = "+965"+binding.editPhone.text.toString(),
            name = binding.editFullName.text.toString(),
            dob = binding.txtDob.text.toString(),
            altMobileNo = binding.editAltPhone.text.toString(),
            civilId = binding.editCivilId.text.toString(),
            id = "",
            gender = binding.txtGender.text.toString(),
            nationality = countryId
        )
        viewModel.createPatient(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
    }


}