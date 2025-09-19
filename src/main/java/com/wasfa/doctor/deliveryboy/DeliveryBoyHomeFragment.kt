package com.wasfa.doctor.deliveryboy

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import com.wasfa.doctor.databinding.FragmentDeliveryBoyHomeBinding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.viewmodel.HomeViewModel
import com.wasfa.doctor.viewmodel.HomeViewModelFactory

class DeliveryBoyHomeFragment : Fragment() {
    private var _binding: FragmentDeliveryBoyHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeliveryBoyHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBgGradient()
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

        viewModel.homeDataDelivery.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE


            binding.txtAllOrderCount.text = data?.totalOrdersCount
            binding.txtPendingOrderCount.text = data?.pendingOrdersCount
            binding.txtDeliveredOrderCount.text = data?.deliveredOrdersCount

        }

        binding.progressBar.visibility = View.VISIBLE
        viewModel.getDeliveryBoyHome(appPreferences.getToken().toString())
    }

    private fun setBgGradient() {
        applyGradientBackground(
            binding.lytAllOrderCount,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF875FC0.toInt(), 0xFF5346BA.toInt())
        )

        applyGradientBackground(
            binding.lytPendingOrderCount,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFF47C5F4.toInt(), 0xFF6791D9.toInt())
        )

        applyGradientBackground(
            binding.lytDeliveredOrderCount,
            GradientDrawable.Orientation.RIGHT_LEFT,
            intArrayOf(0xFFEB4786.toInt(), 0xFFB854A6.toInt())
        )

    }

    private fun applyGradientBackground(view: View, orientation: GradientDrawable.Orientation, colors: IntArray) {
        val gradientDrawable = GradientDrawable(orientation, colors).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
        view.background = gradientDrawable
    }
}