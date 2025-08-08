package com.example.fitcheck.UI

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitcheck.Adapters.WeeklyMetricAdapter
import com.example.fitcheck.R
import com.example.fitcheck.logic.DailyTrackerManager
import com.example.fitcheck.model.WeeklyMetric
import java.text.SimpleDateFormat
import java.util.*

class WeeklyMetricsActivity : AppCompatActivity() {

    private lateinit var rvMetrics: RecyclerView
    private lateinit var WeeklyMetrics_LBL_DateRange: TextView
    private lateinit var WeeklyMetrics_LBL_avgWeight: TextView
    private lateinit var WeeklyMetrics_LBL_avgCalories: TextView
    private lateinit var WeeklyMetrics_LBL_avgSleep: TextView
    private lateinit var WeeklyMetrics_LBL_avgSteps: TextView

    private val dailyTrackerManager = DailyTrackerManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_metrics)

        findViews()
        setupBottomBar()
        loadAndShowWeeklyMetrics()
    }


    private fun findViews() {
        rvMetrics = findViewById(R.id.rvMetrics)
        WeeklyMetrics_LBL_DateRange = findViewById(R.id.WeeklyMetrics_LBL_DateRange)
        WeeklyMetrics_LBL_avgWeight = findViewById(R.id.WeeklyMetrics_LBL_avgWeight)
        WeeklyMetrics_LBL_avgCalories = findViewById(R.id.WeeklyMetrics_LBL_avgCalories)
        WeeklyMetrics_LBL_avgSleep = findViewById(R.id.WeeklyMetrics_LBL_avgSleep)
        WeeklyMetrics_LBL_avgSteps = findViewById(R.id.WeeklyMetrics_LBL_avgSteps)
    }

    // Loads the last 7 days of daily tracker entries
    private fun loadAndShowWeeklyMetrics() {
        dailyTrackerManager.loadWeekEntries(
            onLoaded = { weekEntries ->
                val weeklyMetrics = listOf(
                    WeeklyMetric("Weight", weekEntries.map { it?.weight ?: 0f }),
                    WeeklyMetric("Calories", weekEntries.map { it?.calories?.toFloat() ?: 0f }),
                    WeeklyMetric("Sleep", weekEntries.map { it?.sleep?.toFloat() ?: 0f }),
                    WeeklyMetric("Steps", weekEntries.map { it?.steps?.toFloat() ?: 0f })
                )
                setupRecyclerView(weeklyMetrics)
                updateDateRange()
                updateWeeklyAverages(weeklyMetrics)
            }

        )
    }

    // Sets up the RecyclerView with a linear layout manager and adapter
    private fun setupRecyclerView(weeklyMetrics: List<WeeklyMetric>) {
        rvMetrics.layoutManager = LinearLayoutManager(this)
        rvMetrics.adapter = WeeklyMetricAdapter(weeklyMetrics)
    }

    //  Updates the the current week's date range
    private fun updateDateRange() {
        val cal = Calendar.getInstance()
        val endDate = cal.time
        cal.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = cal.time
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        WeeklyMetrics_LBL_DateRange.text = "${sdf.format(startDate)} - ${sdf.format(endDate)}"
    }

    //Updates the average values
    private fun updateWeeklyAverages(weeklyMetrics: List<WeeklyMetric>) {
        WeeklyMetrics_LBL_avgWeight.text = "%.1f".format(weeklyMetrics[0].values.average())
        WeeklyMetrics_LBL_avgCalories.text = "%.0f".format(weeklyMetrics[1].values.average())
        WeeklyMetrics_LBL_avgSleep.text = "%.1f".format(weeklyMetrics[2].values.average())
        WeeklyMetrics_LBL_avgSteps.text = "%.0f".format(weeklyMetrics[3].values.average())
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
