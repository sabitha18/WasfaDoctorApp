package com.wasfa.doctor.ui.main

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ActivityDoctorHomeBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.ui.home.DoctorHomeFragment
import com.wasfa.doctor.ui.med.MeditationFragment
import com.wasfa.doctor.ui.pres.add.AddPresNewFragment
import com.wasfa.doctor.ui.pres.add.DoctorRXFragment
import com.wasfa.doctor.ui.report.PrescribedRxFragment
import com.wasfa.doctor.ui.report.ReportFragment
import com.wasfa.doctor.ui.settings.SettingsFragment
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.ui.login.LoginActivity
import com.wasfa.doctor.ui.report.AllPrescriptionFragment


class DoctorHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorHomeBinding
    private lateinit var dialog: BottomSheetDialog
    private lateinit var cardCancel: MaterialCardView
    private lateinit var cardLogout: MaterialCardView
    private var isBottomNavVisible = true
    private var isSideMenuVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDoctorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomView.customBottomNav) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }

        loadFragment(DoctorHomeFragment())


        AppPreferences.getInstance(this@DoctorHomeActivity).savePatientId("")
        AppPreferences.getInstance(this@DoctorHomeActivity).saveNewRXStatus("")
        AppPreferences.getInstance(this@DoctorHomeActivity).saveKey("false")


        handleViewForDevice()




    }

    private fun manageTabletClicks() {
        binding.sideTabletView.lytHome.setOnClickListener {
            manageTabHomeIcon()
            loadFragment(DoctorHomeFragment())
        }
        binding.sideTabletView.lytMed.setOnClickListener {
            manageTabMedIcon()
        }
        binding.sideTabletView.lytReportNew.setOnClickListener {
            manageTabReportNewIcon()
        }
        binding.sideTabletView.lytFavMed.setOnClickListener {
            binding.sideTabletView.prodBulletSelected!!.visibility = View.GONE
            binding.sideTabletView.prodBullet!!.visibility = View.VISIBLE

            binding.sideTabletView.favBullet!!.visibility = View.GONE
            binding.sideTabletView.favBulletSelected!!.visibility = View.VISIBLE

            binding.sideTabletView.txtFav!!.setTextColor(Color.parseColor("#A61C5C"))
            binding.sideTabletView.txtAllProducts!!.setTextColor(Color.parseColor("#9E9E9E"))
            AppPreferences.getInstance(this@DoctorHomeActivity).saveFavStatus("1")
            loadFragment(MeditationFragment())
        }
        binding.sideTabletView.lytAllProdMed.setOnClickListener {
            binding.sideTabletView.prodBulletSelected!!.visibility = View.VISIBLE
            binding.sideTabletView.prodBullet!!.visibility = View.GONE

            binding.sideTabletView.favBullet!!.visibility = View.VISIBLE
            binding.sideTabletView.favBulletSelected!!.visibility = View.GONE

            binding.sideTabletView.txtFav!!.setTextColor(Color.parseColor("#9E9E9E"))
            binding.sideTabletView.txtAllProducts!!.setTextColor(Color.parseColor("#A61C5C"))

            AppPreferences.getInstance(this@DoctorHomeActivity).saveFavStatus("0")
            loadFragment(MeditationFragment())
        }
        binding.sideTabletView.lytPrescriptions.setOnClickListener {
            manageTabPresIcon()
            AppPreferences.getInstance(this@DoctorHomeActivity).saveNewRXStatus("old")
            loadFragment(AddPresNewFragment())

        }
        binding.sideTabletView.lytPrescriptionsNew.setOnClickListener {
            manageTabPresNewIcon()
            AppPreferences.getInstance(this@DoctorHomeActivity).saveNewRXStatus("new")
            loadFragment(AddPresNewFragment())
        }
        binding.sideTabletView.lytSettings.setOnClickListener {
            manageTabSettingsIcon()
            loadFragment(SettingsFragment())
        }
        binding.sideTabletView.lytReport.setOnClickListener {
            manageTabReportIcon()
            loadFragment(ReportFragment())
        }
        binding.sideTabletView.lytLogout.setOnClickListener {
            manageTabLogoutIcon()
            showLogoutPopup()
        }
        binding.sideTabletView.lytPrescribedRx.setOnClickListener {
            binding.sideTabletView.prescribedMedBulletSelected!!.visibility = View.GONE
            binding.sideTabletView.prescribedMedBullet!!.visibility = View.VISIBLE

            binding.sideTabletView.prescribedRxBullet!!.visibility = View.GONE
            binding.sideTabletView.prescribedRxBulletSelected!!.visibility = View.VISIBLE

            binding.sideTabletView.txtPrescribedRx!!.setTextColor(Color.parseColor("#A61C5C"))
            binding.sideTabletView.txtPrescribedMed!!.setTextColor(Color.parseColor("#9E9E9E"))
            loadFragment(PrescribedRxFragment())
        }
        binding.sideTabletView.lytPrescribedMed.setOnClickListener {
            binding.sideTabletView.prescribedMedBulletSelected!!.visibility = View.VISIBLE
            binding.sideTabletView.prescribedMedBullet!!.visibility = View.GONE

            binding.sideTabletView.prescribedRxBullet!!.visibility = View.VISIBLE
            binding.sideTabletView.prescribedRxBulletSelected!!.visibility = View.GONE

            binding.sideTabletView.txtPrescribedRx!!.setTextColor(Color.parseColor("#9E9E9E"))
            binding.sideTabletView.txtPrescribedMed!!.setTextColor(Color.parseColor("#A61C5C"))

            loadFragment(ReportFragment())
        }
    }

    private fun manageBottomNavClick() {
        binding.bottomView.navAdd.setOnClickListener {
            loadFragment(DoctorRXFragment())
        }
        binding.bottomView.lytHome.setOnClickListener {
            manageHomeIcon()
            loadFragment(DoctorHomeFragment())
        }
        binding.bottomView.lytMed.setOnClickListener {
            manageMedIcon()
            loadFragment(MeditationFragment())
        }
        binding.bottomView.lytPrescriptions.setOnClickListener {
            managePresIcon()
            loadFragment(ReportFragment())
        }
        binding.bottomView.lytSettings.setOnClickListener {
            manageSettingsIcon()
            loadFragment(SettingsFragment())
        }
    }

    private fun manageSideMenuClick() {
        binding.sideMenu.lytHome.setOnClickListener {
            manageHomeIcon()
            manageSideTabHomeIcon()
            loadFragment(DoctorHomeFragment())
            hideSideMenu()
        }
        binding.sideMenu.lytMed.setOnClickListener {
            manageMedIcon()
            manageTabMedIcon()
        }
        binding.sideMenu.lytReportNew.setOnClickListener {
            manageTabReportNewIcon()
        }
        binding.sideMenu.lytFavMed.setOnClickListener {
            binding.sideMenu.prodBulletSelected.visibility = View.GONE
            binding.sideMenu.prodBullet.visibility = View.VISIBLE

            binding.sideMenu.favBullet.visibility = View.GONE
            binding.sideMenu.favBulletSelected.visibility = View.VISIBLE

            binding.sideMenu.txtFav.setTextColor(Color.parseColor("#A61C5C"))
            binding.sideMenu.txtAllProducts.setTextColor(Color.parseColor("#9E9E9E"))
            AppPreferences.getInstance(this@DoctorHomeActivity).saveFavStatus("1")
            loadFragment(MeditationFragment())
            hideSideMenu()
        }
        binding.sideMenu.lytAllProdMed.setOnClickListener {
            binding.sideMenu.prodBulletSelected!!.visibility = View.VISIBLE
            binding.sideMenu.prodBullet.visibility = View.GONE

            binding.sideMenu.favBullet.visibility = View.VISIBLE
            binding.sideMenu.favBulletSelected!!.visibility = View.GONE

            binding.sideMenu.txtFav.setTextColor(Color.parseColor("#9E9E9E"))
            binding.sideMenu.txtAllProducts.setTextColor(Color.parseColor("#A61C5C"))

            AppPreferences.getInstance(this@DoctorHomeActivity).saveFavStatus("0")
            loadFragment(MeditationFragment())
            hideSideMenu()
        }
        binding.sideMenu.lytPrescriptions.setOnClickListener {

            manageTabPresIcon()
            AppPreferences.getInstance(this@DoctorHomeActivity).saveNewRXStatus("old")
            loadFragment(DoctorRXFragment())
            hideSideMenu()
        }
        binding.sideMenu.lytPrescriptionsNew.setOnClickListener {

            manageTabPresNewIcon()
            AppPreferences.getInstance(this@DoctorHomeActivity).saveNewRXStatus("new")
            loadFragment(DoctorRXFragment())
            hideSideMenu()
        }
        binding.sideMenu.lytSettings.setOnClickListener {


            manageTabSettingsIcon()
            manageSettingsIcon()
            loadFragment(SettingsFragment())
            hideSideMenu()
        }
        binding.sideMenu.lytReport.setOnClickListener {
            manageTabReportIcon()
            managePresIcon()
            loadFragment(ReportFragment())
            hideSideMenu()
        }
        binding.sideMenu.lytLogout.setOnClickListener {

            manageTabLogoutIcon()

            showLogoutPopup()
            hideSideMenu()
        }
        binding.sideMenu.lytPrescribedRx.setOnClickListener {
            binding.sideMenu.prescribedMedBulletSelected!!.visibility = View.GONE
            binding.sideMenu.prescribedMedBullet!!.visibility = View.VISIBLE

            binding.sideMenu.prescribedRxBullet!!.visibility = View.GONE
            binding.sideMenu.prescribedRxBulletSelected!!.visibility = View.VISIBLE

            binding.sideMenu.txtPrescribedRx!!.setTextColor(Color.parseColor("#A61C5C"))
            binding.sideMenu.txtPrescribedMed!!.setTextColor(Color.parseColor("#9E9E9E"))
            loadFragment(PrescribedRxFragment())
            managePresIcon()
            hideSideMenu()
        }
        binding.sideMenu.lytPrescribedMed.setOnClickListener {
            binding.sideMenu.prescribedMedBulletSelected.visibility = View.VISIBLE
            binding.sideMenu.prescribedMedBullet.visibility = View.GONE

            binding.sideMenu.prescribedRxBullet.visibility = View.VISIBLE
            binding.sideMenu.prescribedRxBulletSelected.visibility = View.GONE

            binding.sideMenu.txtPrescribedRx.setTextColor(Color.parseColor("#9E9E9E"))
            binding.sideMenu.txtPrescribedMed.setTextColor(Color.parseColor("#A61C5C"))

            managePresIcon()
            loadFragment(ReportFragment())
            hideSideMenu()
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

    fun setDefault(){
        manageHomeIcon()
        manageSideTabHomeIcon()
        hideDefault()
    }
    fun toggleBottomNav() {
        if (isBottomNavVisible) {
            hideBottomNav()
        } else {
            showBottomNav()
        }
    }


    fun toggleSideMenu() {
        if (isSideMenuVisible) hideSideMenu() else showSideMenu()
    }

    fun showSideMenu() {
        val sideMenu = binding.sideMenu.root
        val dimOverlay = binding.dimOverlay

        // Show overlay and fade in
        dimOverlay.visibility = View.VISIBLE
        dimOverlay.animate().alpha(1f).setDuration(200).start()

        // Side menu slide in
        sideMenu.visibility = View.VISIBLE
        sideMenu.animate()
            .translationX(0f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Close menu when tapping overlay
        dimOverlay.setOnClickListener { hideSideMenu() }

        isSideMenuVisible = true
    }

    fun hideSideMenu() {
        val sideMenu = binding.sideMenu.root
        val dimOverlay = binding.dimOverlay

        // Side menu slide out
        sideMenu.animate()
            .translationX(-sideMenu.width.toFloat())
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction { sideMenu.visibility = View.GONE }
            .start()

        // Fade out overlay
        dimOverlay.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction { dimOverlay.visibility = View.GONE }
            .start()

        isSideMenuVisible = false
    }


    fun showBottomNav() {
        if (isTablet()){
            binding.sideTabletView.customBottomNav.visibility = View.VISIBLE
            binding.bottomView.customBottomNav.visibility = View.GONE
        }else{
            binding.sideTabletView.customBottomNav.visibility = View.GONE
            binding.bottomView.customBottomNav.visibility = View.VISIBLE
        }

        isBottomNavVisible = true
    }

    fun hideBottomNav() {
        if (isTablet()){
            binding.sideTabletView.customBottomNav.visibility = View.GONE
            binding.bottomView.customBottomNav.visibility = View.GONE
        }else{
            binding.sideTabletView.customBottomNav.visibility = View.GONE
            binding.bottomView.customBottomNav.visibility = View.GONE
        }

        isBottomNavVisible = false
    }

    private fun showLogoutPopup() {
        val appPreferences = AppPreferences.getInstance(this@DoctorHomeActivity)
        val dialogView = layoutInflater.inflate(R.layout.sheet_log_out, null)
        dialog = BottomSheetDialog(this@DoctorHomeActivity, R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        cardCancel = dialogView.findViewById(R.id.card_cancel)
        cardLogout = dialogView.findViewById(R.id.card_yes)
        cardLogout.setOnClickListener {
            dialog.dismiss()
            var email = ""
            var pass = ""
            var rememberStatus = ""
            if (appPreferences?.getRememberStatus().toString() == "true") {
                email = appPreferences.getEmail().toString()
                pass = appPreferences.getPass().toString()
                rememberStatus = "true"
            }
            AppPreferences.getInstance(this@DoctorHomeActivity).clearAllPreferences()

            if (rememberStatus == "true") {
                appPreferences.saveEmail(email)
                appPreferences.savePass(pass)
                appPreferences.saveRememberStatus("true")
            }
            val intent = Intent(this@DoctorHomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        cardCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        return widthDp >= 600 ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    fun updatePermissionsUI() {
        val prefs = AppPreferences.getInstance(this@DoctorHomeActivity)
        val doctorType = prefs.getDoctorType()

        // Hide everything first
        binding.sideMenu.lytPrescriptions.visibility = View.GONE
        binding.sideTabletView.lytPrescriptions.visibility = View.GONE
        binding.sideMenu.lytPrescriptionsNew.visibility = View.GONE
        binding.sideTabletView.lytPrescriptionsNew.visibility = View.GONE

        when (doctorType) {
            "Government" -> {
                if (PermissionManager.hasPermission(PermissionKeys.POS_MANAGER)) {
                    prefs.saveEditStatus("false")
                    binding.sideMenu.lytPrescriptions.visibility = View.VISIBLE
                    binding.sideTabletView.lytPrescriptions.visibility = View.VISIBLE
                }
            }

            "Private" -> {
                if (PermissionManager.hasPermission(PermissionKeys.POS_NEW_RX)) {

                    prefs.saveEditStatus("true")
                    binding.sideMenu.lytPrescriptionsNew.visibility = View.VISIBLE
                    binding.sideTabletView.lytPrescriptionsNew.visibility = View.VISIBLE
                }

            }

            else -> {
                prefs.saveEditStatus("false")
                // doctorType is null or unknown
                binding.sideMenu.lytPrescriptions.visibility = View.VISIBLE
                binding.sideTabletView.lytPrescriptions.visibility = View.VISIBLE

            }
        }
    }

    private fun View.setVisibleIfPermission(permissionKey: String) {
        visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE
    }

    private fun hideDefault() {
        binding.sideTabletView.lytMedHide.visibility = View.GONE
        binding.sideTabletView.imgPosArrow.rotation = 270f
        binding.sideTabletView.lytReportHide.visibility = View.GONE
        binding.sideTabletView.imgReportArrowNew.rotation = 270f

        binding.sideMenu.lytMedHide.visibility = View.GONE
        binding.sideMenu.imgPosArrow.rotation = 270f
        binding.sideMenu.lytReportHide.visibility = View.GONE
        binding.sideMenu.imgReportArrowNew.rotation = 270f
    }

    //home
    private fun manageHomeIcon() {

        binding.bottomView.imgHomeSelected.visibility = View.VISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doc_home_selected)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavHome.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        medUnSelected()
        reportUnSelected()
        settingsUnSelected()


    }

    private fun manageTabHomeIcon() {
        hideDefault()
        binding.sideTabletView.lytHome.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideTabletView.imgHomeSelected.visibility = View.VISIBLE
        binding.sideTabletView.navHome.setImageResource(R.drawable.nav_doc_home_selected)
        binding.sideTabletView.txtNavHome.setTextColor(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        posRXNewUnselected()
        prescripedRXUnselected()
        medTabUnselected()
        presUnSelected()
        settingsTabUnSelected()
        logoutUnSelected()
        reportTabUnSelected()
    }

    override fun onResume() {
        super.onResume()

        handleViewForDevice()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        handleViewForDevice()
    }

    private fun handleViewForDevice() {
        if (isTablet()) {
            binding.sideTabletView.customBottomNav.visibility = View.VISIBLE
            binding.bottomView.customBottomNav.visibility = View.GONE
            binding.sideMenu.customBottomNav.visibility = View.GONE
            manageTabletClicks()
        } else {
            binding.bottomView.customBottomNav.visibility = View.VISIBLE
            binding.sideTabletView.customBottomNav.visibility = View.GONE
            manageBottomNavClick()
            manageSideMenuClick()
        }
    }

    private fun manageSideTabHomeIcon() {

        binding.sideMenu.lytHome.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideMenu.imgHomeSelected.visibility = View.VISIBLE
        binding.sideMenu.navHome.setImageResource(R.drawable.nav_doc_home_selected)
        binding.sideMenu.txtNavHome.setTextColor(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        posRXNewUnselected()
        prescripedRXUnselected()
        medTabUnselected()
        presUnSelected()
        settingsTabUnSelected()
        logoutUnSelected()
        reportTabUnSelected()
    }

    //med
    private fun manageMedIcon() {

        binding.bottomView.imgMedSelected.visibility = View.VISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med_selected)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        homeUnSelected()

        reportUnSelected()

        settingsUnSelected()

    }

    private fun manageTabMedIcon() {

        if (binding.sideTabletView.lytMedHide.visibility == View.VISIBLE) {
            binding.sideTabletView.lytMedHide.visibility = View.GONE
            binding.sideTabletView.imgPosArrow.rotation = 270f
        } else {
            binding.sideTabletView.imgPosArrow.rotation = 0f
            binding.sideTabletView.lytMedHide.visibility = View.VISIBLE
        }

        binding.sideTabletView.lytReportHide.visibility = View.GONE
        binding.sideTabletView.imgReportArrowNew.rotation = 270f

        binding.sideTabletView.imgPosArrow.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A61C5C"))
        binding.sideTabletView.lytMed.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideTabletView.imgMedSelected.visibility = View.VISIBLE
        binding.sideTabletView.navMed.setImageResource(R.drawable.nav_med_selected)
        binding.sideTabletView.txtNavMed.setTextColor(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavMed.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        prescripedRXUnselected()

        homeTabUnSelected()

        presUnSelected()

        posRXNewUnselected()

        settingsUnSelected()

        logoutUnSelected()

        reportTabUnSelected()


        if (binding.sideMenu.lytMedHide.visibility == View.VISIBLE) {
            binding.sideMenu.lytMedHide.visibility = View.GONE
            binding.sideMenu.imgPosArrow.rotation = 270f
        } else {
            binding.sideMenu.imgPosArrow.rotation = 0f
            binding.sideMenu.lytMedHide.visibility = View.VISIBLE
        }

        binding.sideMenu.lytReportHide.visibility = View.GONE
        binding.sideMenu.imgReportArrowNew.rotation = 270f

        binding.sideMenu.imgPosArrow.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A61C5C"))
        binding.sideMenu.lytMed.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideMenu.imgMedSelected.visibility = View.VISIBLE
        binding.sideMenu.navMed.setImageResource(R.drawable.nav_med_selected)
        binding.sideMenu.txtNavMed.setTextColor(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavMed.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)
    }

    //pres
    private fun managePresIcon() {
        binding.bottomView.imgPresSelected.visibility = View.VISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_rx_selected)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavPres.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        homeUnSelected()

        medUnSelected()

        settingsUnSelected()

    }

    private fun manageTabPresIcon() {

        hideDefault()

        binding.sideTabletView.lytPrescriptions.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideTabletView.imgPresSelected.visibility = View.VISIBLE
        binding.sideTabletView.navPres.setImageResource(R.drawable.nav_prescription)
        binding.sideTabletView.txtNavPres.setTextColor(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        prescripedRXUnselected()

        posRXNewUnselected()

        homeTabUnSelected()
        medTabUnselected()

        settingsTabUnSelected()

        logoutUnSelected()

        reportTabUnSelected()


        binding.sideMenu.lytPrescriptions.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideMenu.imgPresSelected.visibility = View.VISIBLE
        binding.sideMenu.navPres.setImageResource(R.drawable.nav_prescription)
        binding.sideMenu.txtNavPres.setTextColor(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)
    }

    //settings
    private fun manageSettingsIcon() {

        binding.bottomView.imgSettingsSelected.visibility = View.VISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings_selected)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        homeUnSelected()

        medUnSelected()

        reportUnSelected()

    }

    private fun manageTabSettingsIcon() {

        hideDefault()

        binding.sideTabletView.lytSettings.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideTabletView.imgSettingsSelected.visibility = View.VISIBLE
        binding.sideTabletView.navSettings.setImageResource(R.drawable.nav_settings_selected)
        binding.sideTabletView.txtNavSettings.setTextColor(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        prescripedRXUnselected()

        posRXNewUnselected()

        homeUnSelected()

        medTabUnselected()

        presUnSelected()
        logoutUnSelected()

        reportTabUnSelected()

        binding.sideMenu.lytSettings.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideMenu.imgSettingsSelected.visibility = View.VISIBLE
        binding.sideMenu.navSettings.setImageResource(R.drawable.nav_settings_selected)
        binding.sideMenu.txtNavSettings.setTextColor(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)
    }

    //pos rx
    private fun manageTabPresNewIcon() {

        hideDefault()

        binding.sideTabletView.lytPrescriptionsNew.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideTabletView.imgPresSelectedNew.visibility = View.VISIBLE
        binding.sideTabletView.navPresNew.setImageResource(R.drawable.nav_prescription)
        binding.sideTabletView.txtNavPresNew.setTextColor(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        prescripedRXUnselected()

        presUnSelected()

        homeTabUnSelected()

        medTabUnselected()

        settingsTabUnSelected()

        logoutUnSelected()
        reportTabUnSelected()


        binding.sideMenu.lytPrescriptionsNew.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideMenu.imgPresSelectedNew.visibility = View.VISIBLE
        binding.sideMenu.navPresNew.setImageResource(R.drawable.nav_prescription)
        binding.sideMenu.txtNavPresNew.setTextColor(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)
    }

    //logout
    private fun manageTabLogoutIcon() {

        hideDefault()

        binding.sideTabletView.lytLogout?.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideTabletView.imgLogoutSelected?.visibility = View.VISIBLE
        binding.sideTabletView.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.sideTabletView.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavLogout?.setTextColor(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        prescripedRXUnselected()

        settingsTabUnSelected()

        posRXNewUnselected()

        homeTabUnSelected()

        medTabUnselected()

        presUnSelected()

        reportTabUnSelected()
    }

    //report
    private fun manageTabReportIcon() {

        hideDefault()

        binding.sideTabletView.lytReport?.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideTabletView.imgReportSelected?.visibility = View.VISIBLE
        binding.sideTabletView.navReport?.setImageResource(R.drawable.nav_rx_selected)
        binding.sideTabletView.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavReport?.setTextColor(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        prescripedRXUnselected()

        posRXNewUnselected()

        logoutUnSelected()

        settingsTabUnSelected()

        homeTabUnSelected()

        medTabUnselected()

        presUnSelected()


        binding.sideMenu.lytReport?.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideMenu.imgReportSelected?.visibility = View.VISIBLE
        binding.sideMenu.navReport?.setImageResource(R.drawable.nav_rx_selected)
        binding.sideMenu.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavReport?.setTextColor(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

    }

    //prescription rx
    private fun manageTabReportNewIcon() {

        if (binding.sideTabletView.lytReportHide.visibility == View.VISIBLE) {
            binding.sideTabletView.lytReportHide.visibility = View.GONE
            binding.sideTabletView.imgReportArrowNew.rotation = 270f
        } else {
            binding.sideTabletView.imgReportArrowNew.rotation = 0f
            binding.sideTabletView.lytReportHide.visibility = View.VISIBLE
        }
        binding.sideTabletView.lytMedHide.visibility = View.GONE

        binding.sideTabletView.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A61C5C"))
        binding.sideTabletView.lytReportNew.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideTabletView.imgReportSelectedNew.visibility = View.VISIBLE
        binding.sideTabletView.navReportNew.setImageResource(R.drawable.nav_rx_selected)
        binding.sideTabletView.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavReportNew.setTextColor(Color.parseColor("#A42161"))
        binding.sideTabletView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        homeTabUnSelected()

        presUnSelected()

        posRXNewUnselected()

        settingsTabUnSelected()

        logoutUnSelected()

        reportTabUnSelected()

        medTabUnselected()

        if (binding.sideMenu.lytReportHide.visibility == View.VISIBLE) {
            binding.sideMenu.lytReportHide.visibility = View.GONE
            binding.sideMenu.imgReportArrowNew.rotation = 270f
        } else {
            binding.sideMenu.imgReportArrowNew.rotation = 0f
            binding.sideMenu.lytReportHide.visibility = View.VISIBLE
        }
        binding.sideMenu.lytMedHide.visibility = View.GONE

        binding.sideMenu.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A61C5C"))
        binding.sideMenu.lytReportNew.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideMenu.imgReportSelectedNew.visibility = View.VISIBLE
        binding.sideMenu.navReportNew.setImageResource(R.drawable.nav_rx_selected)
        binding.sideMenu.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavReportNew.setTextColor(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

    }


    private fun settingsUnSelected() {
        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

    }

    private fun reportUnSelected() {
        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_rx)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun medUnSelected() {
        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun homeUnSelected() {
        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun homeTabUnSelected() {
        binding.sideTabletView.lytHome.background = null
        binding.sideTabletView.imgHomeSelected.visibility = View.INVISIBLE
        binding.sideTabletView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.sideTabletView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytHome.background = null
        binding.sideMenu.imgHomeSelected.visibility = View.INVISIBLE
        binding.sideMenu.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.sideMenu.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun reportTabUnSelected() {
        binding.sideTabletView.lytReport.background = null
        binding.sideTabletView.imgReportSelected.visibility = View.INVISIBLE
        binding.sideTabletView.navReport.setImageResource(R.drawable.nav_rx)
        binding.sideTabletView.navReport.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavReport.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavReport.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytReport.background = null
        binding.sideMenu.imgReportSelected.visibility = View.INVISIBLE
        binding.sideMenu.navReport.setImageResource(R.drawable.nav_rx)
        binding.sideMenu.navReport.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavReport.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavReport.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun logoutUnSelected() {
        binding.sideTabletView.lytLogout.background = null
        binding.sideTabletView.imgLogoutSelected.visibility = View.INVISIBLE
        binding.sideTabletView.navLogout.setImageResource(R.drawable.log_out_icon)
        binding.sideTabletView.navLogout.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavLogout.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavLogout.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)


        binding.sideMenu.lytLogout.background = null
        binding.sideMenu.imgLogoutSelected.visibility = View.INVISIBLE
        binding.sideMenu.navLogout.setImageResource(R.drawable.log_out_icon)
        binding.sideMenu.navLogout.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavLogout.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavLogout.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun settingsTabUnSelected() {
        binding.sideTabletView.lytSettings.background = null
        binding.sideTabletView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.sideTabletView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.sideTabletView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytSettings.background = null
        binding.sideMenu.imgSettingsSelected.visibility = View.INVISIBLE
        binding.sideMenu.navSettings.setImageResource(R.drawable.nav_settings)
        binding.sideMenu.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun presUnSelected() {
        binding.sideTabletView.lytPrescriptions.background = null
        binding.sideTabletView.imgPresSelected.visibility = View.INVISIBLE
        binding.sideTabletView.navPres.setImageResource(R.drawable.nav_pres)
        binding.sideTabletView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)


        binding.sideMenu.lytPrescriptions.background = null
        binding.sideMenu.imgPresSelected.visibility = View.INVISIBLE
        binding.sideMenu.navPres.setImageResource(R.drawable.nav_pres)
        binding.sideMenu.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun medTabUnselected() {
        binding.sideTabletView.imgPosArrow.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.lytMed.background = null
        binding.sideTabletView.imgMedSelected.visibility = View.INVISIBLE
        binding.sideTabletView.navMed.setImageResource(R.drawable.nav_med)
        binding.sideTabletView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavMed.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)


        binding.sideMenu.imgPosArrow.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideMenu.lytMed.background = null
        binding.sideMenu.imgMedSelected.visibility = View.INVISIBLE
        binding.sideMenu.navMed.setImageResource(R.drawable.nav_med)
        binding.sideMenu.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavMed.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun prescripedRXUnselected() {
        binding.sideTabletView.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.lytReportNew.background = null
        binding.sideTabletView.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.sideTabletView.navReportNew.setImageResource(R.drawable.nav_rx)
        binding.sideTabletView.navReportNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)


        binding.sideMenu.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideMenu.lytReportNew.background = null
        binding.sideMenu.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.sideMenu.navReportNew.setImageResource(R.drawable.nav_rx)
        binding.sideMenu.navReportNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun posRXNewUnselected() {
        binding.sideTabletView.lytPrescriptionsNew.background = null
        binding.sideTabletView.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.sideTabletView.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.sideTabletView.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideTabletView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytPrescriptionsNew.background = null
        binding.sideMenu.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.sideMenu.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.sideMenu.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}