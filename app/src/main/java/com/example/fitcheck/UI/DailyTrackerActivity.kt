package com.example.fitcheck.UI

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fitcheck.R
import com.example.fitcheck.logic.DailyTrackerManager
import com.example.fitcheck.logic.GoalsManager
import com.example.fitcheck.model.DailyTrackerEntry
import com.example.fitcheck.model.Goals

class DailyTrackerActivity : AppCompatActivity() {

    private val dailyTrackerManager = DailyTrackerManager()
    private val goalsManager = GoalsManager()

    private lateinit var DailyTrackerEntry_ET_Calories: TextView
    private lateinit var DailyTrackerEntry_ET_Carbs: EditText
    private lateinit var DailyTrackerEntry_ET_Protein: EditText
    private lateinit var DailyTrackerEntry_ET_Fat: EditText
    private lateinit var DailyTrackerEntry_ET_Steps: EditText
    private lateinit var DailyTrackerEntry_ET_Sleep: EditText
    private lateinit var DailyTrackerEntry_ET_Weight: EditText

    private lateinit var progressBarCalories: ProgressBar
    private lateinit var ProgressBarCarbs: ProgressBar
    private lateinit var ProgressBarProtein: ProgressBar
    private lateinit var ProgressBarFat: ProgressBar
    private lateinit var ProgressBarSteps: ProgressBar
    private lateinit var ProgressBarSleep: ProgressBar

    private lateinit var DailyTrackerEntry_LBL_MaxCalories: TextView
    private lateinit var DailyTrackerEntry_LBL_MaxCarbs: TextView
    private lateinit var DailyTrackerEntry_LBL_MaxProtein: TextView
    private lateinit var DailyTrackerEntry_LBL_MaxFat: TextView
    private lateinit var DailyTrackerEntry_LBL_MaxSleep: TextView
    private lateinit var DailyTrackerEntry_LBL_MaxSteps: TextView

    private lateinit var DailyTrackerEntry_BTN_Save: Button
    private lateinit var DailyTrackerEntry_BTN_Reset: Button

    // User's goals
    private var lastGoals:Goals? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_tracker)

        findViews()
        setUpButtons()
        setupBottomBar()

        // Load user goals then load today's daily entry
        goalsManager.loadGoals(
            onLoaded = { goals ->
                lastGoals = goals
                setGoalsLimits(goals)
                loadDailyEntry()
                setupAutoUpdateProgress()
            },
            onError = {
                Toast.makeText(this, "Failed to load goals!", Toast.LENGTH_SHORT).show()
            }
        )


    }

    private fun setUpButtons() {
        DailyTrackerEntry_BTN_Save.setOnClickListener {
            saveEntryFromUI()
        }

        DailyTrackerEntry_BTN_Reset.setOnClickListener {
            dailyTrackerManager.resetEntry(
                onSuccess = {
                    loadDailyEntry()
                    Toast.makeText(this, "Daily values reset!", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(this, "Failed to reset daily entry!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun findViews() {
        DailyTrackerEntry_ET_Calories = findViewById(R.id.DailyTrackerEntry_ET_Calories)
        DailyTrackerEntry_ET_Carbs = findViewById(R.id.DailyTrackerEntry_ET_Carbs)
        DailyTrackerEntry_ET_Protein = findViewById(R.id.DailyTrackerEntry_ET_Protein)
        DailyTrackerEntry_ET_Fat = findViewById(R.id.DailyTrackerEntry_ET_Fat)
        DailyTrackerEntry_ET_Steps = findViewById(R.id.DailyTrackerEntry_ET_Steps)
        DailyTrackerEntry_ET_Sleep = findViewById(R.id.DailyTrackerEntry_ET_Sleep)
        DailyTrackerEntry_ET_Weight = findViewById(R.id.DailyTrackerEntry_ET_Weight)

        progressBarCalories = findViewById(R.id.progress_calories)
        ProgressBarCarbs = findViewById(R.id.progress_carbs)
        ProgressBarProtein = findViewById(R.id.progress_protein)
        ProgressBarFat = findViewById(R.id.progress_fat)
        ProgressBarSteps = findViewById(R.id.progress_steps)
        ProgressBarSleep = findViewById(R.id.progress_sleep)

        DailyTrackerEntry_LBL_MaxCalories = findViewById(R.id.DailyTrackerEntry_LBL_MaxCalories)
        DailyTrackerEntry_LBL_MaxCarbs = findViewById(R.id.DailyTrackerEntry_LBL_MaxCarbs)
        DailyTrackerEntry_LBL_MaxProtein = findViewById(R.id.DailyTrackerEntry_LBL_MaxProtein)
        DailyTrackerEntry_LBL_MaxFat = findViewById(R.id.DailyTrackerEntry_LBL_MaxFat)
        DailyTrackerEntry_LBL_MaxSteps = findViewById(R.id.DailyTrackerEntry_LBL_MaxSteps)
        DailyTrackerEntry_LBL_MaxSleep = findViewById(R.id.DailyTrackerEntry_LBL_MaxSleep)
        DailyTrackerEntry_BTN_Reset = findViewById(R.id.DailytrackerEntry_BTN_Reset)
        DailyTrackerEntry_BTN_Save = findViewById(R.id.DailytrackerEntry_BTN_Save)
    }

    // Sets progress bar max values and UI labels according to user goals
    private fun setGoalsLimits(goals: Goals) {
        val maxCalories =
            if (goals.calories > 0)
            {
                goals.calories
            }
            else
            {
                goalsManager.calculateCalories(goals.carbs, goals.protein, goals.fat)
            }

        progressBarCalories.max = maxCalories
        ProgressBarCarbs.max = goals.carbs
        ProgressBarProtein.max = goals.protein
        ProgressBarFat.max = goals.fat
        ProgressBarSteps.max = goals.steps
        ProgressBarSleep.max = goals.sleep

        DailyTrackerEntry_LBL_MaxCalories.text = "${maxCalories} kal"
        DailyTrackerEntry_LBL_MaxCarbs.text = "${goals.carbs} g"
        DailyTrackerEntry_LBL_MaxProtein.text = "${goals.protein} g"
        DailyTrackerEntry_LBL_MaxFat.text = "${goals.fat} g"
        DailyTrackerEntry_LBL_MaxSteps.text = "${goals.steps} steps"
        DailyTrackerEntry_LBL_MaxSleep.text = "${goals.sleep} h"
    }

    //Loads today's daily entry from the cloud and updates all UI fields
    private fun loadDailyEntry() {
        dailyTrackerManager.loadEntry(
            onLoaded = { entry ->
                DailyTrackerEntry_ET_Calories.text = entry.calories.toString()
                DailyTrackerEntry_ET_Carbs.setText(entry.carbs.toString())
                DailyTrackerEntry_ET_Protein.setText(entry.protein.toString())
                DailyTrackerEntry_ET_Fat.setText(entry.fat.toString())
                DailyTrackerEntry_ET_Steps.setText(entry.steps.toString())
                DailyTrackerEntry_ET_Sleep.setText(entry.sleep.toString())
                DailyTrackerEntry_ET_Weight.setText(entry.weight.toString())
                updateAllProgress()
            },
            onError = {
                Toast.makeText(this, "Failed to load daily entry!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Saves the current input values as today's entry and updates progress
    private fun saveEntryFromUI() {
        lastGoals?.let { goals ->
            val carbs = DailyTrackerEntry_ET_Carbs.text.toString().toIntOrNull() ?: 0
            val protein = DailyTrackerEntry_ET_Protein.text.toString().toIntOrNull() ?: 0
            val fat = DailyTrackerEntry_ET_Fat.text.toString().toIntOrNull() ?: 0
            val calories = goalsManager.calculateCalories(carbs, protein, fat)

            val entry = DailyTrackerEntry(
                carbs = carbs,
                protein = protein,
                fat = fat,
                steps = DailyTrackerEntry_ET_Steps.text.toString().toIntOrNull() ?: 0,
                sleep = DailyTrackerEntry_ET_Sleep.text.toString().toIntOrNull() ?: 0,
                weight = DailyTrackerEntry_ET_Weight.text.toString().toFloatOrNull() ?: 0f,
                calories = calories
            )
            dailyTrackerManager.saveEntry(entry,
                onSuccess = {
                    updateAllProgress()
                    giveFeedback(entry)
                    Toast.makeText(this, "Progress saved!", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(this, "Failed to save progress!", Toast.LENGTH_SHORT).show()
                }
            )
        } ?: run {
            Toast.makeText(this, "Goals not loaded yet!", Toast.LENGTH_SHORT).show()
        }
    }

    //Adds a TextWatcher to relevant input fields for live update of all progress bars
    private fun setupAutoUpdateProgress() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateAllProgress() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        DailyTrackerEntry_ET_Carbs.addTextChangedListener(watcher)
        DailyTrackerEntry_ET_Protein.addTextChangedListener(watcher)
        DailyTrackerEntry_ET_Fat.addTextChangedListener(watcher)
        DailyTrackerEntry_ET_Steps.addTextChangedListener(watcher)
        DailyTrackerEntry_ET_Sleep.addTextChangedListener(watcher)
    }

    //Calculates calories and updates
    private fun updateAllProgress() {
        lastGoals?.let { goals ->
            val carbs = DailyTrackerEntry_ET_Carbs.text.toString().toIntOrNull() ?: 0
            val protein = DailyTrackerEntry_ET_Protein.text.toString().toIntOrNull() ?: 0
            val fat = DailyTrackerEntry_ET_Fat.text.toString().toIntOrNull() ?: 0
            val calories = goalsManager.calculateCalories(carbs, protein, fat)
            val steps = DailyTrackerEntry_ET_Steps.text.toString().toIntOrNull() ?: 0
            val sleep = DailyTrackerEntry_ET_Sleep.text.toString().toIntOrNull() ?: 0

            // Live update of calories view
            DailyTrackerEntry_ET_Calories.text = calories.toString()

            // Live update of progress bars
            progressBarCalories.progress = calories
            ProgressBarCarbs.progress = carbs
            ProgressBarProtein.progress = protein
            ProgressBarFat.progress = fat
            ProgressBarSteps.progress = steps
            ProgressBarSleep.progress = sleep
        }
    }

    //Shows  feedback in a Toast if user reaches any of their goals
    private fun giveFeedback(entry: DailyTrackerEntry) {
        lastGoals?.let { goals ->
            var feedback = ""
            if (entry.carbs >= goals.carbs) feedback += "Carbs goal reached!\n"
            if (entry.protein >= goals.protein) feedback += "Protein goal reached!\n"
            if (entry.fat >= goals.fat) feedback += "Fat goal reached!\n"
            if (entry.steps >= goals.steps) feedback += "Steps goal reached!\n"
            if (entry.sleep >= goals.sleep) feedback += "Sleep goal reached!\n"
            if (feedback.isNotEmpty()) {
                Toast.makeText(this, feedback.trim(), Toast.LENGTH_LONG).show()
            }
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
}
