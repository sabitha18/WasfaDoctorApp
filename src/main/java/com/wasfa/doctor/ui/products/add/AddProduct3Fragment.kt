package com.wasfa.doctor.ui.products.add

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.wasfa.doctor.databinding.FragmentAddproduct3Binding
import com.wasfa.doctor.helper.AppPreferences
import com.wasfa.doctor.ui.images.ImageListActivity
import com.wasfa.doctor.ui.products.adapter.SelectedImageListAdapter


class AddProduct3Fragment : Fragment() {

    private var _binding: FragmentAddproduct3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddproduct3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClick()
        manageText()
        manageTextThumb()
        manageImageSelected()
        manageThumb()

    }

    private fun manageThumb() {
        val product = AppPreferences.getInstance(requireContext()).getAddProduct()

        val thumbId = product?.thumbId

        if (!thumbId.isNullOrEmpty()) {
            binding.lytThumb.visibility = View.VISIBLE
            val sizeInMB = product?.thumbSize?.toDoubleOrNull()?.let {
                String.format("%.2f MB", it / (1024 * 1024))
            } ?: "0 MB"

            binding.txtImageSize.text = sizeInMB
            binding.txtImageName.text = product?.thumbUrl!!.substringAfterLast("/")

            Glide.with(requireContext())
                .load(product?.thumbUrl)
                .into(binding.imgList)

        } else {
            binding.lytThumb.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()
        manageImageSelected()
        manageThumb()
    }

    private fun manageImageSelected() {
        val product = AppPreferences.getInstance(requireContext()).getAddProduct()
        val selectedPhotos = product?.photos?.toMutableList() ?: mutableListOf()


        if (selectedPhotos.isNotEmpty()) {
            lateinit var adapter: SelectedImageListAdapter
             adapter = SelectedImageListAdapter(selectedPhotos)
            { photo, position ->
                selectedPhotos.removeAt(position)
                adapter.notifyItemRemoved(position)

                //  update product list
                val appPref = AppPreferences.getInstance(requireContext())
                val product = appPref.getAddProduct()

                if (product != null) {
                    val updatedProduct = product.copy(photos = selectedPhotos)
                    appPref.saveAddProduct(updatedProduct)
                }


                if (selectedPhotos.isEmpty()) {
                    binding.recyclerImages.visibility = View.GONE
                }else{
                    binding.recyclerImages.visibility = View.VISIBLE
                }
            }
            binding.recyclerImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerImages.adapter = adapter
        } else {

        }
    }


    private fun manageText() {

        val fullText = "Drop your image here, or browse files"
        val clickablePart = "browse files"

        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf(clickablePart)
        val endIndex = startIndex + clickablePart.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                AppPreferences.getInstance(requireContext()).saveImgSelectionStatus("multiple")
                val intent = Intent(requireContext(), ImageListActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.BLUE // Match your design
            }
        }

        spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.txtBrowse.text = spannable
        binding.txtBrowse.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun manageTextThumb() {

        val fullText = "Drop your image here, or browse files"
        val clickablePart = "browse files"

        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf(clickablePart)
        val endIndex = startIndex + clickablePart.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                AppPreferences.getInstance(requireContext()).saveImgStatus("thumbnail")
                AppPreferences.getInstance(requireContext()).saveImgSelectionStatus("single")
                val intent = Intent(requireContext(), ImageListActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.BLUE // Match your design
            }
        }

        spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.txtBrowseThumb.text = spannable
        binding.txtBrowseThumb.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun handleClick() {
        binding.btnDot.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.btnDot)

            popupMenu.menu.add("Delete")

            popupMenu.setOnMenuItemClickListener { item ->
                if (item.title == "Delete") {
                    val appPref = AppPreferences.getInstance(requireContext())
                    val product = appPref.getAddProduct()

                    if (product != null) {
                        product.thumbId = ""
                        product.thumbUrl = ""
                        product.thumbSize = ""
                        appPref.saveAddProduct(product)


                        binding.lytThumb.visibility = View.GONE
                    }
                    true
                } else {
                    false
                }
            }

            popupMenu.show()
        }


    }
}