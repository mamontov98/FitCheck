package com.example.fitcheck.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.fitcheck.R

class MainMenu : AppCompatActivity() {
    //init
    private lateinit var Menu_BTN_goals: Button
    private lateinit var Menu_BTN_dataEntry: Button
    private lateinit var Menu_BTN_workouts: Button
    private lateinit var Menu_BTN_metrics: Button
    private lateinit var Menu_BTN_checkup: Button
    private lateinit var Menu_BTN_Logout: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        findViews()
        setUpButtons()

    }

    private fun findViews() {
        Menu_BTN_goals = findViewById(R.id.Menu_BTN_goals)
        Menu_BTN_dataEntry = findViewById(R.id.Menu_BTN_dataEntry)
        Menu_BTN_workouts = findViewById(R.id.Menu_BTN_workouts)
        Menu_BTN_checkup = findViewById(R.id.Menu_BTN_checkup)
        Menu_BTN_metrics = findViewById(R.id.Menu_BTN_metrics)
        Menu_BTN_Logout = findViewById(R.id.MainMenu_BTN_Logout)

    }

    private fun setUpButtons() {
        Menu_BTN_goals.setOnClickListener {
            val intent = Intent(this, SetUpGoals::class.java)
            startActivity(intent)
        }

        Menu_BTN_dataEntry.setOnClickListener {
            val intent = Intent(this, DailyTrackerActivity::class.java)
            startActivity(intent)
        }

        Menu_BTN_workouts.setOnClickListener {
            val intent = Intent(this, WorkoutMenuActivity::class.java)
            startActivity(intent)
        }

        Menu_BTN_checkup.setOnClickListener {
            val intent = Intent(this, WeeklyCheckUpActivity::class.java)
            startActivity(intent)
        }

        Menu_BTN_metrics.setOnClickListener {
            val intent = Intent(this, WeeklyMetricsActivity::class.java)
            startActivity(intent)
        }
        Menu_BTN_Logout.setOnClickListener {
            logoutAndGoToLogin()
        }


    }

    //Logs out the user from Firebase Auth
    private fun logoutAndGoToLogin() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}