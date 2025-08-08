
package com.example.fitcheck.model

data class DailyTrackerEntry(
    var carbs: Int = 0,
    var protein: Int = 0,
    var fat: Int = 0,
    var steps: Int = 0,
    var sleep: Int = 0,
    var weight: Float = 0f,
    var calories: Int = 0
)

