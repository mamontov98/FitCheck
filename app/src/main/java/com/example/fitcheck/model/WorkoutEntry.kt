package com.example.fitcheck.model

data class WorkoutEntry(
    var exerciseName: String = "",
    var sets: Int = 0,
    var reps: String = "",
    var weights: String = "",
    var prevReps: String = "-",
    var prevWeight: String = "-",
    var date: String = ""
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): WorkoutEntry {
            return WorkoutEntry(
                exerciseName = map["exerciseName"] as? String ?: "",
                sets = when (val s = map["sets"]) {
                    is Long -> s.toInt()
                    is Int -> s
                    is String -> s.toIntOrNull() ?: 0
                    else -> 0
                },
                reps = map["reps"] as? String ?: "",
                weights = map["weights"] as? String ?: "",
                date = map["date"]?.toString() ?: ""
            )
        }



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
