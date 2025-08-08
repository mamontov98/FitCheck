package com.example.fitcheck.logic

import com.example.fitcheck.model.DailyTrackerEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DailyTrackerManager {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveEntry(
        entry: DailyTrackerEntry,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val today = getToday()
        db.collection("users").document(uid)
            .collection("dailyTracker").document(today)
            .set(entry)
            .addOnSuccessListener { onSuccess?.invoke() }
            .addOnFailureListener { e -> onFailure?.invoke(e) }
    }

    fun loadEntry(
        onLoaded: (DailyTrackerEntry) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val today = getToday()
        db.collection("users").document(uid)
            .collection("dailyTracker").document(today)
            .get()
            .addOnSuccessListener { doc ->
                val entry = doc.toObject(DailyTrackerEntry::class.java)
                if (entry != null) {
                    onLoaded(entry)
                } else {
                    onLoaded(DailyTrackerEntry())
                }
            }
            .addOnFailureListener { e -> onError?.invoke(e) }
    }

    fun resetEntry(
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        saveEntry(DailyTrackerEntry(), onSuccess, onFailure)
    }

    fun loadWeekEntries(
        onLoaded: (List<DailyTrackerEntry?>) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val neededDates = mutableListOf<String>()
        for (i in 0..6) {
            neededDates.add(sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        db.collection("users").document(uid)
            .collection("dailyTracker")
            .whereIn(FieldPath.documentId(), neededDates)
            .get()
            .addOnSuccessListener { result ->
                val entriesMap = result.documents.associateBy(
                    { it.id },
                    { it.toObject(DailyTrackerEntry::class.java) })
                val ordered = neededDates.reversed().map { entriesMap[it] }
                onLoaded(ordered)
            }
            .addOnFailureListener { e -> onError?.invoke(e) }
    }

    private fun getToday(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(Date())
    }
}
