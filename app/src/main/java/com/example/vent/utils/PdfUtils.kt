package com.example.vent.utils

import Model

import android.content.ContentValues
import android.content.Context

import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import com.example.vent.utils.DateUtils.formatDate
import com.example.vent.utils.TimeUtils.formatTime
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.itextpdf.layout.borders.Border
import java.io.OutputStream
import android.util.Log
import androidx.annotation.RequiresApi

import androidx.core.content.ContextCompat
import com.example.vent.R
import com.itextpdf.kernel.colors.DeviceRgb

object PdfUtils {

    @RequiresApi(Build.VERSION_CODES.R)
    fun createPdf(context: Context, event: Model) {
        var document: Document? = null // Declare document outside the try block
        var outputStream: OutputStream? = null
        try {
            if (event.name.isEmpty() || event.type.isEmpty() || event.startDate == null || event.endDate == null || event.startTime == null || event.endTime == null) {
                Toast.makeText(context, "Error: Event data is incomplete", Toast.LENGTH_LONG).show()
                return // Exit the function if the event data is incomplete
            }
            // Define content values for MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "EventDetails.pdf") // File name
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf") // MIME type
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/") // Save in Downloads folder
                Log.d("PdfUtils", "PDF path: ${MediaStore.Files.getContentUri("external")}")
            }

            // Get the content resolver and insert the new file into MediaStore
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            Log.d("PdfUtils", "URI: $uri") // Log the URI

            if (uri != null) {
                // For older versions, you can open the output stream directly
                outputStream = resolver.openOutputStream(uri)
                Log.d("PdfUtils", "OutputStream: $outputStream") // Log the outputStream
                if (outputStream == null) {
                    Toast.makeText(context, "Error: Could not open output stream", Toast.LENGTH_LONG).show()
                    return // Exit the function if the stream is null
                }
                    // Create the PdfWriter instance using the output stream
                    val writer = PdfWriter(outputStream)
                    // Create the PdfDocument object with the writer
                    val pdfDoc = PdfDocument(writer)

                    // Create the Document object with the PdfDocument instance
                    // You can specify PageSize if you need custom page sizes, here it's the default A4
                    document = Document(pdfDoc, PageSize.A4)

                    // Create header with blue background
                    val headerTable = Table(floatArrayOf(1f, 4f)).useAllAvailableWidth()

                    // Load College Logo (Place it in drawable folder or assets)
                val drawable = ContextCompat.getDrawable(context, R.drawable.college_logo)
                if (drawable == null) {
                    Toast.makeText(context, "Error: Could not load college_logo.png", Toast.LENGTH_LONG).show()
                    return // Exit the function if the drawable is null
                }
                val bitmap = (drawable as BitmapDrawable).bitmap
                val imageData = ImageDataFactory.create(bitmapToByteArray(bitmap))
                val logo = Image(imageData).setWidth(50f).setHeight(50f)

                    // College name cell
                    val titleCell = Cell().add(Paragraph("Vivek College of Commerce")
                        .setFontSize(18f)
                        .setBold()
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(ColorConstants.BLUE)
                        .setBorder(Border.NO_BORDER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)

                val headerBackgroundColorInt = ContextCompat.getColor(context, R.color.blue)
                val red = (headerBackgroundColorInt shr 16) and 0xFF
                val green = (headerBackgroundColorInt shr 8) and 0xFF
                val blue = headerBackgroundColorInt and 0xFF
// Create a DeviceRgb color from the RGB components
                val headerBackgroundColor = DeviceRgb(red, green, blue)


                headerTable.addCell(Cell().add(logo)
                    .setBorder(Border.NO_BORDER)
                    .setBackgroundColor(headerBackgroundColor))
                    headerTable.addCell(titleCell)

                    document.add(headerTable)
                    document.add(Paragraph(" ")) // Space after header

                    // Event Details Table
                    val table = Table(floatArrayOf(1f, 2f)).useAllAvailableWidth()
                    table.addCell(createCell("Program Name:")).addCell(createCell(event.name))
                    table.addCell(createCell("Program Type:")).addCell(createCell(event.type))
//                    table.addCell(createCell("Participants:")).addCell(createCell("${event.participants}"))
                    table.addCell(createCell("Start Date:")).addCell(createCell(formatDate(event.startDate)))
                    table.addCell(createCell("End Date:")).addCell(createCell(formatDate(event.endDate)))
                    table.addCell(createCell("Start Time:")).addCell(createCell(formatTime(event.startTime)))
                    table.addCell(createCell("End Time:")).addCell(createCell(formatTime(event.endTime)))

                    document.add(table)
                    document.close()

                    // Notify the user that the PDF has been saved
                    Toast.makeText(context, "PDF Saved in Downloads", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.e("PdfUtils", "Error generating PDF", e) // Log the exception with tag and message
            Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            document?.close() // Close the document in the finally block
            outputStream?.close()
        }
    }

    private fun createCell(text: String): Cell {
        return Cell().add(Paragraph(text).setFontSize(12f)).setBorder(Border.NO_BORDER)
    }

    private fun bitmapToByteArray(bitmap: android.graphics.Bitmap): ByteArray {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}