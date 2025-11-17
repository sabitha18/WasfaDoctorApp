package com.wasfa.doctor.ui.helper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.wasfa.doctor.R
import com.wasfa.doctor.databinding.FragmentPdfViewerBinding
import com.wasfa.doctor.ui.report.PrescribedRxFragment
import java.io.File


class PdfViewerFragment : Fragment() {

    private lateinit var binding: FragmentPdfViewerBinding
    private var pdfFile: File? = null
    private var renderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var currentPageIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPdfViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pdfPath = arguments?.getString("pdfPath")
        pdfFile = pdfPath?.let { File(it) }
        pdfFile?.let { file ->
            if (file.exists()) {
                binding.pdfView.fromFile(file)
                    .enableSwipe(true)
                    .enableDoubletap(true)
                    .enableAntialiasing(true)
                    .spacing(10)
                    .load()
            } else {
                Toast.makeText(requireContext(), "PDF file not found", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnPrint.setOnClickListener {
            pdfFile?.let { printPdf(it) }
        }

        binding.btnShare.setOnClickListener {
            pdfFile?.let { sharePdf(it) }
        }

        binding.cardSave.setOnClickListener {
            try {
                val sourceFile = pdfFile // your generated PDF File
                if (sourceFile != null && sourceFile.exists()) {
                    val downloadsDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val destFile = File(downloadsDir, sourceFile.name)

                    sourceFile.copyTo(destFile, overwrite = true)

                    Toast.makeText(requireContext(), "PDF saved to Downloads", Toast.LENGTH_SHORT)
                        .show()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PrescribedRxFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "PDF file not found", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to save PDF", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cardClose.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PrescribedRxFragment())
                .addToBackStack(null)
                .commit()
        }

    }


    private fun printPdf(file: File) {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = PdfDocumentAdapter(requireContext(), file.absolutePath)
        printManager.print("Prescription", printAdapter, null)
    }

    private fun sharePdf(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Share PDF"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
