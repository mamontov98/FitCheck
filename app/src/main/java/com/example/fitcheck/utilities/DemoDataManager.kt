package com.example.fitcheck.utilities

import android.content.Context
import android.widget.Toast
import com.example.fitcheck.model.WorkoutEntry
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

object DemoDataManager {

    private val demoUsers = listOf(
        "XeE1aWaK7xVh9Pau6BZYSOrjLrG3", // demo1@fitcheck.com
        "UHSGrTF102YB3HrzhwARoJMXwfz2", // demo2@fitcheck.com
        "ZwMr2yRYscfSqoYyuE5PzY6girn2"  // demo3@fitcheck.com
    )

    fun insertAllDemos(context: Context) {
        val db = FirebaseFirestore.getInstance()
        demoUsers.forEach { uid ->
            insertDemoData(uid, db)
        }
        Toast.makeText(context, "Demo users' data inserted!", Toast.LENGTH_SHORT).show()
    }

    private fun insertDemoData(uid: String, db: FirebaseFirestore) {
        val today = getTodayStr()

        // מטרות
        val goals = hashMapOf(
            "carbs" to 220,
            "protein" to 160,
            "fat" to 70,
            "steps" to 8000,
            "sleep" to 7,
            "weight" to 75.5
        )
        db.collection("users").document(uid)
            .collection("goals").document("weekly_goals")
            .set(goals)

        // נתונים יומיים - 7 ימים אחרונים עם שונות קלה (פורמט yyyyMMdd)
        val dailyTrackerRef = db.collection("users").document(uid).collection("dailyTracker")
        val calendar = Calendar.getInstance()
        for (i in 0 until 7) {
            val date = getDateStr(calendar.time) // yyyyMMdd
            val calories = (180 + i * 5) * 4 + (150 + i * 2) * 4 + (60 + i) * 9 // הערכה לפי מקרונוטריינטים
            val daily = hashMapOf(
                "calories" to calories,
                "carbs" to (180 + i * 5),
                "protein" to (150 + i * 2),
                "fat" to (60 + i),
                "sleep" to (6 + (i % 3)),  // 6,7,8,...
                "steps" to (7000 + i * 300),
                "weight" to (75.0 + i * 0.2)
            )
            dailyTrackerRef.document(date).set(daily)
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        // אימון של היום בלבד לפי פורמט שלך
        val workoutEntries = listOf(
            WorkoutEntry("Bench Press", 3, "10,10,8", "80,80,75", date = today),
            WorkoutEntry("Incline DB Press", 3, "12,12,10", "20,20,18", date = today)
        )

        val workoutData = mapOf(
            "workoutName" to "Workout1",
            "date" to today,
            "entries" to workoutEntries.map { it.toMap() }
        )

        db.collection("users").document(uid)
            .collection("workouts").document("Workout1_$today")
            .set(workoutData)

        // שאלון שבועי
        val checkup = hashMapOf(
            "energyLevel" to 8,
            "hungerLevel" to 5,
            "stressLevel" to 4,
            "sorenessLevel" to 3,
            "moodLevel" to 9,
            "notes" to "Felt stronger this week"
        )
        db.collection("users").document(uid)
            .collection("weekly_checkups").document("2025-08-03")
            .set(checkup)
    }

    private fun getTodayStr(): String {
        return getDateStr(Date())
    }

    private fun getDateStr(date: Date): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(date)
    }
}

fun WorkoutEntry.toMap(): Map<String, Any> {
    return mapOf(
        "exerciseName" to exerciseName,
        "sets" to sets,
        "reps" to reps,
        "weights" to weights,
        "date" to date
    )
}
