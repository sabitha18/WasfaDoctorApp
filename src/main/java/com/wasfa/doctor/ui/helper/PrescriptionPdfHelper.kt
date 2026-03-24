package com.wasfa.doctor.ui.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi
import com.wasfa.doctor.network.response.DoctorInfo
import com.wasfa.doctor.network.response.PatientInfo
import com.wasfa.doctor.network.response.PresDetails
import java.io.File
import java.io.FileOutputStream

object PrescriptionPdfHelper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun generatePdf(
        context: Context,
        cartItems: List<PresDetails>?,
        patientInfo: List<PatientInfo>?,
        doctorInfo: List<DoctorInfo>?,
        logoPath: Bitmap?,
        qrBitmap: Bitmap?,
        clinic: String?,
        designation: String?,
        id: String?,
        doctorNewNote: String?,
        itemsNew: String?,

        ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val density = context.resources.displayMetrics.density
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 12f
        }
        val textPaintTextScan = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 8f
        }
        val textPaintRow = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 10f
        }

        val boldPaint = TextPaint(textPaint).apply {
            isFakeBoldText = true
            textSize = 12f
        }

// Paints
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#2c3e50")
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val companyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#b4b4b4")
            textSize = 16f
            isFakeBoldText = true
        }

// Layout positions
        val topY = 50f
        val lineTopY = 80f + titlePaint.textSize + 22f + 10f
        val title = "RX PRESCRIPTION"
        val companyName = clinic.toString()

// Draw logo on top-right corner
        logoPath?.let { bitmap ->
            val density = context.resources.displayMetrics.density

            val logoSize = (25 * density).toInt()
            val scaledLogo = Bitmap.createScaledBitmap(bitmap, logoSize, logoSize, true)
            val circularLogo = getCircularBitmap(scaledLogo)

            // Move everything 10dp upward
            val offsetTop = 8 * density

            // Title + subtitle center
            val titleY = topY + titlePaint.textSize
            val subtitleY = titleY + companyPaint.textSize + 8f
            val blockCenterY = (titleY + subtitleY) / 2f

            // Updated logo Y (10dp up)
            val logoY = blockCenterY - logoSize / 2f - offsetTop
            val logoX = pageInfo.pageWidth - logoSize - 40f

            // Draw logo
            canvas.drawBitmap(circularLogo, logoX, logoY, null)

            // Logo border
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.LTGRAY
                style = Paint.Style.STROKE
                strokeWidth = 0.5f * density
            }

            val cx = logoX + logoSize / 2f
            val cy = logoY + logoSize / 2f
            canvas.drawCircle(cx, cy, logoSize / 2f, borderPaint)

            // ----- Powered by Apix (also moved up!) -----
            val powerText = "Powered by Apix"
            val powerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.LTGRAY
                textSize = 10f
                textAlign = Paint.Align.CENTER
            }

            // Move the text 10dp up along with logo
            canvas.drawText(
                powerText,
                cx,                                  // centered
                cy + (logoSize / 2f) + 15f,   // text moved 10dp up
                powerPaint
            )
        }


// Draw title centered
        val titleWidth = titlePaint.measureText(title)
        canvas.drawText(title, (pageInfo.pageWidth - titleWidth) / 2f, topY + titlePaint.textSize, titlePaint)

// Draw company name centered below title
        val companyWidth = companyPaint.measureText(companyName)
        canvas.drawText(companyName, (pageInfo.pageWidth - companyWidth) / 2f, topY + titlePaint.textSize + 28f, companyPaint)

        //draw line


        val linePaint = Paint().apply {
            color = Color.parseColor("#dadada")
            strokeWidth = 0.5f * density   // 2dp height
            style = Paint.Style.STROKE
        }

        canvas.drawLine(
            20f,           // start X (little padding from left)
            lineTopY,      // Y
            pageInfo.pageWidth - 20f,   // end X (padding right)
            lineTopY,
            linePaint
        )


        // Patient info
        // --- Patient Info Row ---
        val rowY = lineTopY + 40f   // start below the line

        val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 12f
            isFakeBoldText = true
        }

        val valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 12f
        }

// Column positions based on your screenshot
        val col1X = 30f     // Patient Name
        val col2X = 200f    // Age
        val col3X = 300f    // Civil ID
        val col4X = 420f    // Nationality
        val col5X = 520f    // Gender

        val patient = patientInfo?.getOrNull(0)

// ---- LABELS ----
        canvas.drawText("Patient Name :", col1X, rowY, labelPaint)
        canvas.drawText("Age :",          col2X, rowY, labelPaint)
        canvas.drawText("Civil ID :",     col3X, rowY, labelPaint)
        canvas.drawText("Nationality :",  col4X, rowY, labelPaint)
        canvas.drawText("Gender :",       col5X, rowY, labelPaint)

// ---- VALUES (placed slightly below labels) ----
        val valueY = rowY + 16f

        canvas.drawText(patient?.name ?: "-",         col1X, valueY, valuePaint)
        val age = calculateAge(patient?.dob ?: "")
        canvas.drawText(age, col2X, valueY, valuePaint)
        canvas.drawText(patient?.civilId ?: "-",      col3X, valueY, valuePaint)
        canvas.drawText(patient?.nationality ?: "-",  col4X, valueY, valuePaint)
        canvas.drawText(patient?.gender ?: "-",       col5X, valueY, valuePaint)


        // Table setup
        val columns = floatArrayOf(30f, 60f, 220f, 260f, 340f, 395f, 450f, 595f)

        val headerRowHeight = 40f
        val minDataRowHeight = 40f
        // ======== Instructions Centered Above Dose–Time–Course–Notes ========

// ======== PERFECT INSTRUCTION BOX (NO GAP, SHARED BORDER) ========

// Centers of the 4 headers
        val doseCenter = (columns[3] + columns[4]) / 2f
        val addNotesCenter = (columns[6] + columns[7]) / 2f



// Center of the instruction box
        val instructionCenterX = (doseCenter + addNotesCenter) / 2f

// Instruction box top position
        val instructionTop = 230f
        val instructionBottom = instructionTop + 35f   // Height of box

// Draw instruction box LEFT + RIGHT + TOP border (NO BOTTOM BORDER)
        val instructionBorderPaint = Paint().apply {
            color = Color.parseColor("#dadada")
            strokeWidth = 1.2f
            style = Paint.Style.STROKE
        }

// Left border
        canvas.drawLine(columns[3], instructionTop, columns[3], instructionBottom, instructionBorderPaint)
// Right border
        canvas.drawLine(columns[7], instructionTop, columns[7], instructionBottom, instructionBorderPaint)

// Top border
        canvas.drawLine(columns[3], instructionTop, columns[7], instructionTop, instructionBorderPaint)


// Draw Instruction text centered
        val instructionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4e4e4e")
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(
            " Instruction / تعليمات ",
            instructionCenterX,
            instructionTop + 22f,      // vertically centered text
            instructionPaint
        )

// IMPORTANT: table top = SAME LINE as instruction bottom
        val tableTop = instructionBottom   // <-- NO GAP



        val headers = listOf("Sl No", "Product Name", "QTY", "Dose", "Time", "Course", "Additional Notes")

        // Draw header row background (optional)
        val headerBackgroundPaint = Paint().apply {
            color = Color.parseColor("#f1f1f1")
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
        }
        canvas.drawRect(columns[0], tableTop, columns.last(), tableTop + headerRowHeight, headerBackgroundPaint)

        // Draw headers
        headers.forEachIndexed { i, headerText ->
            val colStartX = columns[i]
            val colWidth = columns[i + 1] - columns[i]

            val headerTextPaint = TextPaint(boldPaint).apply {
                color = Color.parseColor("#4e4e4e")
                textAlign = Paint.Align.LEFT  // Important: Let StaticLayout handle the alignment, not textAlign
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
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
            color = Color.parseColor("#dadada")
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
                buildDoseText(item),
                item.dose_time ?: "-",
                buildCourseText(item),
                item.description ?: "-"
            )


            // Calculate row height based on the tallest cell
            val cellHeights = values.mapIndexed { i, text ->
                val colWidth = columns[i + 1] - columns[i] - 10f // 10f padding horizontally
                calculateStaticLayoutHeight(text, colWidth, textPaint)
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

                // Bold only Product Name (column index 1)
                val paintToUse = if (colIndex == 1) {
                    TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = textPaint.color
                        textSize = textPaintRow.textSize
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                } else {
                    textPaintRow
                }

                drawStaticLayoutText(
                    canvas,
                    text,
                    colStartX + 5f,
                    currentY + 5f,
                    colWidth,
                    paintToUse
                )
            }


            currentY += rowHeight
        }


        if (!itemsNew.isNullOrBlank()) {

            val newRowIndex = dataRows.size // next serial number

            val values = listOf(
                "${newRowIndex + 1}",   // Serial number
                itemsNew,               // Product Name
                "",                     // QTY empty
                "",                     // Dose empty
                "",                     // Time empty
                "",                     // Course empty
                ""                      // Notes empty
            )

            // Calculate row height
            val cellHeights = values.mapIndexed { i, text ->
                val colWidth = columns[i + 1] - columns[i] - 10f
                calculateStaticLayoutHeight(text, colWidth, textPaintRow)
            }

            val rowHeight = cellHeights.maxOrNull()?.coerceAtLeast(minDataRowHeight)
                ?: minDataRowHeight

            // Optional alternate background
            if (newRowIndex % 2 == 1) {
                val bgPaint = Paint().apply {
                    color = Color.parseColor("#F5F5F5")
                    style = Paint.Style.FILL
                }
                canvas.drawRect(columns[0], currentY, columns.last(), currentY + rowHeight, bgPaint)
            }

            // Draw borders
            canvas.drawRect(columns[0], currentY, columns.last(), currentY + rowHeight, borderPaint)
            columns.forEach { x ->
                canvas.drawLine(x, currentY, x, currentY + rowHeight, borderPaint)
            }

            // Draw text
            values.forEachIndexed { colIndex, text ->
                val colStartX = columns[colIndex]
                val colWidth = columns[colIndex + 1] - columns[colIndex] - 10f

                val paintToUse = if (colIndex == 1) {
                    TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = textPaintRow.color
                        textSize = textPaintRow.textSize
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                } else {
                    textPaintRow
                }

                drawStaticLayoutText(
                    canvas,
                    text,
                    colStartX + 5f,
                    currentY + 5f,
                    colWidth,
                    paintToUse
                )
            }

            currentY += rowHeight
        }
        val notesText = "Notes: ${doctorNewNote}"

// Text paint
        val notesTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 11f
        }

// Box width
        val notesLeft = 30f
        val notesRight = pageInfo.pageWidth - 30f
        val textWidth = (notesRight - notesLeft - 20f).toInt() // padding

// Create multiline layout
        val notesLayout = StaticLayout.Builder.obtain(
            notesText,
            0,
            notesText.length,
            notesTextPaint,
            textWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.2f)
            .setIncludePad(false)
            .build()

// Dynamic height based on content
        val paddingVertical = 30f

        val minHeight = 20f * density
        val notesHeight = maxOf(
            notesLayout.height + paddingVertical,
            minHeight
        )

        val notesTopY = currentY + (20f * density)
        val notesBottom = notesTopY + notesHeight

// ===== Background =====
        val bgPaint = Paint().apply {
            color = Color.parseColor("#FAFAFA")
            style = Paint.Style.FILL
        }
        canvas.drawRect(notesLeft, notesTopY, notesRight, notesBottom, bgPaint)

// ===== Border =====

        canvas.drawRect(notesLeft, notesTopY, notesRight, notesBottom, borderPaint)

// ===== Draw Text =====
        canvas.save()
        canvas.translate(notesLeft + 10f, notesTopY + 10f)
        notesLayout.draw(canvas)
        canvas.restore()

// 👉 IMPORTANT: update currentY if needed
        currentY = notesBottom
        // --- Draw dashed line below table ---
        val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#dadada")   // light gray
            strokeWidth = 0.5f * density
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f) // 10px dash, 10px gap
        }

// Add 20dp top space before dashed line
        val dashedLineY = currentY + (15f * density)

// Draw dashed line
        canvas.drawLine(
            10f,
            dashedLineY,
            pageInfo.pageWidth - 10f,
            dashedLineY,
            dashPaint
        )


        // ======== FOOTER EXACTLY LIKE SCREENSHOT ========
        val footerTopY = currentY + 60f
        val leftX = 40f
        val rightX = pageInfo.pageWidth - 180f      // Right block X
        val centerX = pageInfo.pageWidth / 2f       // QR center

        val doctor = doctorInfo?.getOrNull(0)

// -------- LEFT SIDE --------
        canvas.drawText("Prescribed by:", leftX, footerTopY, boldPaint)
        canvas.drawText(doctor?.name ?: "-", leftX, footerTopY + 18f, textPaint)

        canvas.drawText("RX Id:", leftX, footerTopY + 40f, boldPaint)
        canvas.drawText(id ?: "-", leftX, footerTopY + 40f + 18f, textPaint)


// -------- RIGHT SIDE (top-right) --------
        canvas.drawText(
            doctor?.name ?: "-",
            pageInfo.pageWidth - 40f,
            footerTopY,
            boldPaint.apply { textAlign = Paint.Align.RIGHT }
        )

        canvas.drawText(
            designation ?: "-",
            pageInfo.pageWidth - 40f,
            footerTopY + 20f,
            textPaint.apply { textAlign = Paint.Align.RIGHT }
        )


// -------- CENTER QR --------
        qrBitmap?.let {
            val qrSize = 120
            val qr = Bitmap.createScaledBitmap(it, qrSize, qrSize, true)

            // Text (two lines)
            val scanText = "Scan here to receive your Prescription"
            val scanPaint = TextPaint(textPaintTextScan).apply {
                textAlign = Paint.Align.LEFT
                textSize = 10f
            }

            // Max width of the text box (depends on your layout)
            val maxTextWidth = 70  // you can adjust this

            // Create StaticLayout for multiline text
            val staticLayout = StaticLayout.Builder.obtain(
                scanText, 0, scanText.length, scanPaint, maxTextWidth
            )
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()

            val textWidth = staticLayout.width.toFloat()
            val textHeight = staticLayout.height.toFloat()

            val gap = 10f

            // Center QR only
            val qrStartX = (pageInfo.pageWidth - qrSize) / 2f

            // Move text to left of QR
            val groupStartX = qrStartX - (textWidth + gap)

            val bottomPadding = 40f
            val qrY = pageInfo.pageHeight - qrSize - bottomPadding

            // Vertically center the text block relative to QR
            val textY = qrY + (qrSize / 2f) - (textHeight / 2f)

            // Draw multiline text
            canvas.save()
            canvas.translate(groupStartX, textY)
            staticLayout.draw(canvas)
            canvas.restore()

            // Draw QR
            canvas.drawBitmap(qr, qrStartX, qrY, null)
        }



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
    private fun buildDoseText(item: PresDetails): String {
        val dose = item.doseday?.takeIf { !it.isNullOrBlank() && it.lowercase() != "null" }
        val doseday = item.dose?.takeIf { !it.isNullOrBlank() && it.lowercase() != "null" }

        val doseParts = listOfNotNull( doseday,dose)
        return doseParts.joinToString(" dose ")
    }

    private fun buildCourseText(item: PresDetails): String {
        val duration = item.course_duration?.takeIf { !it.isNullOrBlank() && it.lowercase() != "null" }
        val day = item.course_day?.takeIf { !it.isNullOrBlank() && it.lowercase() != "null" }

        return listOfNotNull("for ",duration, day).joinToString(" ") // e.g., "3 Week"
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, size, size)

        // Draw circle mask
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        // Set Xfer mode to overlay bitmap inside circle
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, null, rect, paint)

        return output
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
    private fun drawCircularBorder(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY       // grey border
            style = Paint.Style.STROKE
            strokeWidth = 1f * canvas.density  // 1dp border
        }
        canvas.drawCircle(cx, cy, radius, borderPaint)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAge(dobString: String): String {
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dob = java.time.LocalDate.parse(dobString, formatter)
            val today = java.time.LocalDate.now()

            val age = java.time.Period.between(dob, today).years
            age.toString()
        } catch (e: Exception) {
            "-"
        }
    }

}
