package com.example.fitcheck.UI

import ExerciseAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitcheck.R
import com.example.fitcheck.logic.WorkoutManager
import com.example.fitcheck.model.WorkoutEntry

class Workout1Activity : AppCompatActivity() {

    private lateinit var Workout1_RV_Exercises: RecyclerView
    private lateinit var Workout1_BTN_AddExerciseButton: Button
    private lateinit var Workout1_BTN_Save: Button
    private lateinit var Workout1_BTN_Reset: Button
    private lateinit var Workout1_BNT_FinishWorkout: Button
    private lateinit var adapter: ExerciseAdapter

    private val exercises = mutableListOf<WorkoutEntry>()
    private val workoutManager = WorkoutManager()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout1)

        findViews()
        adapter = ExerciseAdapter(exercises)
        Workout1_RV_Exercises.layoutManager = LinearLayoutManager(this)
        Workout1_RV_Exercises.adapter = adapter

        loadWorkout()

        setupBottomBar()
        setUpButtons()


    }

    private fun findViews() {
        Workout1_RV_Exercises = findViewById(R.id.Workout1_RV_Exercises)
        Workout1_BTN_AddExerciseButton = findViewById(R.id.Workout1_BTN_AddExerciseButton)
        Workout1_BTN_Save = findViewById(R.id.Workout1_BTN_Save)
        Workout1_BTN_Reset = findViewById(R.id.Workout1_BTN_Reset)
        Workout1_BNT_FinishWorkout = findViewById(R.id.Workout1_BNT_FinishWorkout)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpButtons() {
        Workout1_BTN_AddExerciseButton.setOnClickListener {
            exercises.add(WorkoutEntry())
            adapter.notifyItemInserted(exercises.size - 1)
            Workout1_RV_Exercises.scrollToPosition(exercises.size - 1)
        }

        Workout1_BTN_Save.setOnClickListener {
            saveWorkout()
        }

        Workout1_BTN_Reset.setOnClickListener {
            exercises.clear()
            adapter.notifyDataSetChanged()
            saveWorkout()
        }

        Workout1_BNT_FinishWorkout.setOnClickListener {
            saveWorkout()
            Toast.makeText(this, "Workout finished and saved!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupBottomBar() {
        findViewById<LinearLayout>(R.id.BottomMenu_BTN_Home).setOnClickListener {
            startActivity(Intent(this, MainMenu::class.java))

        }
        findViewById<LinearLayout>(R.id.BottomMenu_BTN_Workouts).setOnClickListener {
            startActivity(Intent(this, WorkoutMenuActivity::class.java))

        }
        findViewById<LinearLayout>(R.id.BottomMenu_BTN_Metrics).setOnClickListener {
            startActivity(Intent(this, WeeklyMetricsActivity::class.java))

        }
    }

    //Saves the current workout to Firestore
    private fun saveWorkout() {
        val today = workoutManager.getTodayStr()
        // Make sure each exercise entry gets the current date
        val entriesWithDate = exercises.map { it.copy(date = today) }
        workoutManager.saveWorkout(
            workoutName = "Workout1",
            entries = entriesWithDate,
            onSuccess = { Toast.makeText(this, "Workout saved!", Toast.LENGTH_SHORT).show() },
            onFailure = { e -> Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show() }
        )
    }



    @SuppressLint("NotifyDataSetChanged")
    //Loads today's workout from Firestore and populates the RecyclerView
    private fun loadWorkout() {
        workoutManager.loadWorkout(
            workoutName = "Workout1",
            onLoaded = { loaded ->
                exercises.clear()
                exercises.addAll(loaded)
                loadPrevExercises()
                adapter.notifyDataSetChanged()
            },
            onError = { e ->
                Toast.makeText(this, "Load failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Loads previous workout entries
    private fun loadPrevExercises() {
        workoutManager.loadLastWorkout(
            workoutName = "Workout1",
            onLoaded = { prevEntries ->
                for (current in exercises) {
                    val prev = prevEntries.find {
                        it.exerciseName.trim().lowercase() == current.exerciseName.trim().lowercase()
                    }
                    if (prev != null) {
                        current.prevReps = prev.reps
                        current.prevWeight = prev.weights
                        Log.d("DEBUG", "Current: ${current.exerciseName}, Prev: ${prev?.exerciseName}, Reps: ${prev?.reps}, Weights: ${prev?.weights}")
                    } else {
                        current.prevReps = "-"
                        current.prevWeight = "-"
                    }
                }
                adapter.notifyDataSetChanged()
            }
        )
    }


}
