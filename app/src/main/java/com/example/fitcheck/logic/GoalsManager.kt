package com.example.fitcheck.logic

import com.example.fitcheck.model.Goals
import com.example.fitcheck.utilities.AppConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class GoalsManager {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Saves the user's goals to Firestore
    fun saveGoals(goals: Goals, onSuccess: (() -> Unit)? = null, onFailure: ((Exception) -> Unit)? = null) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .set(hashMapOf("goals" to goals), SetOptions.merge())
            .addOnSuccessListener { onSuccess?.invoke() }
            .addOnFailureListener { e -> onFailure?.invoke(e) }
    }

    // Loads the user's goals from Firestore
    fun loadGoals(onLoaded: (Goals) -> Unit, onError: ((Exception) -> Unit)? = null) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val map = doc.get("goals") as? Map<*, *>
                if (map != null) {
                    // Parse values from Firestore map (with defaults)
                    val goals = Goals(
                        carbs   = (map["carbs"] as? Long)?.toInt() ?: AppConstants.GoalsDefault.DEFAULT_CARBS,
                        protein = (map["protein"] as? Long)?.toInt() ?:  AppConstants.GoalsDefault.DEFAULT_PROTEIN,
                        fat     = (map["fat"] as? Long)?.toInt() ?:  AppConstants.GoalsDefault.DEFAULT_FAT,
                        steps   = (map["steps"] as? Long)?.toInt() ?:  AppConstants.GoalsDefault.DEFAULT_STEPS,
                        sleep   = (map["sleep"] as? Long)?.toInt() ?:  AppConstants.GoalsDefault.DEFAULT_SLEEP,
                        weight  = (map["weight"] as? Double)?.toFloat() ?: AppConstants.GoalsDefault.DEFAULT_WEIGHT
                    )
                    onLoaded(goals)
                } else {
                    // Default values
                    onLoaded(
                        Goals(
                            carbs = AppConstants.GoalsDefault.DEFAULT_CARBS,
                            protein = AppConstants.GoalsDefault.DEFAULT_PROTEIN,
                            fat = AppConstants.GoalsDefault.DEFAULT_FAT,
                            steps = AppConstants.GoalsDefault.DEFAULT_STEPS,
                            sleep = AppConstants.GoalsDefault.DEFAULT_SLEEP,
                            weight = AppConstants.GoalsDefault.DEFAULT_WEIGHT
                        )
                    )
                }
            }
            .addOnFailureListener { e -> onError?.invoke(e) }
    }

    //Calculates calories
    fun calculateCalories(carbs: Int, protein: Int, fat: Int): Int {
        return 4 * carbs + 4 * protein + 9 * fat
    }
}
