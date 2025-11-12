package com.wasfa.doctor.ui.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.wasfa.doctor.network.response.CartItem
import com.wasfa.doctor.network.response.DoctorInfo
import com.wasfa.doctor.network.response.PatientInfo
import java.io.File
import java.io.FileOutputStream

object PrescriptionPdfReviewHelper {

    fun generatePdf(
        context: Context,
        cartItems: List<CartItem>?,
        patientInfo: List<PatientInfo>?,
        doctorInfo: List<DoctorInfo>?,
        qrBitmap: Bitmap?,
        logoPath: Bitmap?,
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 12f
        }

        val boldPaint = TextPaint(textPaint).apply {
            isFakeBoldText = true
            textSize = 12f
        }

        // Title
        val titlePaint = TextPaint(textPaint).apply {
            textSize = 16f
            isFakeBoldText = true
        }
        // Load logo bitmap from URL and draw it above the title
        logoPath?.let { bitmap ->
            val left = (pageInfo.pageWidth - bitmap.width) / 2f
            canvas.drawBitmap(bitmap, left, 40f, null)
        }


        val title = "RX PRESCRIPTION"
        val titleWidth = titlePaint.measureText(title)
        canvas.drawText(title, (pageInfo.pageWidth - titleWidth) / 2f, 160f, titlePaint)

        // Patient info
        val patient = patientInfo?.getOrNull(0)
        canvas.drawText("Patient Name: ", 30f, 210f, boldPaint)
        canvas.drawText("Age: ", 250f, 210f, boldPaint)
        canvas.drawText("Civil ID: ", 400f, 210f, boldPaint)

        canvas.drawText(patient?.name ?: "-", 110f, 210f, textPaint)
        canvas.drawText(patient?.dob ?: "-", 280f, 210f, textPaint)
        canvas.drawText(patient?.civilId ?: "-", 460f, 210f, textPaint)

        // Table setup
        val columns = floatArrayOf(30f, 60f, 220f, 260f, 300f, 355f, 410f, 555f) // Columns X coords
        val headerRowHeight = 40f
        val minDataRowHeight = 40f
        var tableTop = 220f

        val headers = listOf("Sl No", "Product Name", "QTY", "Dose", "Time", "Course", "Additional Notes")

        // Draw header row background (optional)
        val headerBackgroundPaint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
        }
        canvas.drawRect(columns[0], tableTop, columns.last(), tableTop + headerRowHeight, headerBackgroundPaint)

        // Draw headers
        // Draw headers
        headers.forEachIndexed { i, headerText ->
            val colStartX = columns[i]
            val colWidth = columns[i + 1] - columns[i]

            val headerTextPaint = TextPaint(boldPaint).apply {
                textAlign = Paint.Align.LEFT  // Important: Let StaticLayout handle the alignment, not textAlign
                textSize = 12f
            }

            val staticLayout = StaticLayout.Builder.obtain(
                headerText, 0, headerText.length, headerTextPaint, (colWidth - 10).toInt()
            )
                .setAlignment(Layout.Alignment.ALIGN_CENTER) // This centers text block
                .setIncludePad(false)
                .build()

            canvas.save()
            // Proper centering: shift left edge to center of column minus half the text block width
            canvas.translate(colStartX + (colWidth - staticLayout.width) / 2f, tableTop + (headerRowHeight - staticLayout.height) / 2f)
            staticLayout.draw(canvas)
            canvas.restore()
        }


        // Draw header borders
        val borderPaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        canvas.drawRect(columns[0], tableTop, columns.last(), tableTop + headerRowHeight, borderPaint)
        columns.forEach { x -> canvas.drawLine(x, tableTop, x, tableTop + headerRowHeight, borderPaint) }

        var currentY = tableTop + headerRowHeight

        // Draw data rows
        val dataRows = cartItems ?: emptyList()

        dataRows.forEachIndexed { rowIndex, item ->
            // Prepare values for each column
            val values = listOf(
                "${rowIndex + 1}",
                item.productName ?: "-",
                item.quantity ?: "-",
                item.dose ?: "-",
                item.dose_time ?: "-",
                item.course_duration ?: "-",
                item.description ?: "-"
            )

            // Calculate row height based on the tallest cell
            val cellHeights = values.mapIndexed { i, text ->
                val colWidth = columns[i + 1] - columns[i] - 10f // 10f padding horizontally
                calculateStaticLayoutHeight(text.toString(), colWidth, textPaint)
            }
            val rowHeight = cellHeights.maxOrNull()?.coerceAtLeast(minDataRowHeight) ?: minDataRowHeight

            // Draw row background (optional: alternate rows)
            if (rowIndex % 2 == 1) {
                val bgPaint = Paint().apply { color = Color.parseColor("#F5F5F5"); style = Paint.Style.FILL }
                canvas.drawRect(columns[0], currentY, columns.last(), currentY + rowHeight, bgPaint)
            }

            // Draw cell borders
            canvas.drawRect(columns[0], currentY, columns.last(), currentY + rowHeight, borderPaint)
            columns.forEach { x -> canvas.drawLine(x, currentY, x, currentY + rowHeight, borderPaint) }

            // Draw cell texts (left-aligned with padding)
            values.forEachIndexed { colIndex, text ->
                val colStartX = columns[colIndex]
                val colWidth = columns[colIndex + 1] - columns[colIndex] - 10f
                drawStaticLayoutText(canvas,
                    text.toString(), colStartX + 5f, currentY + 5f, colWidth, textPaint)
            }

            currentY += rowHeight
        }

        // Footer with doctor info
        val footerY = currentY + 40f +40f
        val doctor = doctorInfo?.getOrNull(0)
        canvas.drawText("Prescribed by: ${doctor?.name ?: "-"}", 50f, footerY, boldPaint)
        canvas.drawText("RX Id: ${doctor?.id ?: "-"}", 50f, footerY + 20f, boldPaint)
        canvas.drawText(doctor?.name ?: "-", 50f, footerY + 50f, textPaint)
        canvas.drawText(doctor?.type ?: "-", 50f, footerY + 70f, textPaint)
        canvas.drawText("Scan here to receive the Prescription", 300f, footerY + 100f, textPaint)

        // Draw barcode if exists
        qrBitmap?.let {
            val scaled = Bitmap.createScaledBitmap(it, 80, 80, true)
            canvas.drawBitmap(scaled, 300f, footerY + 120f, null)
        }

        // Footer text right aligned
        val footerText = "Apix Medical"
        val footerPaint = Paint(textPaint).apply {
            color = Color.BLUE
            textSize = 12f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(footerText, pageInfo.pageWidth - 20f, pageInfo.pageHeight - 40f, footerPaint)

        pdfDocument.finishPage(page)

        return try {
            val file = File(context.getExternalFilesDir(null), "prescription.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            pdfDocument.close()
        }
    }

    private fun calculateStaticLayoutHeight(text: String, maxWidth: Float, paint: TextPaint): Float {
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, maxWidth.toInt())
            .setAlignment(Layout.Alignment.ALIGN_CENTER) // left align text inside cells
            .setIncludePad(false)
            .build()
        return staticLayout.height.toFloat() + 10f // extra padding vertically
    }

    private fun drawStaticLayoutText(canvas: Canvas, text: String, startX: Float, startY: Float, maxWidth: Float, paint: TextPaint) {
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, maxWidth.toInt())
            .setAlignment(Layout.Alignment.ALIGN_CENTER) // left align text
            .setIncludePad(false)
            .build()
        canvas.save()
        canvas.translate(startX, startY)
        staticLayout.draw(canvas)
        canvas.restore()
    }
}
