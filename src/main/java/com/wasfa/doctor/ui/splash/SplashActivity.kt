package com.wasfa.doctor.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.wasfa.doctor.databinding.ActivitySplashBinding
import com.wasfa.doctor.R
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.ui.main.DoctorHomeActivity
import com.wasfa.doctor.ui.login.LoginActivity


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        handleTimer()
    }

    private fun handleTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            manageNavigation()
        }, 2000)
    }

    private fun manageNavigation() {

        val appPreferences = AppPreferences.getInstance(this)

        if (appPreferences.getToken() == null){

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }else {

            if(appPreferences.getLoginType() == "influencer"){
                val intent = Intent(this, DoctorHomeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }

        }

    }


}