package com.wasfa.doctor.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ActivityMainBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.ui.login.LoginActivity
import com.wasfa.doctor.ui.products.add.AddProductActivity
import com.wasfa.doctor.ui.products.brand.BrandActivity
import com.wasfa.doctor.ui.products.category.CategoryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var POSStatus: Boolean = false
    private var productStatus: Boolean = false
    private var customerStatus: Boolean = false
    private lateinit var dialog: BottomSheetDialog
    private lateinit var cardCancel: MaterialCardView
    private lateinit var cardLogout: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setNav()

        if (AppPreferences.getInstance(this@MainActivity).getLoginType() == "staff"){
            manageMenuViews()
        }
    }

    private fun manageMenuViews() = with(binding.menuPage) {
        lytAddNewProduct.setVisibleIfPermission(PermissionKeys.ADD_NEW_PRODUCT)
        lytAllProduct.setVisibleIfPermission(PermissionKeys.SHOW_ALL_PRODUCT)
        lytCategory.setVisibleIfPermission(PermissionKeys.VIEW_PRODUCT_CATEGORIES)
        lytBrand.setVisibleIfPermission(PermissionKeys.VIEW_ALL_BRANDS)
        lytCustomerList.setVisibleIfPermission(PermissionKeys.VIEW_ALL_CUSTOMERS)
        lytSegment.setVisibleIfPermission(PermissionKeys.VIEW_ALL_SEGMANT)
        lytType.setVisibleIfPermission(PermissionKeys.VIEW_ALL_TYPE)

        lytCustomers.visibility =
            if (listOf(
                    lytCustomerList,
                    lytSegment,
                    lytType
                ).all { it.visibility == View.GONE }
            ) View.GONE else View.VISIBLE
        lytProducts.visibility =
            if (listOf(
                    lytAddNewProduct,
                    lytAllProduct,
                    lytCategory,
                    lytBrand
                ).all { it.visibility == View.GONE }
            ) View.GONE else View.VISIBLE
    }
    private fun View.setVisibleIfPermission(permissionKey: String) {
        Log.d("StaffPerms", "check  :"+permissionKey)
            visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE

    }
    private fun setNav() {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_pos, R.id.nav_products, R.id.nav_sale
            )
        )
        val destination = intent.getIntExtra("navigate_to", -1)
        if (destination != -1) {
            navController.navigate(destination)
        }
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home, R.id.nav_pos, R.id.nav_products, R.id.nav_sale, R.id.nav_account -> {
                    navView.visibility = View.VISIBLE
                }

                else -> {
                    navView.visibility = View.GONE
                }
            }
            when (destination.id) {
                R.id.nav_home -> navView.menu.findItem(R.id.nav_home).isChecked = true
                R.id.nav_pos -> binding.navView.menu.findItem(R.id.nav_pos).isChecked = true
                R.id.nav_products -> binding.navView.menu.findItem(R.id.nav_products).isChecked =
                    true

                R.id.nav_sale -> binding.navView.menu.findItem(R.id.nav_sale).isChecked = true
                R.id.nav_account -> binding.navView.menu.findItem(R.id.nav_account).isChecked = true
            }
        }

    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.nav_home) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        val currentDestination = navController.currentDestination?.id
        when (currentDestination) {
            R.id.nav_home -> binding.navView.menu.findItem(R.id.nav_home).isChecked = true
            R.id.nav_pos -> binding.navView.menu.findItem(R.id.nav_pos).isChecked = true
            R.id.nav_products -> binding.navView.menu.findItem(R.id.nav_products).isChecked = true
            R.id.nav_sale -> binding.navView.menu.findItem(R.id.nav_sale).isChecked = true
            R.id.nav_account -> binding.navView.menu.findItem(R.id.nav_account).isChecked = true
        }
    }


    fun toggleMenuOverlay(isVisible: Boolean) {
        if (AppPreferences.getInstance(this@MainActivity).getLoginType() == "staff"){
            manageMenuViews()
        }
        binding.menuOverlay.visibility = if (isVisible) View.VISIBLE else View.GONE
        manageMenuClick()


    }

    private fun manageMenuClick() {

        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        binding.menuPage.lytLogOut.setOnClickListener {
            showLogoutPopup()
        }

        binding.menuPage.imgBack.setOnClickListener {
            toggleMenuOverlay(false)
        }

        binding.menuPage.lytPos.setOnClickListener {
            if (POSStatus) {
                POSStatus = false
            } else {
                POSStatus = true
            }
            managePOSView()
        }
        binding.menuPage.lytPosRx.setOnClickListener {
            val isTablet = resources.getBoolean(R.bool.isTablet)
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            if (isTablet || isLandscape) {
                navController.navigate(R.id.nav_tab_pos_rx_product)
            } else {
                navController.navigate(R.id.nav_pos_rx_product)
            }
            toggleMenuOverlay(false)
        }
        binding.menuPage.lytPosShop.setOnClickListener {
            val isTablet = resources.getBoolean(R.bool.isTablet)
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            if (isTablet || isLandscape) {
                navController.navigate(R.id.nav_tab_pos_shop_product)
            } else {
                navController.navigate(R.id.nav_pos_shop)
            }
            toggleMenuOverlay(false)
        }

        binding.menuPage.lytCustomers.setOnClickListener {
            if (customerStatus) {
                customerStatus = false
            } else {
                customerStatus = true
            }
            manageCustView()

        }
        binding.menuPage.lytCustomerList.setOnClickListener {
            navController.navigate(R.id.nav_customer)
            toggleMenuOverlay(false)
        }
        binding.menuPage.lytSegment.setOnClickListener {
            navController.navigate(R.id.nav_segment)
            toggleMenuOverlay(false)
        }
        binding.menuPage.lytType.setOnClickListener {
            navController.navigate(R.id.nav_type)
            toggleMenuOverlay(false)
        }

        binding.menuPage.lytProducts.setOnClickListener {

            if (productStatus) {
                productStatus = false
            } else {
                productStatus = true
            }
            manageProductView()
        }
        binding.menuPage.lytAllProduct.setOnClickListener {
            AppPreferences.getInstance(this).savePDType("All Products")
            navController.navigate(R.id.nav_all_product)
            toggleMenuOverlay(false)
        }
        binding.menuPage.lytAddNewProduct.setOnClickListener {
            AppPreferences.getInstance(this).saveAddOrEditStatus("Add New Product")
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
            toggleMenuOverlay(false)
        }
        binding.menuPage.lytCategory.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
            toggleMenuOverlay(false)
        }
        binding.menuPage.lytBrand.setOnClickListener {
            val intent = Intent(this, BrandActivity::class.java)
            startActivity(intent)
            toggleMenuOverlay(false)
        }
    }
    private fun showLogoutPopup() {
        val appPreferences = AppPreferences.getInstance(this)
        val dialogView = layoutInflater.inflate(R.layout.sheet_log_out, null)
        dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)
        cardCancel = dialogView.findViewById(R.id.card_cancel)
        cardLogout = dialogView.findViewById(R.id.card_yes)
        cardLogout.setOnClickListener {
            dialog.dismiss()
            var email = ""
            var pass = ""
            var rememberStatus = ""
            if (appPreferences?.getRememberStatus().toString() == "true"){
                email = appPreferences.getEmail().toString()
                pass =  appPreferences.getPass().toString()
                rememberStatus = "true"
            }
            AppPreferences.getInstance(this).clearAllPreferences()

            if (rememberStatus == "true"){
                appPreferences.saveEmail(email)
                appPreferences.savePass(pass)
                appPreferences.saveRememberStatus("true")
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        cardCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun manageProductView() {
        if (productStatus) {
            binding.menuPage.imgProductArrow.rotation = 0f
            binding.menuPage.lytProductHide.visibility = View.VISIBLE
        } else {
            binding.menuPage.imgProductArrow.rotation = 270f
            binding.menuPage.lytProductHide.visibility = View.GONE
        }
    }

    private fun manageCustView() {
        if (customerStatus) {
            binding.menuPage.imgCustomerArrow.rotation = 0f
            binding.menuPage.lytCustomerHide.visibility = View.VISIBLE
        } else {
            binding.menuPage.imgCustomerArrow.rotation = 270f
            binding.menuPage.lytCustomerHide.visibility = View.GONE
        }
    }

    private fun managePOSView() {

        if (POSStatus) {
            binding.menuPage.imgPosArrow.rotation = 0f
            binding.menuPage.lytPosHide.visibility = View.VISIBLE
        } else {
            binding.menuPage.imgPosArrow.rotation = 270f
            binding.menuPage.lytPosHide.visibility = View.GONE
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)

                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
