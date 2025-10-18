package com.wasfa.doctor.doctor.main

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import com.wasfa.doctor.doctor.home.DoctorHomeFragment
import com.wasfa.doctor.doctor.med.MeditationFragment
import com.wasfa.doctor.doctor.pres.add.AddPresNewFragment
import com.wasfa.doctor.doctor.pres.add.DoctorRXFragment
import com.wasfa.doctor.doctor.report.PrescribedRxFragment
import com.wasfa.doctor.doctor.report.ReportFragment
import com.wasfa.doctor.doctor.settings.SettingsFragment
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.ui.login.LoginActivity

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


        manageSideMenuClick()

        manageBottomNavClick()

    }

    private fun manageBottomNavClick() {
        binding.bottomView.navAdd.setOnClickListener {
            if (isTablet()) {
                loadFragment(AddPresNewFragment())
            } else {
                loadFragment(DoctorRXFragment())
            }

        }
        binding.bottomView.lytHome.setOnClickListener {
            if (isTablet()) {
                manageTabHomeIcon()
            } else {
                manageHomeIcon()
            }

            loadFragment(DoctorHomeFragment())
        }
        binding.bottomView.lytMed.setOnClickListener {
            if (isTablet()) {
                manageTabMedIcon()
            } else {
                manageMedIcon()
                loadFragment(MeditationFragment())
            }


        }
        // report new
        binding.bottomView.lytReportNew.setOnClickListener {
            if (isTablet()) {
                manageTabReportNewIcon()
            }


        }
        binding.bottomView.lytFavMed.setOnClickListener {
            binding.bottomView.prodBulletSelected!!.visibility = View.GONE
            binding.bottomView.prodBullet!!.visibility = View.VISIBLE

            binding.bottomView.favBullet!!.visibility = View.GONE
            binding.bottomView.favBulletSelected!!.visibility = View.VISIBLE

            binding.bottomView.txtFav!!.setTextColor(Color.parseColor("#A61C5C"))
            binding.bottomView.txtAllProducts!!.setTextColor(Color.parseColor("#9E9E9E"))
            AppPreferences.getInstance(this@DoctorHomeActivity).saveFavStatus("1")
            loadFragment(MeditationFragment())
        }
        binding.bottomView.lytAllProdMed.setOnClickListener {
            binding.bottomView.prodBulletSelected!!.visibility = View.VISIBLE
            binding.bottomView.prodBullet!!.visibility = View.GONE

            binding.bottomView.favBullet!!.visibility = View.VISIBLE
            binding.bottomView.favBulletSelected!!.visibility = View.GONE

            binding.bottomView.txtFav!!.setTextColor(Color.parseColor("#9E9E9E"))
            binding.bottomView.txtAllProducts!!.setTextColor(Color.parseColor("#A61C5C"))

            AppPreferences.getInstance(this@DoctorHomeActivity).saveFavStatus("0")
            loadFragment(MeditationFragment())
        }
        binding.bottomView.lytPrescriptions.setOnClickListener {
            if (isTablet()) {
                manageTabPresIcon()
                AppPreferences.getInstance(this@DoctorHomeActivity).saveNewRXStatus("old")
                loadFragment(AddPresNewFragment())
            } else {
                managePresIcon()
                loadFragment(ReportFragment())
            }


        }
        binding.bottomView.lytPrescriptionsNew.setOnClickListener {
            if (isTablet()) {
                manageTabPresNewIcon()
                AppPreferences.getInstance(this@DoctorHomeActivity).saveNewRXStatus("new")
                loadFragment(AddPresNewFragment())
            } else {
                managePresIcon()
                loadFragment(ReportFragment())
            }


        }
        binding.bottomView.lytSettings.setOnClickListener {

            if (isTablet()) {
                manageTabSettingsIcon()
            } else {
                manageSettingsIcon()
            }
            loadFragment(SettingsFragment())
        }
        binding.bottomView.lytReport.setOnClickListener {
            if (isTablet()) {
                println("---------- report")
                manageTabReportIcon()
            }
            loadFragment(ReportFragment())
        }
        binding.bottomView.lytLogout?.setOnClickListener {
            if (isTablet()) {
                manageTabLogoutIcon()
            }
            showLogoutPopup()
        }

        //report section click
        binding.bottomView.lytPrescribedRx.setOnClickListener {
            binding.bottomView.prescribedMedBulletSelected!!.visibility = View.GONE
            binding.bottomView.prescribedMedBullet!!.visibility = View.VISIBLE

            binding.bottomView.prescribedRxBullet!!.visibility = View.GONE
            binding.bottomView.prescribedRxBulletSelected!!.visibility = View.VISIBLE

            binding.bottomView.txtPrescribedRx!!.setTextColor(Color.parseColor("#A61C5C"))
            binding.bottomView.txtPrescribedMed!!.setTextColor(Color.parseColor("#9E9E9E"))
            loadFragment(PrescribedRxFragment())
        }
        binding.bottomView.lytPrescribedMed.setOnClickListener {
            binding.bottomView.prescribedMedBulletSelected!!.visibility = View.VISIBLE
            binding.bottomView.prescribedMedBullet!!.visibility = View.GONE

            binding.bottomView.prescribedRxBullet!!.visibility = View.VISIBLE
            binding.bottomView.prescribedRxBulletSelected!!.visibility = View.GONE

            binding.bottomView.txtPrescribedRx!!.setTextColor(Color.parseColor("#9E9E9E"))
            binding.bottomView.txtPrescribedMed!!.setTextColor(Color.parseColor("#A61C5C"))

            loadFragment(ReportFragment())
        }
    }

    private fun manageSideMenuClick() {
        binding.sideMenu.lytHome.setOnClickListener {
            if (isTablet()) {
                manageTabHomeIcon()
            } else {
                manageHomeIcon()
            }

            loadFragment(DoctorHomeFragment())
            hideSideMenu()
        }
        binding.sideMenu.lytMed.setOnClickListener {
                manageSideMenuMedIcon()
        }
        binding.sideMenu.lytReportNew.setOnClickListener {
            manageTabReportNewIcon()
            hideSideMenu()
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
            loadFragment(AddPresNewFragment())
            hideSideMenu()
        }
        binding.sideMenu.lytPrescriptionsNew.setOnClickListener {

                manageTabPresNewIcon()
                AppPreferences.getInstance(this@DoctorHomeActivity).saveNewRXStatus("new")
                loadFragment(AddPresNewFragment())
        }
        binding.sideMenu.lytSettings.setOnClickListener {


                manageTabSettingsIcon()

            loadFragment(SettingsFragment())
        }
        binding.sideMenu.lytReport.setOnClickListener {
                manageTabReportIcon()
            loadFragment(ReportFragment())
        }
        binding.sideMenu.lytLogout.setOnClickListener {

                manageTabLogoutIcon()

            showLogoutPopup()
        }
        binding.sideMenu.lytPrescribedRx.setOnClickListener {
            binding.sideMenu.prescribedMedBulletSelected!!.visibility = View.GONE
            binding.sideMenu.prescribedMedBullet!!.visibility = View.VISIBLE

            binding.sideMenu.prescribedRxBullet!!.visibility = View.GONE
            binding.sideMenu.prescribedRxBulletSelected!!.visibility = View.VISIBLE

            binding.sideMenu.txtPrescribedRx!!.setTextColor(Color.parseColor("#A61C5C"))
            binding.sideMenu.txtPrescribedMed!!.setTextColor(Color.parseColor("#9E9E9E"))
            loadFragment(PrescribedRxFragment())
        }
        binding.sideMenu.lytPrescribedMed.setOnClickListener {
            binding.sideMenu.prescribedMedBulletSelected.visibility = View.VISIBLE
            binding.sideMenu.prescribedMedBullet.visibility = View.GONE

            binding.sideMenu.prescribedRxBullet.visibility = View.VISIBLE
            binding.sideMenu.prescribedRxBulletSelected.visibility = View.GONE

            binding.sideMenu.txtPrescribedRx.setTextColor(Color.parseColor("#9E9E9E"))
            binding.sideMenu.txtPrescribedMed.setTextColor(Color.parseColor("#A61C5C"))

            loadFragment(ReportFragment())
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
        binding.bottomView.customBottomNav.visibility = View.VISIBLE
        isBottomNavVisible = true
    }

    fun hideBottomNav() {
        binding.bottomView.customBottomNav.visibility = View.GONE
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
        binding.bottomView.lytPrescriptionsNew.setVisibleIfPermission(PermissionKeys.POS_NEW_RX)
        binding.bottomView.lytPrescriptions.setVisibleIfPermission(PermissionKeys.POS_MANAGER)

    }

    private fun View.setVisibleIfPermission(permissionKey: String) {
        visibility = if (PermissionManager.hasPermission(permissionKey)) View.VISIBLE else View.GONE
    }

    private fun manageHomeIcon() {

        binding.bottomView.lytMedHide!!.visibility = View.GONE

        binding.bottomView.imgHomeSelected.visibility = View.VISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doc_home_selected)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavHome.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_rx)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)


    }

    private fun manageMedIcon() {

        binding.bottomView.lytMedHide!!.visibility = View.GONE

        binding.bottomView.imgMedSelected.visibility = View.VISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med_selected)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_rx)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

    }

    private fun managePresIcon() {

        binding.bottomView.lytMedHide!!.visibility = View.GONE

        binding.bottomView.imgPresSelected.visibility = View.VISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_rx_selected)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavPres.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)


    }

    private fun manageSettingsIcon() {

        binding.bottomView.lytMedHide!!.visibility = View.GONE

        binding.bottomView.imgSettingsSelected.visibility = View.VISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings_selected)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_rx)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

    }

    //tab click setup
    private fun manageTabHomeIcon() {

        binding.bottomView.lytMedHide.visibility = View.GONE
        binding.bottomView.imgPosArrow.rotation = 270f
        binding.bottomView.lytReportHide.visibility = View.GONE
        binding.bottomView.imgReportArrowNew.rotation = 270f

        binding.bottomView.lytHome.setBackgroundResource(R.drawable.home_selection_bg)
        binding.bottomView.imgHomeSelected.visibility = View.VISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doc_home_selected)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavHome.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)


        binding.bottomView.lytPrescriptionsNew.background = null
        binding.bottomView.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytReportNew.background = null
        binding.bottomView.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navReportNew?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgPosArrow.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytMed.background = null
        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptions.background = null
        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytSettings.background = null
        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytLogout?.background = null
        binding.bottomView.imgLogoutSelected?.visibility = View.INVISIBLE
        binding.bottomView.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.bottomView.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytReport?.background = null
        binding.bottomView.imgReportSelected?.visibility = View.INVISIBLE
        binding.bottomView.navReport?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

    }

    private fun manageTabReportNewIcon() {

        if (binding.bottomView.lytReportHide!!.visibility == View.VISIBLE) {
            binding.bottomView.lytReportHide!!.visibility = View.GONE
            binding.bottomView.imgReportArrowNew!!.rotation = 270f
        } else {
            binding.bottomView.imgReportArrowNew!!.rotation = 0f
            binding.bottomView.lytReportHide!!.visibility = View.VISIBLE
        }
        binding.bottomView.imgReportArrowNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A61C5C"))
        binding.bottomView.lytReportNew.setBackgroundResource(R.drawable.home_selection_bg)
        binding.bottomView.imgReportSelectedNew.visibility = View.VISIBLE
        binding.bottomView.navReportNew.setImageResource(R.drawable.nav_rx_selected)
        binding.bottomView.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A42161"))
        binding.bottomView.txtNavReportNew.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.lytHome.background = null
        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptions.background = null
        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptionsNew.background = null
        binding.bottomView.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytSettings.background = null
        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytLogout?.background = null
        binding.bottomView.imgLogoutSelected?.visibility = View.INVISIBLE
        binding.bottomView.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.bottomView.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytReport?.background = null
        binding.bottomView.imgReportSelected?.visibility = View.INVISIBLE
        binding.bottomView.navReport?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun manageSideMenuMedIcon() {

        if (binding.sideMenu.lytMedHide!!.visibility == View.VISIBLE) {
            binding.sideMenu.lytMedHide!!.visibility = View.GONE
            binding.sideMenu.imgPosArrow!!.rotation = 270f
        } else {
            binding.sideMenu.imgPosArrow!!.rotation = 0f
            binding.sideMenu.lytMedHide!!.visibility = View.VISIBLE
        }

        binding.sideMenu.lytReportHide.visibility = View.GONE
        binding.sideMenu.imgReportArrowNew.rotation = 270f

        binding.sideMenu.imgPosArrow?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A61C5C"))
        binding.sideMenu.lytMed.setBackgroundResource(R.drawable.home_selection_bg)
        binding.sideMenu.imgMedSelected.visibility = View.VISIBLE
        binding.sideMenu.navMed.setImageResource(R.drawable.nav_med_selected)
        binding.sideMenu.txtNavMed.setTextColor(Color.parseColor("#A42161"))
        binding.sideMenu.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.sideMenu.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideMenu.lytReportNew.background = null
        binding.sideMenu.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.sideMenu.navReportNew?.setImageResource(R.drawable.nav_rx)
        binding.sideMenu.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytHome.background = null
        binding.sideMenu.imgHomeSelected.visibility = View.INVISIBLE
        binding.sideMenu.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.sideMenu.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytPrescriptions.background = null
        binding.sideMenu.imgPresSelected.visibility = View.INVISIBLE
        binding.sideMenu.navPres.setImageResource(R.drawable.nav_pres)
        binding.sideMenu.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytPrescriptionsNew.background = null
        binding.sideMenu.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.sideMenu.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.sideMenu.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytSettings.background = null
        binding.sideMenu.imgSettingsSelected.visibility = View.INVISIBLE
        binding.sideMenu.navSettings.setImageResource(R.drawable.nav_settings)
        binding.sideMenu.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytLogout?.background = null
        binding.sideMenu.imgLogoutSelected?.visibility = View.INVISIBLE
        binding.sideMenu.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.sideMenu.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavLogout?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.sideMenu.lytReport?.background = null
        binding.sideMenu.imgReportSelected?.visibility = View.INVISIBLE
        binding.sideMenu.navReport?.setImageResource(R.drawable.nav_rx)
        binding.sideMenu.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavReport?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.sideMenu.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun manageTabMedIcon() {

        if (binding.bottomView.lytMedHide!!.visibility == View.VISIBLE) {
            binding.bottomView.lytMedHide!!.visibility = View.GONE
            binding.bottomView.imgPosArrow!!.rotation = 270f
        } else {
            binding.bottomView.imgPosArrow!!.rotation = 0f
            binding.bottomView.lytMedHide!!.visibility = View.VISIBLE
        }

        binding.bottomView.lytReportHide.visibility = View.GONE
        binding.bottomView.imgReportArrowNew.rotation = 270f

        binding.bottomView.imgPosArrow?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A61C5C"))
        binding.bottomView.lytMed.setBackgroundResource(R.drawable.home_selection_bg)
        binding.bottomView.imgMedSelected.visibility = View.VISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med_selected)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytReportNew.background = null
        binding.bottomView.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navReportNew?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytHome.background = null
        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptions.background = null
        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptionsNew.background = null
        binding.bottomView.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytSettings.background = null
        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytLogout?.background = null
        binding.bottomView.imgLogoutSelected?.visibility = View.INVISIBLE
        binding.bottomView.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.bottomView.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytReport?.background = null
        binding.bottomView.imgReportSelected?.visibility = View.INVISIBLE
        binding.bottomView.navReport?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun manageTabPresNewIcon() {

        binding.bottomView.imgPosArrow!!.rotation = 270f
        binding.bottomView.lytMedHide!!.visibility = View.GONE
        binding.bottomView.lytReportHide.visibility = View.GONE
        binding.bottomView.imgReportArrowNew.rotation = 270f

        binding.bottomView.lytPrescriptionsNew.setBackgroundResource(R.drawable.home_selection_bg)
        binding.bottomView.imgPresSelectedNew.visibility = View.VISIBLE
        binding.bottomView.navPresNew.setImageResource(R.drawable.nav_prescription)
        binding.bottomView.txtNavPresNew.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytReportNew.background = null
        binding.bottomView.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navReportNew?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptions.background = null
        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytHome.background = null
        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgPosArrow?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytMed.background = null
        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytSettings.background = null
        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytLogout?.background = null
        binding.bottomView.imgLogoutSelected?.visibility = View.INVISIBLE
        binding.bottomView.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.bottomView.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytReport?.background = null
        binding.bottomView.imgReportSelected?.visibility = View.INVISIBLE
        binding.bottomView.navReport?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun manageTabPresIcon() {

        binding.bottomView.imgPosArrow!!.rotation = 270f
        binding.bottomView.lytMedHide!!.visibility = View.GONE
        binding.bottomView.lytReportHide.visibility = View.GONE
        binding.bottomView.imgReportArrowNew.rotation = 270f

        binding.bottomView.lytPrescriptions.setBackgroundResource(R.drawable.home_selection_bg)
        binding.bottomView.imgPresSelected.visibility = View.VISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_prescription)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavPres.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytReportNew.background = null
        binding.bottomView.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navReportNew?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptionsNew.background = null
        binding.bottomView.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytHome.background = null
        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgPosArrow?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytMed.background = null
        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytSettings.background = null
        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytLogout?.background = null
        binding.bottomView.imgLogoutSelected?.visibility = View.INVISIBLE
        binding.bottomView.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.bottomView.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytReport?.background = null
        binding.bottomView.imgReportSelected?.visibility = View.INVISIBLE
        binding.bottomView.navReport?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun manageTabSettingsIcon() {

        binding.bottomView.imgPosArrow!!.rotation = 270f
        binding.bottomView.lytMedHide!!.visibility = View.GONE
        binding.bottomView.lytReportHide.visibility = View.GONE
        binding.bottomView.imgReportArrowNew.rotation = 270f

        binding.bottomView.lytSettings.setBackgroundResource(R.drawable.home_selection_bg)
        binding.bottomView.imgSettingsSelected.visibility = View.VISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings_selected)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytReportNew.background = null
        binding.bottomView.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navReportNew?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptionsNew.background = null
        binding.bottomView.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytHome.background = null
        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgPosArrow?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytMed.background = null
        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptions.background = null
        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytLogout?.background = null
        binding.bottomView.imgLogoutSelected?.visibility = View.INVISIBLE
        binding.bottomView.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.bottomView.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytReport?.background = null
        binding.bottomView.imgReportSelected?.visibility = View.INVISIBLE
        binding.bottomView.navReport?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun manageTabLogoutIcon() {

        binding.bottomView.imgPosArrow!!.rotation = 270f
        binding.bottomView.lytMedHide!!.visibility = View.GONE
        binding.bottomView.lytReportHide.visibility = View.GONE
        binding.bottomView.imgReportArrowNew.rotation = 270f

        binding.bottomView.lytLogout?.setBackgroundResource(R.drawable.home_selection_bg)
        binding.bottomView.imgLogoutSelected?.visibility = View.VISIBLE
        binding.bottomView.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.bottomView.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A42161"))
        binding.bottomView.txtNavLogout?.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytReportNew.background = null
        binding.bottomView.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navReportNew?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytSettings.background = null
        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.lytPrescriptionsNew.background = null
        binding.bottomView.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytHome.background = null
        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgPosArrow?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytMed.background = null
        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptions.background = null
        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytReport?.background = null
        binding.bottomView.imgReportSelected?.visibility = View.INVISIBLE
        binding.bottomView.navReport?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)
    }

    private fun manageTabReportIcon() {

        binding.bottomView.imgPosArrow!!.rotation = 270f
        binding.bottomView.lytMedHide!!.visibility = View.GONE
        binding.bottomView.lytReportHide.visibility = View.GONE
        binding.bottomView.imgReportArrowNew.rotation = 270f

        binding.bottomView.lytReport?.setBackgroundResource(R.drawable.home_selection_bg)
        binding.bottomView.imgReportSelected?.visibility = View.VISIBLE
        binding.bottomView.navReport?.setImageResource(R.drawable.nav_rx_selected)
        binding.bottomView.navReport?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#A42161"))
        binding.bottomView.txtNavReport?.setTextColor(Color.parseColor("#A42161"))
        binding.bottomView.txtNavReport?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgReportArrowNew.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytReportNew.background = null
        binding.bottomView.imgReportSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navReportNew?.setImageResource(R.drawable.nav_rx)
        binding.bottomView.navReportNew?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavReportNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptionsNew.background = null
        binding.bottomView.imgPresSelectedNew.visibility = View.INVISIBLE
        binding.bottomView.navPresNew.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPresNew.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPresNew.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytLogout?.background = null
        binding.bottomView.imgLogoutSelected?.visibility = View.INVISIBLE
        binding.bottomView.navLogout?.setImageResource(R.drawable.log_out_icon)
        binding.bottomView.navLogout?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavLogout?.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytSettings.background = null
        binding.bottomView.imgSettingsSelected.visibility = View.INVISIBLE
        binding.bottomView.navSettings.setImageResource(R.drawable.nav_settings)
        binding.bottomView.txtNavSettings.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavSettings.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.bottomView.lytHome.background = null
        binding.bottomView.imgHomeSelected.visibility = View.INVISIBLE
        binding.bottomView.navHome.setImageResource(R.drawable.nav_doctor_home)
        binding.bottomView.txtNavHome.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavHome.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.imgPosArrow?.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
        binding.bottomView.lytMed.background = null
        binding.bottomView.imgMedSelected.visibility = View.INVISIBLE
        binding.bottomView.navMed.setImageResource(R.drawable.nav_med)
        binding.bottomView.txtNavMed.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavMed.typeface = ResourcesCompat.getFont(this, R.font.roboto_regular)

        binding.bottomView.lytPrescriptions.background = null
        binding.bottomView.imgPresSelected.visibility = View.INVISIBLE
        binding.bottomView.navPres.setImageResource(R.drawable.nav_pres)
        binding.bottomView.txtNavPres.setTextColor(Color.parseColor("#9E9E9E"))
        binding.bottomView.txtNavPres.typeface =
            ResourcesCompat.getFont(this, R.font.roboto_regular)

    }


    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

    }
}