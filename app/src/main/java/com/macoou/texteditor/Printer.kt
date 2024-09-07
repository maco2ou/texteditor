package com.macoou.texteditor

/**
MIT License

Copyright (c) 2024 Rohit Kushvaha

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 **/

import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.webkit.WebView
import android.widget.Toast
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date


class Printer(private val context: Context, private val content: String) : PrintDocumentAdapter() {

  override fun onLayout(
    oldAttributes: PrintAttributes?,
    newAttributes: PrintAttributes?,
    cancellationSignal: CancellationSignal?,
    callback: LayoutResultCallback?,
    extras: Bundle?
  ) {
    if (cancellationSignal?.isCanceled == true) {
      callback?.onLayoutCancelled()
      return
    }


    val builder = PrintDocumentInfo.Builder("output.pdf")
      .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
      .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN).build()

    callback?.onLayoutFinished(builder, true)
  }

  override fun onWrite(
    pages: Array<PageRange>?,
    destination: ParcelFileDescriptor?,
    cancellationSignal: CancellationSignal?,
    callback: WriteResultCallback?
  ) {
    val output = FileOutputStream(destination?.fileDescriptor)
    val pdfDocument = PdfDocument()

    // Define page width and height
    val pageWidth = 612
    val pageHeight = 792

    var yPos = 40f // Starting Y position
    var pageNumber = 0 // Start page number from 0

    // Declare variables for page and canvas, initialize first page.
    var page = pdfDocument.startPage(
      PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    )
    var canvas = page.canvas

    // Configure the text paint.
    val paint = Paint().apply {
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      textSize = 12f
      color = Color.BLACK
    }

    val maxWidth = pageWidth - 80f // Leave some margin (40f on each side)

    // Function to split a line into multiple lines that fit within the page width without cutting words
    fun wrapText(line: String): List<String> {
      val wrappedLines = mutableListOf<String>()
      var currentLine = StringBuilder()

      line.split(" ").forEach { word ->
        if (paint.measureText(word) > maxWidth) {
          // If the word itself is too long to fit in a line, split it
          var part = ""
          for (char in word) {
            if (paint.measureText(part + char) < maxWidth) {
              part += char
            } else {
              wrappedLines.add(part + "-") // Add hyphen to indicate the word is split
              part = char.toString()
            }
          }
          if (part.isNotEmpty()) {
            wrappedLines.add(part)
          }
        } else {
          val testLine = if (currentLine.isEmpty()) word else "${currentLine} $word"
          if (paint.measureText(testLine) < maxWidth) {
            currentLine.append(if (currentLine.isEmpty()) word else " $word")
          } else {
            wrappedLines.add(currentLine.toString())
            currentLine = StringBuilder(word)
          }
        }
      }

      if (currentLine.isNotEmpty()) {
        wrappedLines.add(currentLine.toString())
      }

      return wrappedLines
    }

    content.split("\n").forEach { line ->
      val wrappedLines = wrapText(line)
      wrappedLines.forEach { wrappedLine ->
        // Check if the text will go out of the page bounds, create a new page if needed
        if (yPos + paint.textSize > pageHeight - 40) {
          pdfDocument.finishPage(page) // Finish the current page

          // Start a new page and reinitialize the canvas
          pageNumber++
          page = pdfDocument.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
          )
          canvas = page.canvas
          yPos = 40f // Reset Y position for the new page
        }

        // Draw the text line on the canvas
        canvas.drawText(wrappedLine, 40f, yPos, paint)
        yPos += paint.textSize + 4 // Move to next line with a small gap
      }
    }

    // Finish the last page
    pdfDocument.finishPage(page)

    // Write the document to the file.
    try {
      pdfDocument.writeTo(output)
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context,
        context.getString(R.string.failed_to_generate_pdf_file), Toast.LENGTH_SHORT).show()
    }

    // Close the document.
    pdfDocument.close()
    output.close()

    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
  }

  companion object {
    @JvmStatic
    fun print(context: Context, text: String) {
      val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
      val jobName = "PrintJob"
      printManager?.print(jobName, Printer(context, text), null)
    }


}


}

