package com.wasfa.doctor.ui.products.brand

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentAddBrandBinding


class AddBrandActivity : AppCompatActivity() {
    private lateinit var binding: FragmentAddBrandBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        binding = FragmentAddBrandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleClick()
    }
    private fun handleClick() {

        binding.imgBack.setOnClickListener {
            finish()
        }
    }

}