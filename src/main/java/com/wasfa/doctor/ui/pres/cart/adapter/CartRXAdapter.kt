package com.wasfa.doctor.ui.pres.cart.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.RxCartItemBinding
import com.wasfa.doctor.network.response.CartItem

class CartRXAdapter(
    private val CatList: List<CartItem>?,
    private val decrementListener: (CartItem, String) -> Unit,
    private val incrementListener: (CartItem, String) -> Unit,
    private val deleteListener: (CartItem, String) -> Unit,
    private val doseListener: (CartItem, String) -> Unit,
    private val doseTimeListener: (CartItem, String) -> Unit,
    private val doseDayListener: (CartItem, String) -> Unit,
    private val courseDurationListener: (CartItem, String) -> Unit,
    private val courseDayListener: (CartItem, String) -> Unit,
    private val notesListener: (CartItem, String) -> Unit,
    private val listener: (CartItem, Int) -> Unit,

    ) :
    RecyclerView.Adapter<CartRXAdapter.ViewHolder>() {
    private var notesRunnable: Runnable? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = RxCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            v,
            doseListener,
            doseDayListener,
            doseTimeListener,
            courseDayListener,
            courseDurationListener,
            notesListener,
            deleteListener,
            decrementListener,
            incrementListener,
            notesRunnable
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(CatList!![position])
        holder.itemView.setOnClickListener {
            listener(CatList!![position], position)
        }

        holder.itemBinding.editNotes.setOnClickListener {
            showNotes(holder.itemView.context,CatList!![position])
        }
    }

    private fun showNotes(context: Context, data: CartItem) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_show_notes, null)

        val imgClose = dialogView.findViewById<ImageView>(R.id.img_close)
        val editNotes = dialogView.findViewById<EditText>(R.id.edit_notes)
        val cardSave = dialogView.findViewById<MaterialCardView>(R.id.card_save)
       editNotes.setText( if (data.description == "null") "" else data.description)



        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        cardSave.setOnClickListener {
            notesListener(data,editNotes.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun getItemCount(): Int {
        return CatList!!.size
    }

    class ViewHolder(
        var itemBinding: RxCartItemBinding,
        private val doseListener: (CartItem, String) -> Unit,
        private val doseDayListener: (CartItem, String) -> Unit,
        private val doseTimeListener: (CartItem, String) -> Unit,
        private val courseDayListener: (CartItem, String) -> Unit,
        private val courseDurationListener: (CartItem, String) -> Unit,
        private val notesListener: (CartItem, String) -> Unit,
        private val deleteListener: (CartItem, String) -> Unit,
        private val decrementListener: (CartItem, String) -> Unit,
        private val incrementListener: (CartItem, String) -> Unit,
        private var notesRunnable: Runnable?
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(data: CartItem) {
            var count = data.quantity ?: 1

            fun updateCountUI() {
                itemBinding.txtCount.text = count.toString()
                itemBinding.cardDecreaseCount.isEnabled = count > 1
                itemBinding.cardDecreaseCount.alpha = if (count > 1) 1.0f else 0.5f
            }

            updateCountUI()

            itemBinding.cardDecreaseCount.setOnClickListener {
                if (count > 1) {
                    count--
                    updateCountUI()
                    decrementListener(data, count.toString())
                }
            }
            itemBinding.cardIncreaseCount.setOnClickListener {
                count++
                updateCountUI()
                incrementListener(data, count.toString())
            }
            itemBinding.lytRemove.setOnClickListener {
                deleteListener(data, data?.id.toString())
            }
            itemBinding.cardCourseDuration.setOnClickListener {
                showCourseDurationDropdown(data)
            }

            itemBinding.cardDose.setOnClickListener {
                showDoseDropdown(data)
            }
            itemBinding.cardDoseDay.setOnClickListener {
                showDoseDayDropdown(data)
            }
            itemBinding.cardDoseTime.setOnClickListener {
                showDoseTimeDropdown(data)
            }
            itemBinding.cardCourseDay.setOnClickListener {
                showCourseDayDropdown(data)
            }
            Glide.with(itemBinding.root.context)
                .load(data?.productThumbnailImage)
                .into(itemBinding.imdProduct)

            itemBinding.txtCount.text = data?.quantity.toString()

            setTextOrHint(itemBinding.txtSelectedDose, data?.doseday)
            itemBinding.txtPdName.text = data?.productName
            itemBinding.txtPrize.text = data?.unitPrice.toString()
            itemBinding.editNotes.text =
                if (data?.description.isNullOrBlank() || data.description == "null") "" else data.description

            setTextOrHint(itemBinding.txtDoseSelectedDay, data?.dose)
            setTextOrHint(itemBinding.txtDoseTime, data?.dose_time)
            setTextOrHint(itemBinding.txtCourseDay, data?.course_day)
            setTextOrHint(itemBinding.txtCourseDuration, data?.course_duration)


        }
        fun setTextOrHint(view: TextView, value: String?) {
            if (value.isNullOrEmpty()) {
                view.text = ""
                view.hint = "Select"
            } else {
                view.text = value
            }
        }
        private fun showDoseDropdown(cartItem: CartItem) {
            val doses = listOf("Daily", "Weekly", "Monthly")
            val popup = android.widget.PopupMenu(itemView.context, itemBinding.cardDose)
            doses.forEachIndexed { index, dose ->
                popup.menu.add(0, index, index, dose)
            }

            popup.setOnMenuItemClickListener { menuItem ->
                val selectedDose = doses[menuItem.itemId]

                itemBinding.txtSelectedDose.text = selectedDose
                doseListener(cartItem, selectedDose)
                true
            }
            popup.show()
        }

        private fun showDoseDayDropdown(cartItem: CartItem) {
            val doses = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            val popup = android.widget.PopupMenu(itemView.context, itemBinding.cardDoseDay)
            doses.forEachIndexed { index, dose ->
                popup.menu.add(0, index, index, dose)
            }

            popup.setOnMenuItemClickListener { menuItem ->
                val selectedDose = doses[menuItem.itemId]

                itemBinding.txtDoseSelectedDay.text = selectedDose
                doseDayListener(cartItem, selectedDose)
                true
            }
            popup.show()
        }

        private fun showDoseTimeDropdown(cartItem: CartItem) {
            val doses = listOf("Before Meal", "After Meal", "Morning", "Night", "Any Time")
            val popup = android.widget.PopupMenu(itemView.context, itemBinding.cardDoseTime)
            doses.forEachIndexed { index, dose ->
                popup.menu.add(0, index, index, dose)
            }

            popup.setOnMenuItemClickListener { menuItem ->
                val selectedDose = doses[menuItem.itemId]

                itemBinding.txtDoseTime.text = selectedDose
                doseTimeListener(cartItem, selectedDose)
                true
            }
            popup.show()
        }

        private fun showCourseDayDropdown(cartItem: CartItem) {
            val doses = listOf("Day", "Week", "Month")
            val popup = android.widget.PopupMenu(itemView.context, itemBinding.cardCourseDay)
            doses.forEachIndexed { index, dose ->
                popup.menu.add(0, index, index, dose)
            }

            popup.setOnMenuItemClickListener { menuItem ->
                val selectedDose = doses[menuItem.itemId]

                itemBinding.txtCourseDay.text = selectedDose
                courseDayListener(cartItem, selectedDose)
                true
            }
            popup.show()
        }

        private fun showCourseDurationDropdown(cartItem: CartItem) {
            val doses = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            val popup = android.widget.PopupMenu(itemView.context, itemBinding.cardCourseDuration)
            doses.forEachIndexed { index, dose ->
                popup.menu.add(0, index, index, dose)
            }

            popup.setOnMenuItemClickListener { menuItem ->
                val selectedDose = doses[menuItem.itemId]

                itemBinding.txtCourseDuration.text = selectedDose
                courseDurationListener(cartItem, selectedDose)
                true
            }
            popup.show()
        }
    }
}