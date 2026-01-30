package com.wasfa.doctor.ui.pres.add

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
import android.widget.ImageView
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
import com.wasfa.doctor.databinding.FragmentAddMedNewBinding
import com.wasfa.doctor.ui.pres.cart.adapter.CartRXAdapter
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CartItem
import com.wasfa.doctor.network.response.ProductListResponse
import com.wasfa.doctor.network.response.Products
import com.wasfa.doctor.ui.pres.adapter.POSRXListAdapter
import com.wasfa.doctor.ui.med.POSShopDetailsFragment
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class AddMedPOSNewFragment : Fragment() {
    private var _binding: FragmentAddMedNewBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: POSRXListAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var selectedInfluencerId: String = ""
    var searchValue: String = ""
    var patientId = ""
    private var isLoading = false
    private val visibleThreshold = 5
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddMedNewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        patientId = AppPreferences.getInstance(requireContext()).getPatientId().toString()
        handleClick()

        setUpRecyclerView()
        setViewModel()
        setUpPagination()
        callProductAPI("first")
        callCartApi()
        manageSearch()
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
    private fun callCartApi() {
        binding.progressBarCart.visibility = View.VISIBLE
        viewModel.getCart(AppPreferences.getInstance(requireContext()).getToken().toString())
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
                    viewModel.loadNextPage(
                        "", "", searchValue, selectedInfluencerId, "", "", AppPreferences.getInstance(requireContext()).getFavStatus().toString(), ""
                    )
                }
            }
        })
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
        viewModel.addUnitFailStatus.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.cartList.observe(viewLifecycleOwner) { data ->
            binding.progressBarCart.visibility = View.GONE
            binding.progressBar.visibility = View.GONE

            manageCart(data?.cartItems)
           // patientId = data?.patientInfo?.get(0)?.id.toString()


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

        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }


        viewModel.addCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
            viewModel.getCart(appPreferences.getToken().toString())

        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            isLoading = false
            binding.pageShimmer.shimmer.visibility = View.GONE
            binding.pageShimmer.shimmer.stopShimmer()

        }
        viewModel.productListData.observe(viewLifecycleOwner) { data ->

            binding.progressBar.visibility = View.GONE
            isLoading = false
            binding.progressBarSmall.visibility = View.GONE
            try {
                val totalPages = data?.totalPage
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

    }
    private fun manageCart(cartItems: List<CartItem>?) {

        if (cartItems.isNullOrEmpty()) {
            binding.txtNoDataCart.visibility = View.VISIBLE
            binding.cardProceed.visibility = View.GONE
            binding.recyclerCart.visibility = View.GONE
        } else {
            binding.txtNoDataCart.visibility = View.GONE
            binding.cardProceed.visibility = View.VISIBLE
            binding.recyclerCart.visibility = View.VISIBLE
            binding.recyclerCart.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                val catAdapter = CartRXAdapter(
                    cartItems,
                    { cart, count ->  //decrement
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = count,
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description.toString(),
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, count -> //increment
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = count,
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description.toString(),
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, id -> // delete
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.CartRemoveRequest(
                            id = id
                        )
                        viewModel.deleteCartShop(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, doseValue -> //dose

                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description ?: "",
                            course_duration = cart?.course_duration ?: "",
                            dose_day = doseValue
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, doseTime -> //dose time
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = doseTime,
                            course_day = cart?.course_day ?: "",
                            description = cart?.description ?: "",
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: ""
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, dayValue -> //dose day
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = dayValue,
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description ?: "",
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: ""
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, courseDurationValue -> //course duration
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description ?: "",
                            course_duration = courseDurationValue,
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, courseDayValue -> //course day
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = courseDayValue,
                            description = cart?.description ?: "",
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, notesValue -> //notes
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = notesValue,
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { Cat, position ->

                    },
                )
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


    private fun callProductAPI(status: String) {
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
        binding.cardProceed.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PresReviewFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.imgBack.setOnClickListener {
            val navController = runCatching { findNavController() }.getOrNull()
            if (navController?.currentDestination != null) {
                navController.popBackStack()
            } else {
                parentFragmentManager.popBackStack()
            }
        }

    }
}