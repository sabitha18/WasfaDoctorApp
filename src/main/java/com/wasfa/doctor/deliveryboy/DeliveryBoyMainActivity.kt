package com.wasfa.doctor.deliveryboy

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ActivityDeliveruBoyHomeBinding

class DeliveryBoyMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeliveruBoyHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        binding = ActivityDeliveruBoyHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNav()
    }
    private fun setNav() {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home, R.id.nav_sale, R.id.nav_account -> {
                    navView.visibility = View.VISIBLE
                }

                else -> {
                    navView.visibility = View.GONE
                }
            }
            when (destination.id) {
                R.id.nav_home -> binding.navView.menu.findItem(R.id.nav_home).isChecked = true
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
            R.id.nav_sale -> binding.navView.menu.findItem(R.id.nav_sale).isChecked = true
            R.id.nav_account -> binding.navView.menu.findItem(R.id.nav_account).isChecked = true
        }
    }
}