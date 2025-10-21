package com.wasfa.doctor.ui.pres

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfa.doctor.databinding.FragmentPresDetailsBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.network.ApiService
import com.wasfa.doctor.network.response.Med
import com.wasfa.doctor.network.response.PatientInfoDetails
import com.wasfa.doctor.ui.pres.adapter.MedicationListDetailsAdapter
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory


class PresDetailsFragment : Fragment() {
    private var _binding: FragmentPresDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPresDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageClick()
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

        }

        viewModel.presData.observe(viewLifecycleOwner) { data ->
            println("--------------------------")
            binding.progressBar.visibility = View.GONE

            manageCart(data?.get(0)?.medications)
            managePatient(data?.get(0)?.patientInfo)

        }

        val request = ApiService.PresDetailsRequest(
            id = appPreferences?.getPresID().toString()
        )
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getPresDetails(appPreferences.getToken().toString(),request)
    }

    private fun managePatient(patientInfo: PatientInfoDetails?) {

        fun safeText(text: String?): String {
            return text ?: ""
        }

        binding.txtPatientName.text = safeText(patientInfo?.name)
        binding.txtDob.text = safeText(patientInfo?.dob)
        binding.txtCivilId.text = safeText(patientInfo?.civil_id)
        binding.txtPhone.text = safeText(patientInfo?.phone)
        binding.txtGender.text = safeText(patientInfo?.gender)
        val nationality = patientInfo?.nationality
        binding.txtNationality.text = if (nationality == "0") "" else safeText(nationality)

        binding.txtGovernorate.text = safeText(patientInfo?.address?.getOrNull(0)?.governorateName)
        binding.txtArea.text = safeText(patientInfo?.address?.getOrNull(0)?.areaName)
        binding.txtBlock.text = safeText(patientInfo?.address?.getOrNull(0)?.block)
        binding.txtStreet.text = safeText(patientInfo?.address?.getOrNull(0)?.street)
        binding.txtBuilding.text = safeText(patientInfo?.address?.getOrNull(0)?.building)
        binding.txtFloor.text = safeText(patientInfo?.address?.getOrNull(0)?.floor)

        if (patientInfo?.address.isNullOrEmpty()) {
            binding.txtAddressDetails.visibility = View.GONE
            binding.cardAddress.visibility = View.GONE
        } else {
            binding.txtAddressDetails.visibility = View.VISIBLE
            binding.cardAddress.visibility = View.VISIBLE
        }
    }


    private fun manageCart(cartItems: List<Med>?) {
        binding.recyclerMed.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = MedicationListDetailsAdapter(cartItems) { data, type ->

            }
            adapter = catAdapter
        }
    }

    private fun manageClick() {
        binding.cardBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}