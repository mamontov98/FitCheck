package com.example.fitcheck.utilities

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

object CheckupPdfManager {

    /**
     * Generates a weekly checkup PDF using Android's PdfDocument
     *
     * @param context Android context
     * @param weekRange A formatted string like "21.07.2025 - 27.07.2025"
     * @param averages Weekly averages map (e.g., weight, calories)
     * @param goals User goal values map
     * @param questionnaire User answers from the checkup form
     * @return A generated PDF file
     */
    fun generatePdf(
        context: Context,
        weekRange: String,
        averages: Map<String, String>,
        goals: Map<String, String>,
        questionnaire: Map<String, String>
    ): File {
        val file = File(context.cacheDir, "WeeklyCheckup.pdf")
        val document = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint().apply {
            textSize = 14f
            isAntiAlias = true
        }

        var y = 40

        // Title
        paint.textSize = 20f
        canvas.drawText("Weekly Check-Up Summary", 40f, y.toFloat(), paint)

        paint.textSize = 14f
        y += 30
        canvas.drawText("Week: $weekRange", 40f, y.toFloat(), paint)

        // Averages
        y += 30
        canvas.drawText("📊 Weekly Averages:", 40f, y.toFloat(), paint)
        y += 20
        for ((key, value) in averages) {
            canvas.drawText("$key: $value", 60f, y.toFloat(), paint)
            y += 20
        }

        // Goals
        y += 20
        canvas.drawText("🎯 Goals:", 40f, y.toFloat(), paint)
        y += 20
        for ((key, value) in goals) {
            canvas.drawText("$key: $value", 60f, y.toFloat(), paint)
            y += 20
        }

        // Questionnaire
        y += 20
        canvas.drawText("📝 Check-Up Form:", 40f, y.toFloat(), paint)
        y += 20
        for ((question, answer) in questionnaire) {
            val wrappedLines = wrapText("$question: $answer", paint, 480)
            for (line in wrappedLines) {
                canvas.drawText(line, 60f, y.toFloat(), paint)
                y += 20
                if (y > 800) {
                    // create new page if needed
                    document.finishPage(page)
                    val newPage = document.startPage(pageInfo)
                    y = 40
                    canvas.drawText("Continued...", 40f, y.toFloat(), paint)
                    y += 30
                }
            }
            y += 10
        }

        document.finishPage(page)
        document.writeTo(FileOutputStream(file))
        document.close()
        return file
    }

    // Helper function to wrap long lines
    private fun wrapText(text: String, paint: Paint, maxWidth: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""
        for (word in words) {
            val potentialLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(potentialLine) <= maxWidth) {
                currentLine = potentialLine
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        return lines
    }
}