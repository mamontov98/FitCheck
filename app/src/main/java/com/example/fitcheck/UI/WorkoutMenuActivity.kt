package com.example.fitcheck.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.fitcheck.R

class WorkoutMenuActivity : AppCompatActivity() {

    private lateinit var WorkoutMenu_BTN_Workout1: Button
    private lateinit var WorkoutMenu_BTN_Workout2: Button
    private lateinit var WorkoutMenu_BTN_Workout3: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout_menu)

        findViews()
        setupButtons()
        setupBottomBar()

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

    private fun setupButtons() {
        WorkoutMenu_BTN_Workout1.setOnClickListener {
            val intent = Intent(this, Workout1Activity::class.java)
            startActivity(intent)
        }

        WorkoutMenu_BTN_Workout2.setOnClickListener {
            val intent = Intent(this, Workout2Activity::class.java)
            startActivity(intent)
        }

        WorkoutMenu_BTN_Workout3.setOnClickListener {
            val intent = Intent(this, Workout3Activity::class.java)
            startActivity(intent)
        }
    }

    private fun findViews() {
        WorkoutMenu_BTN_Workout1 = findViewById(R.id.WorkoutMenu_BTN_Workout1)
        WorkoutMenu_BTN_Workout2 = findViewById(R.id.WorkoutMenu_BTN_Workout2)
        WorkoutMenu_BTN_Workout3 = findViewById(R.id.WorkoutMenu_BTN_Workout3)
    }


}