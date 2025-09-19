package com.wasfa.doctor.ui.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentFilterBinding
import com.wasfa.doctor.ui.filter.adapter.FilterDeliveryStaffAdapter
import com.wasfa.doctor.ui.filter.adapter.FilterNothingSelectedAdapter
import com.wasfa.doctor.ui.home.model.Cat


class FilterFragment : Fragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    } override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        manageDeliveryStaff()
        manageNothingSelected()
        managePaymentStatus()
        manageFilterByDate()
        manageFilterByPaymentChange()
        manageFilterByPaymentMethod()
    }

    private fun manageFilterByPaymentMethod() {
        binding.recyclerFilterByPM.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createPC()) { data, position ->

                binding.txtFilterPM.text = data?.name
                closeFilterByPM()
            }
            adapter = catAdapter
        }
    }

    private fun closeFilterByPM() {
        binding.imgFilterPM.rotation = 0f
        binding.cardFilterByPMHide.visibility = View.GONE
    }

    private fun manageFilterByPaymentChange() {
        binding.recyclerFilterByPC.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createPC()) { data, position ->

                binding.txtFilterByPc.text = data?.name
                closeFilterByPC()
            }
            adapter = catAdapter
        }
    }

    private fun closeFilterByPC() {
        binding.imgFilterByPc.rotation = 0f
        binding.cardFilterByPCHide.visibility = View.GONE
    }

    private fun manageFilterByDate() {
        binding.recyclerFilterByDate.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createPD()) { data, position ->

                binding.txtFilterByDate.text = data?.name
                closeFilterByDate()
            }
            adapter = catAdapter
        }
    }

    private fun closeFilterByDate() {
        binding.imgFilterByDateArrow.rotation = 0f
        binding.cardFilterByDateHide.visibility = View.GONE
    }

    private fun managePaymentStatus() {
        binding.recyclerPaymentStatus.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createPS()) { data, position ->

                binding.txtPaymentStatus.text = data?.name
                closePaymentStatus()
            }
            adapter = catAdapter
        }
    }

    private fun closePaymentStatus() {
        binding.imgPaymentStatusArrow.rotation = 0f
        binding.cardFilterPaymentStatusHide.visibility = View.GONE
    }

    private fun manageNothingSelected() {

        binding.recyclerNothingSelected.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterNothingSelectedAdapter(createListNothing()) { data, position ->

                binding.txtFilterNothingSelected.text = data?.name
                closeNothingSelected()
            }
            adapter = catAdapter
        }

    }

    private fun closeNothingSelected() {
        binding.imgNothingSelectedArrow.rotation = 0f
        binding.cardNothingSelectedHide.visibility = View.GONE
    }

    private fun manageDeliveryStaff() {
        binding.recyclerDeliveryStaff.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val catAdapter = FilterDeliveryStaffAdapter(createList()) { data, position ->

                binding.txtFilterDeliveryStaff.text = data?.name
                closeDeliveryStaff()
            }
            adapter = catAdapter
        }
    }

    private fun closeDeliveryStaff() {
        binding.imgDeliveryStaffArrow.rotation = 0f
        binding.cardDeliveryStaffHide.visibility = View.GONE
    }

    private fun createList(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Aldawayea",
                R.drawable.dummy_image
            ),
            Cat(
                "Apix Delivery Boy",
                R.drawable.dummy_image
            ),
            Cat(
                "ffdffg",
                R.drawable.dummy_image
            ),
            Cat(
                "fdgdgd",
                R.drawable.dummy_image
            )
        )
    }
    private fun createListNothing(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Filter By Delivery",
                R.drawable.dummy_image
            ),
            Cat(
                "Pending",
                R.drawable.dummy_image
            ),
            Cat(
                "Confirmed",
                R.drawable.dummy_image
            ),
            Cat(
                "Picked Up",
                R.drawable.dummy_image
            ),
            Cat(
                "On The Way",
                R.drawable.dummy_image
            )
        )
    }
    private fun createPS(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Un-Paid",
                R.drawable.dummy_image
            ),
            Cat(
                "Paid",
                R.drawable.dummy_image
            ),
            Cat(
                "Processed",
                R.drawable.dummy_image
            )
        )
    }
    private fun createPC(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Knet",
                R.drawable.dummy_image
            ),
            Cat(
                "Quick Pay",
                R.drawable.dummy_image
            ),
            Cat(
                "Go Tap",
                R.drawable.dummy_image
            ),
            Cat(
                "COD",
                R.drawable.dummy_image
            )
        )
    }
    private fun createPD(): ArrayList<Cat> {
        return arrayListOf<Cat>(
            Cat(
                "Today",
                R.drawable.dummy_image
            ),
            Cat(
                "Yesterday",
                R.drawable.dummy_image
            ),
            Cat(
                "Last 7 Days",
                R.drawable.dummy_image
            ),
            Cat(
                "Last 30 Days",
                R.drawable.dummy_image
            ),
            Cat(
                "This Month",
                R.drawable.dummy_image
            ),
            Cat(
                "Last Month",
                R.drawable.dummy_image
            ),
            Cat(
                "Custom Range",
                R.drawable.dummy_image
            )
        )
    }
    private fun handleClick() {
        binding.cardApplyFilter.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imgClose.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cardFilterByDate.setOnClickListener {
            if (binding.imgFilterByDateArrow.rotation == 0f) {
                binding.imgFilterByDateArrow.rotation = 180f
                binding.cardFilterByDateHide.visibility = View.VISIBLE
            } else {
                binding.imgFilterByDateArrow.rotation = 0f
                binding.cardFilterByDateHide.visibility = View.GONE
            }
        }
        binding.cardFilterPaymentStatus.setOnClickListener {
            if (binding.imgPaymentStatusArrow.rotation == 0f) {
                binding.imgPaymentStatusArrow.rotation = 180f
                binding.cardFilterPaymentStatusHide.visibility = View.VISIBLE
            } else {
                binding.imgPaymentStatusArrow.rotation = 0f
                binding.cardFilterPaymentStatusHide.visibility = View.GONE
            }
        }
        binding.cardNothingSelected.setOnClickListener {
            if (binding.imgNothingSelectedArrow.rotation == 0f) {
                binding.imgNothingSelectedArrow.rotation = 180f
                binding.cardNothingSelectedHide.visibility = View.VISIBLE
            } else {
                binding.imgNothingSelectedArrow.rotation = 0f
                binding.cardNothingSelectedHide.visibility = View.GONE
            }
        }

        binding.cardFilterDeliveryStaff.setOnClickListener {
            if (binding.imgDeliveryStaffArrow.rotation == 0f) {
                binding.imgDeliveryStaffArrow.rotation = 180f
                binding.cardDeliveryStaffHide.visibility = View.VISIBLE
            } else {
                binding.imgDeliveryStaffArrow.rotation = 0f
                binding.cardDeliveryStaffHide.visibility = View.GONE
            }
        }

        binding.cardFilterPaymentChange.setOnClickListener {
            if (binding.imgFilterByPc.rotation == 0f) {
                binding.imgFilterByPc.rotation = 180f
                binding.cardFilterByPCHide.visibility = View.VISIBLE
            } else {
                binding.imgFilterByPc.rotation = 0f
                binding.cardFilterByPCHide.visibility = View.GONE
            }
        }
        binding.cardFilterPaymentMethod.setOnClickListener {
            if (binding.imgFilterPM.rotation == 0f) {
                binding.imgFilterPM.rotation = 180f
                binding.cardFilterByPMHide.visibility = View.VISIBLE
            } else {
                binding.imgFilterPM.rotation = 0f
                binding.cardFilterByPMHide.visibility = View.GONE
            }
        }
    }

}