package com.wasfa.doctor.ui.products.brand

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentBrandBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.BrandListResponse
import com.wasfa.doctor.ui.products.brand.adapter.BrandListAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class BrandActivity : AppCompatActivity() {
    private lateinit var binding: FragmentBrandBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var brandAdapter: BrandListAdapter
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        binding = FragmentBrandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()
        handleClick()
        setViewModel()
        callBrandApi()
        setUpPagination()
        if (AppPreferences.getInstance(this@BrandActivity).getLoginType().toString() == "staff") {
            manageStaffViews()
        }
    }
    private fun manageStaffViews() = with(binding) {
        cardAdd.setVisibleIfPermission(PermissionKeys.ADD_BRAND)

    }

    private fun View.setVisibleIfPermission(permissionKey: String) {
        visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE

    }
    private fun callBrandApi() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.currentPageBrand= 1
        viewModel.clearBrandData()
        val request = ApiService.BrandRequest(
            page_no = "1",
            per_page = "4"
        )
        viewModel.getBrandList(AppPreferences.getInstance(this).getToken().toString(),request)
    }
    private fun setUpPagination() {
        binding.recyclerBrand.isNestedScrollingEnabled = false
        scrollListener = ViewTreeObserver.OnScrollChangedListener {

            val view =
                binding.nestedScrollView.getChildAt(binding.nestedScrollView.childCount - 1) as View
            val diff: Int =
                view.bottom - (binding.nestedScrollView.height + binding.nestedScrollView.scrollY)
            if (diff == 0) {
                if (viewModel.isLastPageBrand()) {
                    // No action for the last page
                } else {
                    viewModel.currentPageBrand++
                    viewModel.loadNextPageBrand()

                    if (binding.progressBar.visibility == View.VISIBLE){
                        binding.progressBarSmall.visibility = View.GONE
                    }else{
                        binding.progressBarSmall.visibility = View.VISIBLE
                    }

                }
            }
        }

        binding.nestedScrollView.viewTreeObserver.addOnScrollChangedListener(scrollListener)


    }
    private fun setUpRecyclerView() {
        brandAdapter = BrandListAdapter(
            mutableListOf()
        ) { product, type ->

            when (type) {
                "edit" -> {
                    AppPreferences.getInstance(this@BrandActivity).saveCategoryId(product?.id)
                    val intent = Intent(this@BrandActivity, EditBrandActivity::class.java)
                    startActivity(intent)
                }
                "delete" -> showDeletePopup(product?.id.toString())
            }
        }

        binding.recyclerBrand.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = brandAdapter
        }
    }
    private fun handleClick() {

        binding.cardAdd.setOnClickListener {
            val intent = Intent(this, AddBrandActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun setViewModel() {
        val appPreferences = AppPreferences.getInstance(this)
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(this)
        ).get(HomeViewModel::class.java)

        viewModel.loadingState.observe(this) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }
        viewModel.showAlertEvent.observe(this) { message ->
            binding.progressBar.visibility = View.GONE


        }
        viewModel.productEvent.observe(this) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.deleteBrandStatus.observe(this) { message ->
            callBrandApi()
        }
        viewModel.deleteBrandFailStatus.observe(this) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.brandList.observe(this) { data ->
            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountBrand = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPageBrand == 1) {

                manageBrand(data)
            } else {
                brandAdapter.addProducts(data?.brands!!)
            }

        }
    }
    fun showAlertCustom(message: String) {
        val builder = AlertDialog.Builder(this)


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
    private fun manageBrand(data: BrandListResponse) {
        binding.recyclerBrand.visibility = View.VISIBLE
        brandAdapter.setProducts(data?.brands!!)
    }
    private fun showDeletePopup(id: String) {
        val builder = AlertDialog.Builder(this@BrandActivity)
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
            binding.progressBar.visibility = View.VISIBLE
            val request= ApiService.CatDetailsRequest(
                id = id
            )
            viewModel.deleteBrand(AppPreferences.getInstance(this@BrandActivity).getToken().toString(),request)

        }
        dialog = builder.create()
        dialog.show()
    }
}