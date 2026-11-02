package com.example.myapplication.data

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

// Reads the text out of a PDF or text file the student picked.
object MaterialReader {

    // Only the start of a long document is used, to keep the request small and quick
    const val MAX_CHARACTERS = 40000

    fun readText(context: Context, uri: Uri): String? {
        return try {
            val type = context.contentResolver.getType(uri) ?: ""
            val isPdf = type == "application/pdf" || (uri.lastPathSegment ?: "").endsWith(".pdf", ignoreCase = true)
            val text = if (isPdf) readPdf(context, uri) else readPlainText(context, uri)
            text?.trim()?.take(MAX_CHARACTERS)?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    private fun readPlainText(context: Context, uri: Uri): String? =
        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }

    private fun readPdf(context: Context, uri: Uri): String? {
        PDFBoxResourceLoader.init(context.applicationContext)
        return context.contentResolver.openInputStream(uri)?.use { input ->
            PDDocument.load(input).use { doc -> PDFTextStripper().getText(doc) }
        }
    }
}
