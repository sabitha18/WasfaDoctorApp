package com.wasfa.doctor.ui.cart.summary

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.wasfa.doctor.databinding.FragmentCartRXSummaryBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.CartItem
import com.wasfa.doctor.network.response.PatientInfo
import com.wasfa.doctor.network.response.SubmitResponse
import com.wasfa.doctor.ui.cart.adapter.SummeryRXAdapter
import com.wasfa.doctor.ui.helper.PrescriptionPdfHelper
import com.wasfa.doctor.ui.main.MainActivity
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory
import java.io.File


class CartRXSummaryFragment : Fragment() {
    private var _binding: FragmentCartRXSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    var cust_id = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartRXSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        setViewModel()

    }

    override fun onResume() {
        super.onResume()

        val prefs = AppPreferences.getInstance(requireContext())
        if (prefs.getKey() == "true") {
            prefs.saveKey("false")
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
    private fun setViewModel() {
        val appPreferences = AppPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(requireContext())
        ).get(HomeViewModel::class.java)

        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                _binding = null
            }
        })
        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE

        }
        viewModel.deleteCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getCart(appPreferences.getToken().toString())

        }

        viewModel.submitStatus.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            generatePrescriptionPdf(data)


        }
        viewModel.submitSaveStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

        }
        viewModel.submitStatusFail.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE

        }


        viewModel.cartList.observe(viewLifecycleOwner) { data ->
            println("--------------------------")
            binding.progressBar.visibility = View.GONE

            manageCart(data?.cartItems)
            managePatient(data?.patientInfo)

        }

        binding.progressBar.visibility = View.VISIBLE
        viewModel.getCart(appPreferences.getToken().toString())

    }
    private fun generatePrescriptionPdf(data: SubmitResponse?) {
        Thread {
            val logoBitmap = try {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(data?.logo?.replace("\\/", "/"))
                    .submit(100, 80)
                    .get()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            val qrBitmap = svgToBitmap(data?.qrCode.toString())
            val pdfFile = PrescriptionPdfHelper.generatePdf(
                requireContext(),
                data?.prescriptionDetails,
                data?.patientInfo,
                data?.doctorInfo,
                logoBitmap,    // <-- Pass Bitmap here instead of String
                qrBitmap
            )

            pdfFile?.let {
                // Switch back to Main thread to open PDF
                requireActivity().runOnUiThread {
                    openPdf(it)
                }
            }
        }.start()
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



    private fun openPdf(file: File) {
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            AppPreferences.getInstance(requireContext()).saveKey("true")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }
    private fun managePatient(data: List<PatientInfo>?) {

        cust_id = data?.get(0)?.id.toString()
        binding.txtPatientName.text = data?.get(0)?.name ?: ""
        binding.txtPatientEmail.text = data?.get(0)?.email ?: ""
        binding.txtPatientPhone.text = data?.get(0)?.phone ?: ""
        binding.txtCivilId.text = data?.get(0)?.civil_id ?: ""
        binding.txtDob.text = data?.get(0)?.dob ?: ""

    }

    private fun manageCart(cartItems: List<CartItem>?) {
        binding.recyclerSummery.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val catAdapter = SummeryRXAdapter(
                cartItems
            ) { Cat, position ->

                binding.progressBar.visibility = View.VISIBLE
                val request = ApiService.CartRemoveRequest(
                    id = Cat?.id.toString()
                )
                viewModel.deleteCartShop(
                    AppPreferences.getInstance(requireContext()).getToken().toString(), request
                )
            }
            adapter = catAdapter
        }
    }

    private fun handleClick() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cardSend.setOnClickListener {
            callSubmitRxApi()
        }
        binding.cardProceed.setOnClickListener {
            callSubmitSaveRxApi()
        }
    }


    private fun callSubmitSaveRxApi() {
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.SubmitRXRequest(
            influencerId = "",
            customerId = cust_id,
            is_edit = "",
            submitWithCustomerCall = ""
        )
        viewModel.submitSaveRX(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }
    private fun callSubmitRxApi() {
        binding.progressBar.visibility = View.VISIBLE
        val request = ApiService.SubmitRXRequest(
            influencerId = "",
            customerId = cust_id,
            is_edit = "",
            submitWithCustomerCall = ""
        )
        viewModel.submitRX(
            AppPreferences.getInstance(requireContext()).getToken().toString(),
            request
        )
    }
}