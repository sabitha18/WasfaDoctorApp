package com.wasfa.doctor.ui.customers.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.ItemCustomersBinding
import com.wasfa.doctor.helper.PermissionKeys
import com.wasfa.doctor.helper.PermissionManager
import com.wasfa.doctor.network.response.TypeResponse
import com.wasfa.doctor.network.response.UserDetails

class CustomerListAdapter(
    private val data: MutableList<UserDetails>,
    private var typeList: List<TypeResponse>,
    private var segmentList: List<TypeResponse>,
    private val listener: (UserDetails, String) -> Unit
) :
    RecyclerView.Adapter<CustomerListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemCustomersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data!![position],typeList,segmentList)

        holder.itemView.setOnClickListener{
            listener(data!![position],"view")
        }
        holder.itemBinding.imgBan.setOnClickListener {
            listener(data!![position],"ban")
        }
        holder.itemBinding.imgDelete.setOnClickListener {
            listener(data!![position],"delete")
        }
        holder.itemBinding.imgEdit.setOnClickListener {
            listener(data!![position],"edit")
        }
        holder.itemBinding.cardSegment.setOnClickListener {
            listener(data!![position],"segment")
        }
        holder.itemBinding.cardSelectType.setOnClickListener {
            listener(data!![position],"type")
        }

        holder.itemBinding.imgBan.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.BAN_CUSTOMER)) View.VISIBLE else View.INVISIBLE
        holder.itemBinding.imgDelete.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.DELETE_CUSTOMER)) View.VISIBLE else View.INVISIBLE
        holder.itemBinding.imgEdit.visibility =
            if (PermissionManager.hasPermission(PermissionKeys.EDIT_CUSTOMERS)) View.VISIBLE else View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(var itemBinding: ItemCustomersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(
            data: UserDetails,
            typeList: List<TypeResponse>,
            segmentList: List<TypeResponse>
        ) {

            itemBinding.custId.text = "CUST"+data?.id
            itemBinding.custName.text = data?.name
            itemBinding.custNumber.text = data?.phone
            itemBinding.custEmail.text = data?.email
            itemBinding.custAddress.text = ""
            itemBinding.createdBy.text = data?.createdBy
            itemBinding.createdFrom.text = data?.createdOn


            if (data?.segment.isNullOrEmpty()){

            }else{
                itemBinding.txtSegment.text = data?.segment
            }

            if (data?.type.isNullOrEmpty()){

            }else{
                itemBinding.txtSelectType.text = data?.type
            }


            if (data?.banned == "1") {
                itemBinding.imgBanIcon.setColorFilter(
                    ContextCompat.getColor(itemBinding.root.context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                itemBinding.imgBanIcon.clearColorFilter()
            }


        }
    }
    fun setProducts(newProducts: List<UserDetails>) {
        data.clear()
        data.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun addProducts(newProducts: List<UserDetails>) {
        val startPosition = data.size
        data.addAll(newProducts)
        notifyItemRangeInserted(startPosition, newProducts.size)
    }


}