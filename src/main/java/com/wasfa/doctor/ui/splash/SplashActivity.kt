package com.wasfa.doctor.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.wasfa.doctor.databinding.ActivitySplashBinding
import com.wasfa.doctor.R
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.response.AddressItem
import com.wasfa.doctor.network.response.DoctorInfo
import com.wasfa.doctor.network.response.PatientInfo
import com.wasfa.doctor.network.response.PresDetails
import com.wasfa.doctor.network.response.SubmitResponse
import com.wasfa.doctor.ui.helper.PdfViewerFragment
import com.wasfa.doctor.ui.helper.PrescriptionPdfHelper
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
//        val dummyData = getDummySubmitResponse()
//        generatePrescriptionPdf(dummyData)

    }
    private fun svgToBitmap(svgString: String, width: Int = 200, height: Int = 200): Bitmap? {
        return try {
            val svg = SVG.getFromString(svgString)
            svg.setDocumentWidth("200px")
            svg.setDocumentHeight("200px")
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            svg.renderToCanvas(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generatePrescriptionPdf(data: SubmitResponse?) {
        Thread {
            val logoBitmap = try {
                BitmapFactory.decodeResource(resources, R.drawable.wasfa_logo)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            val qrBitmap = svgToBitmap(data?.qrCode.toString())
            val pdfFile = PrescriptionPdfHelper.generatePdf(
               this,
                data?.prescriptionDetails,
                data?.patientInfo,
                data?.doctorInfo,
                logoBitmap,    // <-- Pass Bitmap here instead of String
                qrBitmap,
                data?.clinicName,
                data?.designation,
                data?.id
            )

            pdfFile?.let {
                runOnUiThread {
                    val bundle = Bundle().apply {
                        putString("pdfPath", it.absolutePath)
                    }

                    val fragment = PdfViewerFragment().apply { arguments = bundle }

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }

        }.start()
    }
    private fun getDummySubmitResponse(): SubmitResponse {
        return SubmitResponse(
            id = "123",
            logo = "", // you can put a sample URL if you want to load an image
            qrCode = "https://dummy-qr",
            clinicName = "",
            designation = "",
            doctorInfo = listOf(
                DoctorInfo(
                    id = "1",
                    name = "Dr. Sabitha",
                    type = "General Physician",
                    email = "doctor@example.com",
                    phone = "9876543210",
                    civilId = "CID123",
                    dob = "1990-01-01",
                    alternateNumber = "9876500000",
                    profilePic = ""
                )
            ),
            patientInfo = listOf(
                PatientInfo(
                    id = "101",
                    name = "John Doe",
                    email = "john@example.com",
                    phone = "9000000000",
                    civilId = "CIV998",
                    dob = "1995-05-20",
                    profilePic = "",
                    gender = "Male",
                    nationality = "Indian",
                    civil_id = "12152585",
                    alt_phone = "",
                    address = emptyList()
                )
            ),
            prescriptionDetails = listOf(
                PresDetails(
                    id = "item1",
                    productId = "PRD100",
                    productName = "Paracetamol 500mg",
                    productThumbnailImage = "",
                    variation = "Tablet",
                    price = "1.5",
                    unitPrice = "1.5",
                    actualPrice = "2.0",
                    currencySymbol = "KD",
                    doseday = "1",
                    dose = "1",
                    dose_time = "After Food",
                    course_day = "3",
                    course_duration = "3",
                    description = "Take only if fever is above 100F",
                    quantity = "10"
                )
            )
        )
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