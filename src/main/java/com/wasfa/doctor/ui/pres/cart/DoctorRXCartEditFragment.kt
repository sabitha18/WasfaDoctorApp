package com.wasfa.doctor.ui.pres.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentDoctorRxCartBinding
import com.wasfa.doctor.ui.pres.add.PresReviewFragment
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.PatientInfo
import com.wasfa.doctor.network.response.PresDetails
import com.wasfa.doctor.ui.cart.adapter.CartRXPOSDetaiilsNewAdapter
import com.wasfa.doctor.ui.pres.add.PresReviewEditFragment
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class DoctorRXCartEditFragment : Fragment() {
    private var _binding: FragmentDoctorRxCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private var patientInfo: List<PatientInfo>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoctorRxCartBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        setViewModel()
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
        viewModel.showAlertEvent.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            showAlertCustom(message)

        }
        viewModel.deleteCartShopStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.VISIBLE
            println("me =--------------")
            callApi()

        }
        viewModel.cartUpdateStatus.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            callApi()

        }

        viewModel.presRXNewData.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            manageCart(data?.prescriptionDetails)
            patientInfo = data?.patientInfo
        }
        binding.progressBar.visibility = View.VISIBLE
        callApi()

    }

    fun callApi(){
        val request = ApiService.EditPOSDetailsRequest(
            prescriptionId = AppPreferences.getInstance(requireContext()).getNewRXStatus().toString()
        )
        viewModel.getPresRXNewDetailsEdit(AppPreferences.getInstance(requireContext()).getToken().toString(),request)
    }
    fun showAlertCustom(message: String) {
        val builder = AlertDialog.Builder(requireContext())


        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.validation_alert, null)
        builder.setView(customLayout)
        val text_validation = customLayout.findViewById<TextView>(R.id.text_validation)
        text_validation.text = message
        val continueShoppingButton = customLayout.findViewById<MaterialCardView>(R.id.view_cart)
        lateinit var dialog: AlertDialog
        continueShoppingButton.setOnClickListener {
            if (message == "Internal Server Error") {
                findNavController().popBackStack()
                dialog?.dismiss()
            } else {
                dialog?.dismiss()
            }

        }

        dialog = builder.create()
        dialog.show()
    }

    private fun handleClick() {
        binding.cardProceed.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PresReviewEditFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.imgBack.setOnClickListener {
            val navController = runCatching { findNavController() }.getOrNull()
            if (navController?.currentDestination != null) {
                navController.popBackStack()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
    }


    private fun manageCart(cartItems: List<PresDetails>?) {

        if (cartItems.isNullOrEmpty()) {
            binding.txtNoData.visibility = View.VISIBLE
            binding.recyclerRxCart.visibility = View.GONE
        } else {
            binding.txtNoData.visibility = View.GONE
            binding.recyclerRxCart.visibility = View.VISIBLE
            binding.recyclerRxCart.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                val catAdapter = CartRXPOSDetaiilsNewAdapter(
                    cartItems,
                    { cart, count ->  //decrement
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = count,
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description.toString(),
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, count -> //increment
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = count,
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description.toString(),
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, id -> // delete
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.CartRemoveRequest(
                            id = id
                        )
                        viewModel.deleteCartShop(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, doseValue -> //dose

                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description ?: "",
                            course_duration = cart?.course_duration ?: "",
                            dose_day = doseValue
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, doseTime -> //dose time
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = doseTime,
                            course_day = cart?.course_day ?: "",
                            description = cart?.description ?: "",
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: ""
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, dayValue -> //dose day
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = dayValue,
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description ?: "",
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: ""
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, courseDurationValue -> //course duration
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = cart?.description ?: "",
                            course_duration = courseDurationValue,
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, courseDayValue -> //course day
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = courseDayValue,
                            description = cart?.description ?: "",
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { cart, notesValue -> //notes
                        binding.progressBar.visibility = View.VISIBLE
                        val request = ApiService.UpdateRXCartRequest(
                            id = cart?.id.toString(),
                            quantity = cart?.quantity.toString(),
                            dose = cart?.dose ?: "",
                            dose_time = cart?.dose_time ?: "",
                            course_day = cart?.course_day ?: "",
                            description = notesValue,
                            course_duration = cart?.course_duration ?: "",
                            dose_day = cart?.doseday ?: "",
                        )
                        viewModel.updateCartRx(
                            AppPreferences.getInstance(requireContext()).getToken().toString(),
                            request
                        )
                    },
                    { Cat, position ->

                    },
                )
                adapter = catAdapter
            }
        }

    }

}