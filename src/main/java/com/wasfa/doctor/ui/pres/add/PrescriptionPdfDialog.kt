package com.wasfa.doctor.ui.pres.add

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.wasfa.doctor.R
import java.io.File

class PrescriptionPdfDialog(
    private val pdfFile: File?,
    private val onSaveClicked: (() -> Unit)? = null // 👈 callback for fragment
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prescription_pdf_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pdfImageView = view.findViewById<ImageView>(R.id.pdfImageView)
        val btnSave = view.findViewById<Button>(R.id.btnSubmit)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        try {
            val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)

            if (pdfRenderer.pageCount > 0) {
                val page = pdfRenderer.openPage(0)
                val displayMetrics = resources.displayMetrics

                // Scale PDF to fit dialog width (90% of screen)
                val dialogWidth = displayMetrics.widthPixels * 0.9f
                val scale = dialogWidth / page.width.toFloat()
                val newWidth = (page.width * scale).toInt()
                val newHeight = (page.height * scale).toInt()

                val bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
                val matrix = Matrix().apply { postScale(scale, scale) }

                page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                pdfImageView.setImageBitmap(bitmap)

                page.close()
            }

            pdfRenderer.close()
            fileDescriptor.close()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to display PDF", Toast.LENGTH_SHORT).show()
        }
        btnCancel.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            pdfFile?.let { file ->
                try {
                    val context = requireContext()
                    val fileName = "Prescription_${System.currentTimeMillis()}.pdf"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10 and above
                        val resolver = context.contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                        }

                        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                        if (uri != null) {
                            resolver.openOutputStream(uri)?.use { outputStream ->
                                file.inputStream().use { inputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }

                            Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()
                            onSaveClicked?.invoke()
                            dismiss()
                        } else {
                            Toast.makeText(context, "Failed to create file in Downloads", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Android 9 and below
                        val downloadsDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val outFile = File(downloadsDir, fileName)
                        file.copyTo(outFile, overwrite = true)

                        onSaveClicked?.invoke()
                        dismiss()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed to save PDF", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(requireContext(), "PDF file not found", Toast.LENGTH_SHORT).show()
        }



    }
}





