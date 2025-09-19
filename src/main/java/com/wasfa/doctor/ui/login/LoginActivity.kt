package com.wasfa.doctor.ui.login

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.ui.main.MainActivity
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ActivityLoginBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.deliveryboy.DeliveryBoyMainActivity
import com.wasfa.doctor.doctor.main.DoctorHomeActivity
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: HomeViewModel

    data class Prescription(
        val name: String,
        val qty: String,
        val dose: String,
        val time: String,
        val duration: String,
        val notes: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appPreferences = AppPreferences.getInstance(this)
        manageClick()
        checkButtonStatus()
        setupTextWatchers()
        setViewModel()


        if (appPreferences?.getRememberStatus().toString() == "true") {
            binding.editEmail.setText(appPreferences.getEmail())
            binding.editPass.setText(appPreferences.getPass())
            binding.cardReminderSelected.visibility = View.VISIBLE
        }


    }


    override fun onBackPressed() {
        super.onBackPressed()

        finishAffinity()

    }

    private fun setViewModel() {
        val appPreferences = AppPreferences.getInstance(this)
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(this@LoginActivity)
        ).get(HomeViewModel::class.java)

        viewModel.loadingState.observe(this@LoginActivity) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }
        viewModel.showAlertEvent.observe(this@LoginActivity) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }

        viewModel.loginDetails.observe(this@LoginActivity) { data ->
            binding.progressBar.visibility = View.GONE
            data?.userDetails?.permissions?.let { appPreferences.saveStaffPermissions(it) }
            if (appPreferences?.getRememberStatus().toString() == "true") {
                appPreferences?.saveEmail(binding.editEmail.text.toString())
                appPreferences?.savePass(binding.editPass.text.toString())
            }

            appPreferences.saveToken(data?.token)
            appPreferences.saveLoginType(data?.userDetails?.user_type)

            if (data?.userDetails?.user_type == "influencer") {
                val intent = Intent(this, DoctorHomeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
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

    private fun setupTextWatchers() {
        binding.editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                checkButtonStatus()
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        binding.editPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                checkButtonStatus()
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun checkButtonStatus() {
        if (binding.editEmail.text.isNullOrEmpty() || binding.editPass.text.isNullOrEmpty()) {
            binding.btnSignIn.isEnabled = false
            binding.btnSignIn.alpha = 0.5f
        } else {
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.alpha = 1f
        }
    }

    private fun manageClick() {
        val appPreferences = AppPreferences.getInstance(this)
        binding.imgHidePass.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.lytRemember.setOnClickListener {
            if (binding.cardReminderSelected.visibility == View.VISIBLE) {
                binding.cardReminderSelected.visibility = View.GONE
                appPreferences.saveRememberStatus("false")
            } else {
                binding.cardReminderSelected.visibility = View.VISIBLE
                appPreferences.saveRememberStatus("true")
            }
        }

        binding.btnSignIn.setOnClickListener {
            val request = ApiService.LoginRequest(
                email = binding.editEmail.text.toString(),
                password = binding.editPass.text.toString()
            )
            binding.progressBar.visibility = View.VISIBLE
            viewModel.userLogin(request)
        }
    }

    private fun togglePasswordVisibility() {
        if (binding.editPass.getTransformationMethod() == null) {
            binding.editPass.setTransformationMethod(PasswordTransformationMethod())
            binding.imgHidePass.setImageResource(R.drawable.view) // Set the image to show password
        } else {
            binding.editPass.setTransformationMethod(null)
            binding.imgHidePass.setImageResource(R.drawable.hide_icon) // Set the image to hide password
        }
        binding.editPass.text?.let { binding.editPass.setSelection(it.length) }
    }
}