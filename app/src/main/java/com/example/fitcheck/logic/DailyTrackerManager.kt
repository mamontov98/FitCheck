package com.example.fitcheck.logic

import com.example.fitcheck.model.DailyTrackerEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DailyTrackerManager {
    //init DB && UID
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    //  save today's daily tracker entry to DB
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
    //  load today's daily tracker entry from DB
    fun loadEntry(
        onLoaded: (DailyTrackerEntry) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        //try to read doc
        val uid = auth.currentUser?.uid ?: return
        val today = getToday()
        db.collection("users").document(uid)
            .collection("dailyTracker").document(today)
            .get()
            //if doc exist load to entry
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
    //load weekly daily tracker entry from DB
    fun loadWeekEntries(
        onLoaded: (List<DailyTrackerEntry?>) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        //create list of 7 last dates in formt yyyyMMdd
        val uid = auth.currentUser?.uid ?: return
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val neededDates = mutableListOf<String>()
        //create the data
        for (i in 0..6) {
            neededDates.add(sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        //ask DB for doc's
        db.collection("users").document(uid)
            .collection("dailyTracker")
            .whereIn(FieldPath.documentId(), neededDates)
            .get()
            //converse to dictionary
            .addOnSuccessListener { result ->
                val entriesMap = result.documents.associateBy(
                    { it.id },
                    { it.toObject(DailyTrackerEntry::class.java) })
                // create a list in order
                val ordered = neededDates.reversed().map { entriesMap[it] }
                //return the list
                onLoaded(ordered)
            }
            .addOnFailureListener { e -> onError?.invoke(e) }
    }
    //return in format yyyyMMdd
    private fun getToday(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(Date())
    }
}
