package com.example.fitcheck.UI

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fitcheck.R
import com.example.fitcheck.logic.GoalsManager
import com.example.fitcheck.model.Goals

class SetUpGoals : AppCompatActivity() {

    private lateinit var setUpGoals_ET_calories: TextView
    private lateinit var setUpGoals_ET_carbs: EditText
    private lateinit var setUpGoals_ET_protein: EditText
    private lateinit var setUpGoals_ET_fat: EditText
    private lateinit var setUpGoals_ET_steps: EditText
    private lateinit var setUpGoals_ET_sleep: EditText
    private lateinit var setUpGoals_ET_weight: EditText
    private lateinit var setUpGoals_BTN_save_goals: Button

    private val goalsManager = GoalsManager() // **בלי קונטקסט**

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up_goals)

        findViews()
        loadGoalsToFields()
        setupButton()
        setupBottomBar()
        setupCaloriesCalculator()
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

    private fun findViews() {
        setUpGoals_ET_calories = findViewById(R.id.setUpGoals_ET_calories)
        setUpGoals_ET_carbs = findViewById(R.id.setUpGoals_ET_carbs)
        setUpGoals_ET_protein = findViewById(R.id.setUpGoals_ET_protein)
        setUpGoals_ET_fat = findViewById(R.id.setUpGoals_ET_fat)
        setUpGoals_ET_steps = findViewById(R.id.setUpGoals_ET_steps)
        setUpGoals_ET_sleep = findViewById(R.id.setUpGoals_ET_sleep)
        setUpGoals_ET_weight = findViewById(R.id.setUpGoals_ET_weight)
        setUpGoals_BTN_save_goals = findViewById(R.id.setUpGoals_BTN_save_goals)
    }

    //Loads user goals from the cloud
    private fun loadGoalsToFields() {
        goalsManager.loadGoals({ goals ->
            setUpGoals_ET_calories.text = goalsManager.calculateCalories(
                goals.carbs, goals.protein, goals.fat
            ).toString()
            setUpGoals_ET_carbs.setText(goals.carbs.toString())
            setUpGoals_ET_protein.setText(goals.protein.toString())
            setUpGoals_ET_fat.setText(goals.fat.toString())
            setUpGoals_ET_steps.setText(goals.steps.toString())
            setUpGoals_ET_sleep.setText(goals.sleep.toString())
            setUpGoals_ET_weight.setText(goals.weight.toString())
        }, { error ->
            Toast.makeText(this, "Failed to load goals: ${error.message}", Toast.LENGTH_SHORT).show()
        })
    }

    private fun setupButton() {
        setUpGoals_BTN_save_goals.setOnClickListener {
            val carbs = setUpGoals_ET_carbs.text.toString().toIntOrNull() ?: 0
            val protein = setUpGoals_ET_protein.text.toString().toIntOrNull() ?: 0
            val fat = setUpGoals_ET_fat.text.toString().toIntOrNull() ?: 0
            val calories = goalsManager.calculateCalories(carbs, protein, fat)

            val updatedGoals = Goals(
                carbs = carbs,
                protein = protein,
                fat = fat,
                steps = setUpGoals_ET_steps.text.toString().toIntOrNull() ?: 0,
                sleep = setUpGoals_ET_sleep.text.toString().toIntOrNull() ?: 0,
                weight = setUpGoals_ET_weight.text.toString().toFloatOrNull() ?: 0f,
                calories = calories
            )
            goalsManager.saveGoals(updatedGoals,
                onSuccess = {
                    Toast.makeText(this, "Goals saved!", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(this, "Failed to save goals: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Every change triggers updateCaloriesView to update calories live
    private fun setupCaloriesCalculator() {
        val watcher = object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                updateCaloriesView()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        setUpGoals_ET_carbs.addTextChangedListener(watcher)
        setUpGoals_ET_protein.addTextChangedListener(watcher)
        setUpGoals_ET_fat.addTextChangedListener(watcher)
    }

    //Calculates calories based on the latest macros
    private fun updateCaloriesView() {
        val carbs = setUpGoals_ET_carbs.text.toString().toIntOrNull() ?: 0
        val protein = setUpGoals_ET_protein.text.toString().toIntOrNull() ?: 0
        val fat = setUpGoals_ET_fat.text.toString().toIntOrNull() ?: 0

        val calories = goalsManager.calculateCalories(carbs, protein, fat)
        setUpGoals_ET_calories.text = calories.toString()
    }
}
