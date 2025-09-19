package com.wasfa.doctor.ui.products.category

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
import com.wasfa.doctor.databinding.FragmentCategoryBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CatList
import com.wasfa.doctor.network.response.CatListResponse
import com.wasfa.doctor.ui.products.category.adapter.CategoryListAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var catAdapter: CategoryListAdapter
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

        binding = FragmentCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()
        handleClick()
        setViewModel()
        callCatApi()
        setUpPagination()

        if (AppPreferences.getInstance(this@CategoryActivity).getLoginType().toString() == "staff") {
            manageStaffViews()
        }
    }
    private fun manageStaffViews() = with(binding) {
        cardAdd.setVisibleIfPermission(PermissionKeys.ADD_PRODUCT_CATEGORY)

    }

    private fun View.setVisibleIfPermission(permissionKey: String) {
        visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE

    }

    private fun handleClick() {
        val appPreferences = AppPreferences.getInstance(this)
        binding.cardAdd.setOnClickListener {
           appPreferences.saveCatImgId("")
            appPreferences.saveCatImgName("")
            appPreferences.saveCatIconImgId("")
            appPreferences.saveCatIconImgName("")
            appPreferences.saveImgStatus("")
            val intent = Intent(this, CategoryAddActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.imgBack.setOnClickListener {
            finish() // Closes this screen and returns
        }
    }



    private fun callCatApi() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.currentPageCat= 1
        viewModel.clearCatData()
        val request = ApiService.CatRequest(
            page_no = "1",
            per_page = "4",
            parent_id = ""
        )
        viewModel.getCategoryList(AppPreferences.getInstance(this).getToken().toString(),request)
    }
    private fun setUpPagination() {
        binding.recyclerCategory.isNestedScrollingEnabled = false
        scrollListener = ViewTreeObserver.OnScrollChangedListener {

            val view =
                binding.nestedScrollView.getChildAt(binding.nestedScrollView.childCount - 1) as View
            val diff: Int =
                view.bottom - (binding.nestedScrollView.height + binding.nestedScrollView.scrollY)
            if (diff == 0) {
                if (viewModel.isLastPageCat()) {
                    // No action for the last page
                } else {
                    viewModel.currentPageCat++
                    viewModel.loadNextPageCat("")
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
        catAdapter = CategoryListAdapter(
            mutableListOf()
        ) { product, type ->

            when (type) {
                "edit" -> {
                    AppPreferences.getInstance(this@CategoryActivity).saveCategoryId(product?.id)
                    val intent = Intent(this@CategoryActivity, CategoryEditActivity::class.java)
                    startActivity(intent)
                }
                "delete" -> showDeletePopup(product?.id)
            }
        }

        binding.recyclerCategory.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            adapter = catAdapter
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
        viewModel.deleteCatStatus.observe(this) { message ->
            callCatApi()
        }
        viewModel.deleteCatFailStatus.observe(this) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.productEvent.observe(this) { message ->
            binding.progressBar.visibility = View.GONE

        }
        viewModel.categoryList.observe(this) { data ->

            try {
                val totalPages = data?.totalPages
                if (!totalPages.isNullOrEmpty()) {
                    viewModel.totalPageCountCat = totalPages.toInt()
                } else {
                    // Handle the case where totalPages is empty or null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if totalPages is still an invalid format
            }
            if (viewModel.currentPageCat == 1) {

                manageCategory(data)
            } else {
                catAdapter.addProducts(data?.categories!!)
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
    private fun manageCategory(data: CatListResponse) {
        binding.recyclerCategory.visibility = View.VISIBLE
        val flatCategoryList = flattenCategories(data?.categories ?: emptyList())
        catAdapter.setProducts(flatCategoryList.toMutableList())
    }
    private fun showDeletePopup(id: String?) {
        val builder = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.delete_product_popup, null)
        builder.setView(customLayout)

        val btnCancel = customLayout.findViewById<MaterialCardView>(R.id.card_cancel)
        val btnDelete = customLayout.findViewById<MaterialCardView>(R.id.card_delete)

        val dialog = builder.create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnDelete.setOnClickListener {
            dialog.dismiss()
            binding.progressBar.visibility = View.VISIBLE
            val request= ApiService.CatDetailsRequest(
                id = id.toString()
            )
            viewModel.deleteCategory(AppPreferences.getInstance(this@CategoryActivity).getToken().toString(),request)
        }

        dialog.show()
    }
    private fun flattenCategories(categories: List<CatList>, level: Int = 0): List<CatList> {
        val flatList = mutableListOf<CatList>()

        for (category in categories) {
            val current = category.copy(level = level)
            flatList.add(current)

            val children = category.children ?: emptyList()
            if (children.isNotEmpty()) {
                flatList.addAll(flattenCategories(children, level + 1))
            }
        }

        return flatList
    }

}
