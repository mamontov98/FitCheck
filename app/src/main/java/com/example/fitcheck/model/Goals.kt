package com.example.fitcheck.model

data class Goals(
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val steps: Int,
    val sleep: Int,
    val weight: Float,
    val calories: Int = 0
)

