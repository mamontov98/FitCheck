package com.example.fitcheck.utilities

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object EmailManager {

    /**
     * Sends an email with the provided PDF file attached.
     *
     * @param context The context (usually an Activity)
     * @param pdfFile The file to be attached
     * @param coachEmail The coach's email address
     */
    fun sendEmailWithPdf(context: Context, pdfFile: File, coachEmail: String) {
        // Convert file to a content URI (for security and permissions)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        // Create the email intent
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(coachEmail))
            putExtra(Intent.EXTRA_SUBJECT, "Weekly Checkup Summary")
            putExtra(Intent.EXTRA_TEXT, "Hi Coach,\n\nAttached is my weekly checkup summary.\n\nThanks,\nFitCheck App")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Launch email app
        context.startActivity(Intent.createChooser(intent, "Send email via..."))
    }
}