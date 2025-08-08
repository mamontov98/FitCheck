package com.example.fitcheck.logic

import com.example.fitcheck.model.WorkoutEntry
import com.example.fitcheck.model.toMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WorkoutManager {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveWorkout(
        workoutName: String,
        entries: List<WorkoutEntry>,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val today = getTodayStr()
        db.collection("users").document(uid)
            .collection("workouts")
            .document("${workoutName}_$today")
            .set(mapOf("entries" to entries.map { it.toMap() }, "date" to today, "workoutName" to workoutName))
            .addOnSuccessListener { onSuccess?.invoke() }
            .addOnFailureListener { e -> onFailure?.invoke(e) }
    }

    fun loadWorkout(
        workoutName: String,
        onLoaded: (List<WorkoutEntry>) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val today = getTodayStr()
        db.collection("users").document(uid)
            .collection("workouts")
            .document("${workoutName}_$today")
            .get()
            .addOnSuccessListener { doc ->
                val entries = (doc["entries"] as? List<Map<String, Any>>)
                    ?.map { WorkoutEntry.fromMap(it) }
                    ?: emptyList()
                onLoaded(entries)
            }
            .addOnFailureListener { e -> onError?.invoke(e) }
    }

    fun resetWorkoutForToday(
        workoutName: String,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val today = getTodayStr()
        db.collection("users").document(uid)
            .collection("workouts")
            .document("${workoutName}_$today")
            .delete()
            .addOnSuccessListener { onSuccess?.invoke() }
            .addOnFailureListener { e -> onFailure?.invoke(e) }
    }

    fun getTodayStr(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun loadLastWorkout(
        workoutName: String,
        onLoaded: (List<WorkoutEntry>) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("workouts")
            .whereEqualTo("workoutName", workoutName)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(2)
            .get()
            .addOnSuccessListener { snapshot ->
                val docs = snapshot.documents
                val todayStr = getTodayStr()
                val prevDoc = docs.firstOrNull { (it["date"]?.toString() ?: "") != todayStr }
                val prevEntries = (prevDoc?.get("entries") as? List<Map<String, Any>>)
                    ?.map { WorkoutEntry.fromMap(it) } ?: emptyList()
                onLoaded(prevEntries)
            }
            .addOnFailureListener { e -> onError?.invoke(e) }
    }
}
