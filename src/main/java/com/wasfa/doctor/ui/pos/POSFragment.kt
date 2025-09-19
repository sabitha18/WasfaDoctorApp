package com.wasfa.doctor.ui.pos

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentPOSBinding
import com.wasfa.doctor.ui.home.model.Cat
import com.wasfa.doctor.ui.pos.adapter.POSListAdapter

class POSFragment : Fragment() {
    private var _binding: FragmentPOSBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPOSBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        managePOS()
        createList()
        handleClick()
    }
    private fun handleClick() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun managePOS() {
        binding.recyclerPos.layoutAnimation =
            android.view.animation.AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        binding.recyclerPos.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = POSListAdapter(createList()) { data, position ->

                if (data.name == "POS RX") {

                    val isTablet = resources.getBoolean(R.bool.isTablet)
                    val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                    if (isTablet || isLandscape) {
                        findNavController().navigate(R.id.nav_tab_pos_rx_product)
                    } else {
                        findNavController().navigate(R.id.nav_pos_rx_product)
                    }
                }else if (data.name == "POS Shop"){

                    val isTablet = resources.getBoolean(R.bool.isTablet)
                    val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                    if (isTablet || isLandscape) {
                        findNavController().navigate(R.id.nav_tab_pos_shop_product)
                    } else {
                        findNavController().navigate(R.id.nav_pos_shop)
                    }
                }
            }
            adapter = catAdapter
        }
    }

    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "POS RX",
                R.drawable.dummy_image
            ),
            Cat(
                "POS Shop",
                R.drawable.dummy_image
            )
        )
    }
}