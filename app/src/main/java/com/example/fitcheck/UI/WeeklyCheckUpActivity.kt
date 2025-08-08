package com.example.fitcheck.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.fitcheck.R
import com.example.fitcheck.logic.DailyTrackerManager
import com.example.fitcheck.logic.GoalsManager
import com.example.fitcheck.utilities.CheckupPdfManager
import com.example.fitcheck.utilities.DateHelper
import com.example.fitcheck.utilities.EmailManager
import java.util.Date

class WeeklyCheckUpActivity : AppCompatActivity() {

    //init
    private lateinit var checkup_BTN_send: Button
    private lateinit var checkup_BTN_Photo_Progress: Button
    private lateinit var etName: EditText
    private lateinit var etPositive: EditText
    private lateinit var etEnergy: EditText
    private lateinit var etRecovery: EditText
    private lateinit var etHunger: EditText
    private lateinit var etMood: EditText
    private lateinit var etStress: EditText
    private lateinit var etNutritionChallenge: EditText
    private lateinit var etWorkoutChallenge: EditText
    private lateinit var etSupport: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weekly_check_up)

        findViews()
        setupButtons()
        setupBottomBar()
    }

    private fun setupButtons() {
        checkup_BTN_send.setOnClickListener {
            Toast.makeText(this, "Send button clicked!", Toast.LENGTH_SHORT).show()

            //calc date and create range
            val currentWeekStart = DateHelper.getStartOfWeek(Date())
            val weekRange = DateHelper.getFormattedWeekRange(currentWeekStart)

            //crete map from questionnaire
            val questionnaire = mapOf(
                "Name" to etName.text.toString(),
                "Positive thing this week" to etPositive.text.toString(),
                "Energy (1-10)" to etEnergy.text.toString(),
                "Recovery (1-10)" to etRecovery.text.toString(),
                "Hunger (1-10)" to etHunger.text.toString(),
                "Mood (1-10)" to etMood.text.toString(),
                "Stress (1-10)" to etStress.text.toString(),
                "Nutrition challenges" to etNutritionChallenge.text.toString(),
                "Workout challenges" to etWorkoutChallenge.text.toString(),
                "Coach support needed" to etSupport.text.toString()
            )

            val dailyTrackerManager = DailyTrackerManager()
            val goalsManager = GoalsManager()

            //load user goals
            goalsManager.loadGoals({ goals ->

                val goalsMap = mapOf(
                    "Weight Goal" to "${goals.weight} kg",
                    "Calories Goal" to "${goals.calories} kcal",
                    "Sleep Goal" to "${goals.sleep} h",
                    "Steps Goal" to "${goals.steps}"
                )

                //load weekly data
                dailyTrackerManager.loadWeekEntries({ weekEntries ->

                    val weights = weekEntries.mapNotNull { it?.weight?.toDouble() }
                    val calories = weekEntries.mapNotNull { it?.calories?.toDouble() }
                    val sleep = weekEntries.mapNotNull { it?.sleep?.toDouble() }
                    val steps = weekEntries.mapNotNull { it?.steps?.toDouble() }

                    //calc average
                    val averagesMap = mapOf(
                        "Average Weight" to "%.1f kg".format(if (weights.isNotEmpty()) weights.average() else 0.0),
                        "Average Calories" to "%.0f kcal".format(if (calories.isNotEmpty()) calories.average() else 0.0),
                        "Average Sleep" to "%.1f h".format(if (sleep.isNotEmpty()) sleep.average() else 0.0),
                        "Average Steps" to "%.0f".format(if (steps.isNotEmpty()) steps.average() else 0.0)
                    )

                    //generate PDF
                    val pdfFile = CheckupPdfManager.generatePdf(
                        context = this,
                        weekRange = weekRange,
                        averages = averagesMap,
                        goals = goalsMap,
                        questionnaire = questionnaire
                    )
                    Toast.makeText(this, "Generating PDF and sending...", Toast.LENGTH_SHORT).show()

                    //send email
                    EmailManager.sendEmailWithPdf(
                        context = this,
                        pdfFile = pdfFile,
                        coachEmail = "dani.mamontov98@gmail.com"
                    )

                }, { error ->
                    Toast.makeText(this, "Failed to load weekly data", Toast.LENGTH_SHORT).show()
                })

            }, { error ->
                Toast.makeText(this, "Failed to load goals", Toast.LENGTH_SHORT).show()
            })
        }

        checkup_BTN_Photo_Progress.setOnClickListener {
            val intent = Intent(this, ProgressPhotoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun findViews() {
        checkup_BTN_send = findViewById(R.id.checkup_BTN_send)
        checkup_BTN_Photo_Progress = findViewById(R.id.checkup_BTN_Photo_Progress)

        etName = findViewById(R.id.checkup_ET_name)
        etPositive = findViewById(R.id.checkup_ET_positive)
        etEnergy = findViewById(R.id.checkup_ET_energy)
        etRecovery = findViewById(R.id.checkup_ET_recovery)
        etHunger = findViewById(R.id.checkup_ET_hunger)
        etMood = findViewById(R.id.checkup_ET_mood)
        etStress = findViewById(R.id.checkup_ET_stress)
        etNutritionChallenge = findViewById(R.id.checkup_ET_challenge_nutrition)
        etWorkoutChallenge = findViewById(R.id.checkup_ET_challenge_workouts)
        etSupport = findViewById(R.id.checkup_ET_support)
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
