package com.example.fitcheck.model

data class WeeklyMetric(
    val name: String,            // למשל: "Weight"
    val values: List<Float>      // הערכים הגרפיים לשבוע
)

