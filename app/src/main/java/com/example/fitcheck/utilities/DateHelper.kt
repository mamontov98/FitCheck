package com.example.fitcheck.utilities

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val folderFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    //Returns the first day (Sunday) of the week for a given date
    fun getStartOfWeek(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.firstDayOfWeek = Calendar.SUNDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return cal.time
    }

    //Returns the last day (Saturday) of the week for a given week's start date
    fun getEndOfWeek(start: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = start
        cal.add(Calendar.DAY_OF_MONTH, 6)
        return cal.time
    }

    //Returns a formatted string showing the date range for a week (e.g. "16.06.2025 - 22.06.2025")
    fun getFormattedWeekRange(start: Date): String {
        val end = getEndOfWeek(start)
        return "${dateFormat.format(start)} - ${dateFormat.format(end)}"
    }

    //Checks if the given date is in the current week
    fun isCurrentWeek(date: Date): Boolean {
        val currentWeekStart = getStartOfWeek(Date())
        val targetWeekStart = getStartOfWeek(date)
        return currentWeekStart == targetWeekStart
    }

    //Returns the folder name for a week (e.g. "2025-06-16")
    fun getFolderNameForWeek(start: Date): String {
        return folderFormat.format(start) // למשל: "2025-06-16"
    }

    //Returns the start date of the next week
    fun getNextWeek(start: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = start
        cal.add(Calendar.WEEK_OF_YEAR, 1)
        return getStartOfWeek(cal.time)
    }

    //Returns the start date of the previous week
    fun getPreviousWeek(start: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = start
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        return getStartOfWeek(cal.time)
    }
}

