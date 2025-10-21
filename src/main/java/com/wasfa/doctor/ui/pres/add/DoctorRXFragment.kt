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
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
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
import com.wasfa.doctor.databinding.FragmentDoctorRXBinding
import com.wasfa.doctor.ui.pres.cart.DoctorRXCartFragment
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CountryListResponse
import com.wasfa.doctor.network.response.ProductListResponse
import com.wasfa.doctor.network.response.Products
import com.wasfa.doctor.network.response.UserDetails
import com.wasfa.doctor.ui.model.Cat
import com.wasfa.doctor.ui.pres.adapter.CountryAdapter
import com.wasfa.doctor.ui.pres.adapter.POSRXListAdapter
import com.wasfa.doctor.ui.med.POSShopDetailsFragment
import com.wasfa.doctor.ui.pres.adapter.ProductStockAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.util.Calendar

class DoctorRXFragment : Fragment() {
    private var _binding: FragmentDoctorRXBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: POSRXListAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var selectedInfluencerId: String = ""
    private var popupWindowGender: PopupWindow? = null
    private var popupWindowNationality: PopupWindow? = null
    private val countryList = mutableListOf<CountryListResponse>()
    private lateinit var productStockAdapter: ProductStockAdapter
    private lateinit var countryAdapter: CountryAdapter
    var searchValue: String = ""
    var countryId: String = ""
    var patientId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoctorRXBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()

        setUpRecyclerView()
        setViewModel()
        setUpPagination()
        AppPreferences.getInstance(requireContext()).saveFavStatus("1")
        callProductAPI("first")
        manageSearch()


        handleEditTexts()
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
        viewModel.emptyCart(appPreferences.getToken().toString())
        viewModel.customerListData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE

            manageCustomer(data?.users)

        }
    }

    private fun setUpPagination() {
        binding.recyclerProduct.isNestedScrollingEnabled = false

        binding.nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                val lastChild = v.getChildAt(v.childCount - 1)
                val diff = lastChild.bottom - (v.height + scrollY)

                if (diff <= 200 && scrollY > oldScrollY) { // Scroll reached bottom (with 200px buffer)
                    if (!viewModel.isLastPage()) {
                        viewModel.currentPage++

                        viewModel.loadNextPage(
                            "",
                            "",
                            searchValue,
                            selectedInfluencerId,
                            "",
                            "",
                            AppPreferences.getInstance(requireContext()).getFavStatus().toString(),
                            ""
                        )
                        binding.progressBarSmall.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    private fun setUpRecyclerView() {
        productAdapter = POSRXListAdapter(
            mutableListOf(),
            "doctor"
        ) { product, type ->

            if (type == "add"){

                if (patientId == ""){
                    showAlertCustom("Please Add Patient")
                }else{
                    binding.progressBar.visibility = View.VISIBLE
                    val request = ApiService.AddCartRXRequest(
                        productId = product?.id.toString(),
                        quantity = "1",
                        patientId = patientId,
                        dose_day = "",
                        description = "",
                        dose = "",
                        course_day = "",
                        dose_time = "",
                        course_duration = "",
                        is_edit = "",
                        prescriptionId = ""
                    )
                    viewModel.addCartRX(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
                }

            }else if(type == "info"){
                showPickUpDialog(product)
            }else if(type == "view"){
                showPosViewDialog(product)
            }else{
                AppPreferences.getInstance(requireContext()).saveProductId(product?.id.toString())
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, POSShopDetailsFragment())
                    .addToBackStack(null)
                    .commit()
            }

        }

        binding.recyclerProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
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
                binding.rltBtn.visibility = View.GONE
            }else{
                binding.rltBtn.visibility = View.VISIBLE
            }
            Toast.makeText(requireContext(),message, Toast.LENGTH_LONG).show()
            //56567876

        }
        viewModel.patientIdStatus.observe(viewLifecycleOwner) { patient_id ->
            binding.progressBar.visibility = View.GONE
            println("---------------   "+patient_id)
            patientId = patient_id

        }
        viewModel.emptyCartStatus.observe(viewLifecycleOwner) { patient_id ->
            viewModel.getCartCount(appPreferences.getToken().toString())

        }
        viewModel.addCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            viewModel.getCartCount(appPreferences.getToken().toString())

        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

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


        viewModel.cartCount.observe(viewLifecycleOwner) { data ->

            binding.txtCartCount.text = data?.rxCount

            if (data?.rxCount == "0"){
                binding.rltCartCount.visibility = View.INVISIBLE
            }else{
                binding.rltCartCount.visibility = View.VISIBLE
            }
        }

        viewModel.countryList.observe(viewLifecycleOwner) { data ->

            countryList.addAll(data)
        }


        viewModel.getCartCount(appPreferences.getToken().toString())
        viewModel.getCountryList(appPreferences.getToken().toString())

    }

    private fun manageCustomer(data: List<UserDetails>?) {


        if (data.isNullOrEmpty()){
            binding.cardCustBox.strokeColor = Color.parseColor("#A61C5C")
            binding.rltBtn.visibility = View.VISIBLE
            binding.cardCreate.visibility = View.VISIBLE
            binding.cardUpdate.visibility = View.GONE

            binding.editCivilId.setText(null)
            binding.editAltPhone.setText(null)
            binding.editFullName.setText(null)
            binding.editEmail.text = null
            binding.txtDob.text = null
            binding.txtGender.text = null
            binding.txtNationality.text = null

            binding.imgTick.visibility = View.GONE
        }else{
            binding.cardCustBox.strokeColor = Color.parseColor("#00BCD7")
            patientId = data?.get(0)?.id.toString()
            binding.rltBtn.visibility = View.VISIBLE
            binding.cardCreate.visibility = View.GONE
            binding.cardUpdate.visibility = View.VISIBLE

            binding.editCivilId.setText(data.get(0)?.civilId)
            binding.editAltPhone.setText(data.get(0)?.alternateNumber)
            binding.editFullName.setText(data.get(0)?.name)
            binding.editEmail.setText(data.get(0)?.email)
            binding.txtDob.text = data.get(0)?.dob
            binding.txtGender.text = data.get(0)?.gender
            binding.txtNationality.text = data.get(0)?.nationality

            binding.imgTick.visibility = View.VISIBLE

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

        binding.recyclerProduct.visibility = View.VISIBLE
        productAdapter.setProducts(data?.products?.toMutableList() ?: mutableListOf())
    }


    private fun callProductAPI(status: String) {

//        if (status == "first"){
//            viewModel.emptyCart(AppPreferences.getInstance(requireContext()).getToken().toString())
//        }

        binding.pageShimmer.shimmer.visibility = View.VISIBLE
        binding.pageShimmer.shimmer.startShimmer()

        binding.recyclerProduct.visibility = View.GONE
        binding.txtNoData.visibility = View.GONE

        viewModel.currentPage = 1
        viewModel.clearProductListData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.ProductRequest(
            page_no = "1",
            per_page = "4",
            category = "",
            brand = "",
            sku = "",
            seller = "",
            medical_rep_id = "",
            keyword = searchValue,
            influencer_id = "",
            isFavourite = appPreferences.getFavStatus().toString(),
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
                    callProductAPI("")
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })
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
        binding.cardMyList.setOnClickListener {
            binding.cardMyList.setCardBackgroundColor(Color.parseColor("#A61C5C"))
            binding.cardAllProducts.setCardBackgroundColor(Color.parseColor("#FFFFFF"))

            binding.txtMyList.setTextColor((Color.parseColor("#FFFFFF")))
            binding.txtAllProducts.setTextColor((Color.parseColor("#A61C5C")))
            binding.progressBarSmall.visibility = View.GONE
            AppPreferences.getInstance(requireContext()).saveFavStatus("1")
            callProductAPI("POS")
        }

        binding.cardAllProducts.setOnClickListener {
            binding.cardMyList.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.cardAllProducts.setCardBackgroundColor(Color.parseColor("#A61C5C"))

            binding.txtMyList.setTextColor((Color.parseColor("#A61C5C")))
            binding.txtAllProducts.setTextColor((Color.parseColor("#FFFFFF")))
            binding.progressBarSmall.visibility = View.GONE
            AppPreferences.getInstance(requireContext()).saveFavStatus("0")
            callProductAPI("")
        }
        binding.cardGender.setOnClickListener {
            showGenderDropdown(binding.cardGender, genderList())
        }
        binding.cardNationality.setOnClickListener {
            showNationalityDropdown(binding.cardNationality, countryList)
        }
        binding.imgBack.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }


        binding.imgCart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DoctorRXCartFragment())
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