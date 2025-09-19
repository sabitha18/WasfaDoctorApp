package com.wasfa.doctor.ui.cart

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ActivitySucessBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.ui.main.MainActivity

class SucessActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySucessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
        binding = ActivitySucessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageClick()
    }
    private fun openSaleDetails() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", R.id.nav_sale_details)
        }
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppPreferences.getInstance(this).saveOrderType("All Orders")
        openSaleDetails()
    }

    private fun manageClick() {
        binding.btnBack.setOnClickListener {
            AppPreferences.getInstance(this).saveOrderType("All Orders")
            openSaleDetails()
        }
    }

}