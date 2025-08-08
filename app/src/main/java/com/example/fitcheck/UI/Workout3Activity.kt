package com.example.fitcheck.UI

import ExerciseAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitcheck.R
import com.example.fitcheck.logic.WorkoutManager
import com.example.fitcheck.model.WorkoutEntry

class Workout3Activity : AppCompatActivity() {

    private lateinit var Workout3_RV_Exercises: RecyclerView
    private lateinit var Workout3_BTN_AddExercise: Button
    private lateinit var Workout3_BTN_Save: Button
    private lateinit var Workout3_BTN_Reset: Button
    private lateinit var Workout3_BNT_FinishWorkout: Button
    private lateinit var adapter: ExerciseAdapter

    private val exercises = mutableListOf<WorkoutEntry>()
    private val workoutManager = WorkoutManager()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout3)

        findViews()
        adapter = ExerciseAdapter(exercises)
        Workout3_RV_Exercises.layoutManager = LinearLayoutManager(this)
        Workout3_RV_Exercises.adapter = adapter


        loadWorkout()
        setupBottomBar()
        setUpButtons()


    }

    private fun setUpButtons() {
        Workout3_BTN_AddExercise.setOnClickListener {
            exercises.add(WorkoutEntry())
            adapter.notifyItemInserted(exercises.size - 1)
            Workout3_RV_Exercises.scrollToPosition(exercises.size - 1)
        }

        Workout3_BTN_Save.setOnClickListener {
            saveWorkout()
        }

        Workout3_BTN_Reset.setOnClickListener {
            exercises.clear()
            adapter.notifyDataSetChanged()
            saveWorkout()
        }

        Workout3_BNT_FinishWorkout.setOnClickListener {
            saveWorkout()
            Toast.makeText(this, "Workout finished and saved!", Toast.LENGTH_SHORT).show()

        }
    }

    private fun findViews() {
        Workout3_RV_Exercises = findViewById(R.id.Workout1_RV_Exercises)
        Workout3_BTN_AddExercise = findViewById(R.id.Workout1_BTN_AddExerciseButton)
        Workout3_BTN_Save = findViewById(R.id.Workout3_BTN_Save)
        Workout3_BTN_Reset = findViewById(R.id.Workout3_BTN_Reset)
        Workout3_BNT_FinishWorkout = findViewById(R.id.Workout3_BNT_FinishWorkout)
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

    private fun saveWorkout() {
        workoutManager.saveWorkout(
            workoutName = "Workout3",
            entries = exercises,
            onSuccess = {
                Toast.makeText(this, "Workout saved!", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadWorkout() {
        workoutManager.loadWorkout(
            workoutName = "Workout3",
            onLoaded = { loaded ->
                exercises.clear()
                exercises.addAll(loaded)
                adapter.notifyDataSetChanged()
            },
            onError = { e ->
                Toast.makeText(this, "Load failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
