package com.wasfa.doctor.ui.pres.add

import android.app.DatePickerDialog
import android.content.res.Configuration
import android.graphics.Color
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
import com.wasfa.doctor.databinding.FragmentAddPresBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CartItem
import com.wasfa.doctor.network.response.CountryListResponse
import com.wasfa.doctor.network.response.CustomerListResponse
import com.wasfa.doctor.network.response.PatientInfo
import com.wasfa.doctor.ui.main.DoctorHomeActivity
import com.wasfa.doctor.ui.pres.adapter.MedicationListAdapter
import com.wasfa.doctor.ui.model.Cat
import com.wasfa.doctor.ui.pres.adapter.CountryAdapter
import com.wasfa.doctor.ui.pres.adapter.ProductStockAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.util.Calendar


class AddPresFragment : Fragment() {
    private var _binding: FragmentAddPresBinding? = null
    private val binding get() = _binding!!
    var patientId = ""
    private lateinit var viewModel: HomeViewModel
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
        _binding = FragmentAddPresBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageClick()

        handleEditTexts()
        setViewModel()
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
    private fun nationalityList(): ArrayList<Cat> {
        return arrayListOf(
            Cat("Afghan", R.drawable.dummy_image),
            Cat("Albanian", R.drawable.dummy_image),
            Cat("Algerian", R.drawable.dummy_image),
            Cat("American", R.drawable.dummy_image),
            Cat("Andorran", R.drawable.dummy_image),
            Cat("Angolan", R.drawable.dummy_image),
            Cat("Argentine", R.drawable.dummy_image),
            Cat("Armenian", R.drawable.dummy_image),
            Cat("Australian", R.drawable.dummy_image),
            Cat("Austrian", R.drawable.dummy_image),
            Cat("Azerbaijani", R.drawable.dummy_image),
            Cat("Bahamian", R.drawable.dummy_image),
            Cat("Bahraini", R.drawable.dummy_image),
            Cat("Bangladeshi", R.drawable.dummy_image),
            Cat("Barbadian", R.drawable.dummy_image),
            Cat("Belarusian", R.drawable.dummy_image),
            Cat("Belgian", R.drawable.dummy_image),
            Cat("Belizean", R.drawable.dummy_image),
            Cat("Beninese", R.drawable.dummy_image),
            Cat("Bhutanese", R.drawable.dummy_image),
            Cat("Bolivian", R.drawable.dummy_image),
            Cat("Bosnian", R.drawable.dummy_image),
            Cat("Botswanan", R.drawable.dummy_image),
            Cat("Brazilian", R.drawable.dummy_image),
            Cat("British", R.drawable.dummy_image),
            Cat("Bruneian", R.drawable.dummy_image),
            Cat("Bulgarian", R.drawable.dummy_image),
            Cat("Burkinabe", R.drawable.dummy_image),
            Cat("Burmese", R.drawable.dummy_image),
            Cat("Burundian", R.drawable.dummy_image),
            Cat("Cambodian", R.drawable.dummy_image),
            Cat("Cameroonian", R.drawable.dummy_image),
            Cat("Canadian", R.drawable.dummy_image),
            Cat("Cape Verdean", R.drawable.dummy_image),
            Cat("Central African", R.drawable.dummy_image),
            Cat("Chadian", R.drawable.dummy_image),
            Cat("Chilean", R.drawable.dummy_image),
            Cat("Chinese", R.drawable.dummy_image),
            Cat("Colombian", R.drawable.dummy_image),
            Cat("Comoran", R.drawable.dummy_image),
            Cat("Congolese", R.drawable.dummy_image),
            Cat("Costa Rican", R.drawable.dummy_image),
            Cat("Croatian", R.drawable.dummy_image),
            Cat("Cuban", R.drawable.dummy_image),
            Cat("Cypriot", R.drawable.dummy_image),
            Cat("Czech", R.drawable.dummy_image),
            Cat("Danish", R.drawable.dummy_image),
            Cat("Djiboutian", R.drawable.dummy_image),
            Cat("Dominican", R.drawable.dummy_image),
            Cat("Dutch", R.drawable.dummy_image),
            Cat("East Timorese", R.drawable.dummy_image),
            Cat("Ecuadorean", R.drawable.dummy_image),
            Cat("Egyptian", R.drawable.dummy_image),
            Cat("Emirati", R.drawable.dummy_image),
            Cat("English", R.drawable.dummy_image),
            Cat("Equatorial Guinean", R.drawable.dummy_image),
            Cat("Eritrean", R.drawable.dummy_image),
            Cat("Estonian", R.drawable.dummy_image),
            Cat("Ethiopian", R.drawable.dummy_image),
            Cat("Fijian", R.drawable.dummy_image),
            Cat("Finnish", R.drawable.dummy_image),
            Cat("French", R.drawable.dummy_image),
            Cat("Gabonese", R.drawable.dummy_image),
            Cat("Gambian", R.drawable.dummy_image),
            Cat("Georgian", R.drawable.dummy_image),
            Cat("German", R.drawable.dummy_image),
            Cat("Ghanaian", R.drawable.dummy_image),
            Cat("Greek", R.drawable.dummy_image),
            Cat("Grenadian", R.drawable.dummy_image),
            Cat("Guatemalan", R.drawable.dummy_image),
            Cat("Guinean", R.drawable.dummy_image),
            Cat("Guyanese", R.drawable.dummy_image),
            Cat("Haitian", R.drawable.dummy_image),
            Cat("Honduran", R.drawable.dummy_image),
            Cat("Hungarian", R.drawable.dummy_image),
            Cat("Icelandic", R.drawable.dummy_image),
            Cat("Indian", R.drawable.dummy_image),
            Cat("Indonesian", R.drawable.dummy_image),
            Cat("Iranian", R.drawable.dummy_image),
            Cat("Iraqi", R.drawable.dummy_image),
            Cat("Irish", R.drawable.dummy_image),
            Cat("Israeli", R.drawable.dummy_image),
            Cat("Italian", R.drawable.dummy_image),
            Cat("Ivorian", R.drawable.dummy_image),
            Cat("Jamaican", R.drawable.dummy_image),
            Cat("Japanese", R.drawable.dummy_image),
            Cat("Jordanian", R.drawable.dummy_image),
            Cat("Kazakh", R.drawable.dummy_image),
            Cat("Kenyan", R.drawable.dummy_image),
            Cat("Kuwaiti", R.drawable.dummy_image),
            Cat("Kyrgyz", R.drawable.dummy_image),
            Cat("Lao", R.drawable.dummy_image),
            Cat("Latvian", R.drawable.dummy_image),
            Cat("Lebanese", R.drawable.dummy_image),
            Cat("Liberian", R.drawable.dummy_image),
            Cat("Libyan", R.drawable.dummy_image),
            Cat("Lithuanian", R.drawable.dummy_image),
            Cat("Luxembourgish", R.drawable.dummy_image),
            Cat("Macedonian", R.drawable.dummy_image),
            Cat("Malagasy", R.drawable.dummy_image),
            Cat("Malawian", R.drawable.dummy_image),
            Cat("Malaysian", R.drawable.dummy_image),
            Cat("Maldivian", R.drawable.dummy_image),
            Cat("Malian", R.drawable.dummy_image),
            Cat("Maltese", R.drawable.dummy_image),
            Cat("Mexican", R.drawable.dummy_image),
            Cat("Moldovan", R.drawable.dummy_image),
            Cat("Monacan", R.drawable.dummy_image),
            Cat("Mongolian", R.drawable.dummy_image),
            Cat("Montenegrin", R.drawable.dummy_image),
            Cat("Moroccan", R.drawable.dummy_image),
            Cat("Mozambican", R.drawable.dummy_image),
            Cat("Namibian", R.drawable.dummy_image),
            Cat("Nepalese", R.drawable.dummy_image),
            Cat("New Zealander", R.drawable.dummy_image),
            Cat("Nicaraguan", R.drawable.dummy_image),
            Cat("Nigerian", R.drawable.dummy_image),
            Cat("North Korean", R.drawable.dummy_image),
            Cat("Norwegian", R.drawable.dummy_image),
            Cat("Omani", R.drawable.dummy_image),
            Cat("Pakistani", R.drawable.dummy_image),
            Cat("Palestinian", R.drawable.dummy_image),
            Cat("Panamanian", R.drawable.dummy_image),
            Cat("Paraguayan", R.drawable.dummy_image),
            Cat("Peruvian", R.drawable.dummy_image),
            Cat("Philippine", R.drawable.dummy_image),
            Cat("Polish", R.drawable.dummy_image),
            Cat("Portuguese", R.drawable.dummy_image),
            Cat("Qatari", R.drawable.dummy_image),
            Cat("Romanian", R.drawable.dummy_image),
            Cat("Russian", R.drawable.dummy_image),
            Cat("Rwandan", R.drawable.dummy_image),
            Cat("Saudi", R.drawable.dummy_image),
            Cat("Scottish", R.drawable.dummy_image),
            Cat("Senegalese", R.drawable.dummy_image),
            Cat("Serbian", R.drawable.dummy_image),
            Cat("Seychellois", R.drawable.dummy_image),
            Cat("Singaporean", R.drawable.dummy_image),
            Cat("Slovak", R.drawable.dummy_image),
            Cat("Slovenian", R.drawable.dummy_image),
            Cat("Somali", R.drawable.dummy_image),
            Cat("South African", R.drawable.dummy_image),
            Cat("South Korean", R.drawable.dummy_image),
            Cat("Spanish", R.drawable.dummy_image),
            Cat("Sri Lankan", R.drawable.dummy_image),
            Cat("Sudanese", R.drawable.dummy_image),
            Cat("Swedish", R.drawable.dummy_image),
            Cat("Swiss", R.drawable.dummy_image),
            Cat("Syrian", R.drawable.dummy_image),
            Cat("Taiwanese", R.drawable.dummy_image),
            Cat("Tajik", R.drawable.dummy_image),
            Cat("Tanzanian", R.drawable.dummy_image),
            Cat("Thai", R.drawable.dummy_image),
            Cat("Togolese", R.drawable.dummy_image),
            Cat("Tunisian", R.drawable.dummy_image),
            Cat("Turkish", R.drawable.dummy_image),
            Cat("Ugandan", R.drawable.dummy_image),
            Cat("Ukrainian", R.drawable.dummy_image),
            Cat("Uruguayan", R.drawable.dummy_image),
            Cat("Uzbek", R.drawable.dummy_image),
            Cat("Venezuelan", R.drawable.dummy_image),
            Cat("Vietnamese", R.drawable.dummy_image),
            Cat("Welsh", R.drawable.dummy_image),
            Cat("Yemeni", R.drawable.dummy_image),
            Cat("Zambian", R.drawable.dummy_image),
            Cat("Zimbabwean", R.drawable.dummy_image)
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
    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    private fun setViewModel() {
        if (!isTablet()){
            (activity as? DoctorHomeActivity)?.showBottomNav()
        }else{
            (activity as? DoctorHomeActivity)?.hideBottomNav()
        }
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
        viewModel.createdStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            if (message == "Patient saved successfully"){
                binding.rltBtn.visibility = View.GONE
            }else{
                binding.rltBtn.visibility = View.VISIBLE
            }
            Toast.makeText(requireContext(),message, Toast.LENGTH_LONG).show()
            //56567876

        }
        viewModel.deleteCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.VISIBLE
            println("me =--------------")
            viewModel.getCart(appPreferences.getToken().toString())

        }
        viewModel.patientIdStatus.observe(viewLifecycleOwner) { patient_id ->
            binding.progressBar.visibility = View.GONE
            patientId = patient_id

        }
        viewModel.addCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message, Toast.LENGTH_LONG).show()
            viewModel.getCartCount(appPreferences.getToken().toString())

        }


        viewModel.cartList.observe(viewLifecycleOwner) { data ->
            println("--------------------------")
            binding.progressBar.visibility = View.GONE

            manageCart(data?.cartItems)
           managePatient(data?.patientInfo)

        }
        viewModel.countryList.observe(viewLifecycleOwner) { data ->

            countryList.addAll(data)
        }
        viewModel.getCountryList(appPreferences.getToken().toString())
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getCart(appPreferences.getToken().toString())
    }
    private fun manageCart(cartItems: List<CartItem>?) {

        if (cartItems.isNullOrEmpty()) {
            binding.cardCreate.visibility = View.GONE
            binding.recyclerMed.visibility = View.GONE
        } else {
            binding.recyclerMed.visibility = View.VISIBLE
            binding.cardCreate.visibility = View.VISIBLE
            binding.txtNoData.visibility = View.GONE
            binding.recyclerMed.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                val catAdapter = MedicationListAdapter(cartItems) { data, type ->

                    binding.progressBar.visibility = View.VISIBLE
                    val request = ApiService.CartRemoveRequest(
                        id = data?.id.toString()
                    )
                    viewModel.deleteCartShop(
                        AppPreferences.getInstance(requireContext()).getToken().toString(),
                        request
                    )
                }
                adapter = catAdapter
            }
        }

    }
    private fun managePatient(data: List<PatientInfo>?) {
        if (data.isNullOrEmpty()){
            binding.editCivilId.setText(null)
            binding.editPatientName.setText(null)
            binding.editPhone.setText(null)
            binding.editEmail.setText(null)
            binding.txtDate.setText(null)
            binding.txtGender.setText(null)
            binding.txtNationality.setText(null)
        }else{

            patientId = data?.get(0)?.id.toString()
            binding.rltBtn.visibility = View.VISIBLE
            binding.cardCreatePatiant.visibility = View.GONE
            binding.cardUpdate.visibility = View.VISIBLE

            binding.editCivilId.setText(data.get(0)?.civil_id)
            binding.editPatientName.setText(data.get(0)?.name)
            binding.editEmail.setText(data.get(0)?.email)
            binding.txtDate.text = data.get(0)?.dob
            binding.editPhone.setText(data.get(0)?.phone)
            binding.txtGender.text = data.get(0)?.gender
            binding.txtNationality.text = data.get(0)?.nationality

        }
    }
    private fun manageCustomer(data: CustomerListResponse?) {


        if (data?.users.isNullOrEmpty()){
            binding.cardCustBox.strokeColor = Color.parseColor("#80A61C5C")
            binding.rltBtn.visibility = View.VISIBLE
            binding.cardCreatePatiant.visibility = View.VISIBLE
            binding.cardUpdate.visibility = View.GONE
        }else{
            binding.cardCustBox.strokeColor = Color.parseColor("#8000BCD7")
            patientId = data?.users?.get(0)?.id.toString()
            binding.rltBtn.visibility = View.VISIBLE
            binding.cardCreatePatiant.visibility = View.GONE
            binding.cardUpdate.visibility = View.VISIBLE

            binding.editCivilId.setText(data?.users?.get(0)?.civilId)
            binding.editPatientName.setText(data?.users?.get(0)?.name)
            binding.editEmail.setText(data?.users?.get(0)?.email)
            binding.txtDate.setText(data?.users?.get(0)?.dob)
            binding.txtGender.setText(data?.users?.get(0)?.gender)
            binding.txtNationality.setText(data?.users?.get(0)?.nationality)

        }
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
        binding.editPatientName.addTextChangedListener(object : TextWatcher {
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

            manageCustomer(data)
        }


    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
            binding.txtDate.text = formattedDate
            binding.txtDobEmpty.visibility = View.GONE
        }, year, month, day)

        datePickerDialog.show()
    }
    private fun manageClick() {
        binding.cardGender.setOnClickListener {
            showGenderDropdown(binding.cardGender, genderList())
        }
        binding.cardNationality.setOnClickListener {
            showNationalityDropdown(binding.cardNationality, countryList)
        }
        binding.imgMenu.setOnClickListener {
            (activity as? DoctorHomeActivity)?.toggleBottomNav()
        }
        binding.txtCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.cardCalendar.setOnClickListener {
            showDatePicker()
        }
        binding.cardBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.cardCreate.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PresReviewFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.cardAddNewMed.setOnClickListener {
            if (patientId == ""){
                showAlertCustom("Please Add Patient")
            }else{
                AppPreferences.getInstance(requireContext()).savePatientId(patientId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddMedFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        binding.cardCreatePatiant.setOnClickListener {
            validateFields("create")
        }

        binding.cardUpdate.setOnClickListener {
            validateFields("update")
        }
    }
    private fun validateFields(type: String) {
        if (binding.editPhone.text.isEmpty()){
            binding.txtPhoneEmpty.visibility = View.VISIBLE
        }else if (binding.editPatientName.text.isEmpty()){
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
            name = binding.editPatientName.text.toString(),
            dob = binding.txtDate.text.toString(),
            altMobileNo = "",
            civilId = binding.editCivilId.text.toString(),
            id = patientId,
            gender = binding.txtGender.text.toString(),
            nationality = binding.txtNationality.text.toString()
        )
        viewModel.createPatient(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
    }
    private fun callCreatePatientApi() {

        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.CreatePatientRequest(
            email = binding.editEmail.text.toString(),
            phone = "+965"+binding.editPhone.text.toString(),
            name = binding.editPatientName.text.toString(),
            dob = binding.txtDate.text.toString(),
            altMobileNo ="",
            civilId = binding.editCivilId.text.toString(),
            id = "",
            gender = binding.txtGender.text.toString(),
            nationality = binding.txtNationality.text.toString()
        )
        viewModel.createPatient(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
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

}