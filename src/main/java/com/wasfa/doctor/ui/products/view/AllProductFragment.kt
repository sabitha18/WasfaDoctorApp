package com.wasfa.doctor.ui.products.view

import android.content.Intent
import android.content.res.Configuration
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
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentAllProductBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.ProductListResponse
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.products.adapter.AllProductAdapter
import com.wasfa.doctor.ui.products.add.AddProductActivity
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class AllProductFragment : Fragment() {

    private var _binding: FragmentAllProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: AllProductAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    var searchValue: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        setViewModel()
        setUpPagination()
        callProductAPI()
        manageSearch()

        handleClick()

        if (AppPreferences.getInstance(requireContext()).getLoginType().toString() == "staff") {
            manageStaffViews()
        }

        binding.txtHeader.text = AppPreferences.getInstance(requireContext()).getPDType().toString()
    }
    private fun manageStaffViews() = with(binding) {
        cardAddProduct.setVisibleIfPermission(PermissionKeys.ADD_NEW_PRODUCT)
    }
    private fun View.setVisibleIfPermission(permissionKey: String) {
        visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE

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

    override fun onDestroyView() {
        // Safely check if _binding is not null before accessing it
        _binding?.let { binding ->
            scrollListener?.let { listener ->
                binding.nestedScrollView.viewTreeObserver.removeOnScrollChangedListener(listener)
            }
        }
        scrollListener = null
        _binding = null

        super.onDestroyView()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }

    private fun setUpRecyclerView() {
        productAdapter = AllProductAdapter(
            mutableListOf()
        ) { product, position ->

            if (position == "view") {
                AppPreferences.getInstance(requireContext()).saveProductId(product?.id.toString())
                findNavController().navigate(R.id.nav_pos_shop_details)
            } else if (position == "1") {
                binding.progressBar.visibility = View.VISIBLE
                val request = ApiService.UpdatePSRequest(
                    productId = product?.id.toString(),
                    statusType = "published",
                    status = position
                )
                viewModel.updateProductStatus(
                    AppPreferences.getInstance(requireContext()).getToken().toString(), request
                )
            } else if (position == "0") {
                binding.progressBar.visibility = View.VISIBLE
                val request = ApiService.UpdatePSRequest(
                    productId = product?.id.toString(),
                    statusType = "published",
                    status = position
                )
                viewModel.updateProductStatus(
                    AppPreferences.getInstance(requireContext()).getToken().toString(), request
                )
            } else if (position == "move") {
                Toast.makeText(
                    requireContext(),
                    "Product Moved Successfully",
                    Toast.LENGTH_LONG
                ).show()
            } else if (position == "apix_margin") {
                AppPreferences.getInstance(requireContext()).saveSellerId(product?.seller)
                findNavController().navigate(R.id.nav_seller_apix)
            } else if (position == "delete") {
                showDeletePopup()
            } else if (position == "influ_margin") {
                AppPreferences.getInstance(requireContext()).saveSellerId(product?.seller)
                findNavController().navigate(R.id.nav_influencer)
            } else if (position == "edit") {
                AppPreferences.getInstance(requireContext()).clearAddProduct()
                AppPreferences.getInstance(requireContext()).saveAddOrEditStatus("Edit Product")
                AppPreferences.getInstance(requireContext()).saveProductId(product?.id.toString())
                val intent = Intent(requireContext(), AddProductActivity::class.java)
                startActivity(intent)
            }

        }

        binding.recyclerProduct.apply {
            if (isTablet()){
                layoutManager = GridLayoutManager(context, 3)
            }else{
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            }

            adapter = productAdapter
        }
    }
    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    private fun callProductAPI() {

        binding.recyclerProduct.visibility = View.GONE
        binding.txtNoData.visibility = View.GONE

        viewModel.currentPage = 1
        viewModel.clearProductListData()
        val appPreferences = AppPreferences.getInstance(requireContext())
        val request = ApiService.ProductRequest(
            page_no = "1",
            per_page = "6",
            category = "",
            brand = "",
            sku = "",
            seller = "",
            medical_rep_id = "",
            keyword = searchValue,
            influencer_id = "",
            isFavourite = "",
            listFrom = ""
        )
        binding.progressBar.visibility = View.VISIBLE

        viewModel.getProductList(appPreferences.getToken().toString(), request)
    }

    private fun setUpPagination() {
        binding.recyclerProduct.isNestedScrollingEnabled = false
        scrollListener = ViewTreeObserver.OnScrollChangedListener {
            if (_binding == null) return@OnScrollChangedListener

            val view =
                binding.nestedScrollView.getChildAt(binding.nestedScrollView.childCount - 1) as View
            val diff: Int =
                view.bottom - (binding.nestedScrollView.height + binding.nestedScrollView.scrollY)
            if (diff == 0) {
                if (viewModel.isLastPage()) {
                    // No action for the last page
                } else {
                    viewModel.currentPage++
                    viewModel.loadNextPage("", "", searchValue, "", "", "", "", "")
                    binding.progressBarSmall.visibility = View.VISIBLE
                }
            }
        }

        binding.nestedScrollView.viewTreeObserver.addOnScrollChangedListener(scrollListener)

        // Clean up the listener when the view is destroyed
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                binding.nestedScrollView.viewTreeObserver.removeOnScrollChangedListener(
                    scrollListener
                )
            }
        })
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
        viewModel.status.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.favStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.addCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            viewModel.getCartCount(appPreferences.getToken().toString())

        }
        viewModel.productEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.productListData.observe(viewLifecycleOwner) { data ->


            binding.progressBarSmall.visibility = View.GONE
            if (data?.totalProductsCount == "0") {

                if (binding.progressBar.visibility == View.GONE) {
                    binding.txtNoData.visibility = View.VISIBLE
                }
            } else {
                binding.txtNoData.visibility = View.GONE
            }


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

                manageProduct(data)
            } else {
                productAdapter.addProducts(data?.products!!)
            }

        }

        viewModel.cartCount.observe(viewLifecycleOwner) { data ->


            AppPreferences.getInstance(requireContext()).saveCartCount(data?.cartCount)


        }

        viewModel.getCartCount(appPreferences.getToken().toString())

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
            if (message == "Internal Server Error") {
                findNavController().popBackStack()
                dialog?.dismiss()
            } else {
                dialog?.dismiss()
            }

        }

        dialog = builder.create()
        dialog.show()
    }

    private fun handleClick() {
        binding.cardAddProduct.setOnClickListener {
            AppPreferences.getInstance(requireContext()).saveAddOrEditStatus("Add New Product")
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun manageProduct(data: ProductListResponse?) {
        binding.recyclerProduct.visibility = View.VISIBLE
        productAdapter.setProducts(data?.products?.toMutableList() ?: mutableListOf())
    }

    private fun showDeletePopup() {
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
            dialog?.dismiss()

        }
        dialog = builder.create()
        dialog.show()
    }

    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Add New product",
                R.drawable.dummy_image
            ),
            Cat(
                "Product Update",
                R.drawable.dummy_image
            ),
            Cat(
                "Product Cleaning",
                R.drawable.dummy_image
            ),
            Cat(
                "All Products",
                R.drawable.dummy_image
            ),
            Cat(
                "In House Products",
                R.drawable.dummy_image
            ),
            Cat(
                "Seller Products",
                R.drawable.dummy_image
            ),
            Cat(
                "Bulk Import",
                R.drawable.dummy_image
            )
        )
    }
}