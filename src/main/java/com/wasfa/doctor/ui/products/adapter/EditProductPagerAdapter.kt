package com.wasfa.doctor.ui.products.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wasfa.doctor.ui.products.add.AProductDescriptionFragment
import com.wasfa.doctor.ui.products.add.AProductVideoFragment
import com.wasfa.doctor.ui.products.add.AddProduct2Fragment
import com.wasfa.doctor.ui.products.add.AddProduct3Fragment
import com.wasfa.doctor.ui.products.add.AddProduct4Fragment
import com.wasfa.doctor.ui.products.add.AddProduct5Fragment
import com.wasfa.doctor.ui.products.add.AddProductFragment

class EditProductPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        AddProductFragment(),
        AddProduct2Fragment(),
        AddProduct3Fragment(),
        AProductDescriptionFragment(),
        AProductVideoFragment(),
        AddProduct4Fragment(),
        AddProduct5Fragment()
    )

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}
