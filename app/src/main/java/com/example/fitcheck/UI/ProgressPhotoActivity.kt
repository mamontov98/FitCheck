package com.example.fitcheck.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fitcheck.R
import com.example.fitcheck.logic.WeeklyProgressManager
import com.google.firebase.auth.FirebaseAuth

class ProgressPhotoActivity : AppCompatActivity() {

    private lateinit var progress_TV_week_range: TextView
    private lateinit var progress_BTN_prev: Button
    private lateinit var progress_BTN_next: Button
    private lateinit var progress_BTN_upload: Button
    private lateinit var progress_IV_photo: ImageView
    private lateinit var weeklyManager: WeeklyProgressManager

    // ActivityResultLauncher for picking an image from the gallery
    private val pickMediaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        Log.d("ProgressPhoto", "pickMediaLauncher result: $uri")
        weeklyManager.onMediaPicked(uri)
    }

    //  ActivityResultLauncher for requesting storage permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
            weeklyManager.pickImageFromGallery()
        } else {
            Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_photo)

        initViews()
        setupBottomBar()
        setupWeeklyManager()
        setupButton()
        updateUI()
    }

    private fun initViews() {
        progress_TV_week_range = findViewById(R.id.progress_TV_week_range)
        progress_BTN_prev = findViewById(R.id.progress_BTN_prev)
        progress_BTN_next = findViewById(R.id.progress_BTN_next)
        progress_BTN_upload = findViewById(R.id.progress_BTN_upload)
        progress_IV_photo = findViewById(R.id.progress_IV_photo)
    }

    //Init WeeklyProgressManager
    private fun setupWeeklyManager() {
        weeklyManager = WeeklyProgressManager(this)
        weeklyManager.setPickMediaLauncher(pickMediaLauncher)
        weeklyManager.setImageView(progress_IV_photo)
        weeklyManager.setOnImageUploadedCallback {
            Toast.makeText(this, "Successfully Upload Photo", Toast.LENGTH_SHORT).show()
            updateUI()
        }
    }

    private fun setupButton() {
        progress_BTN_prev.setOnClickListener {
            val newWeek = weeklyManager.getPreviousWeek()
            weeklyManager.updateWeek(newWeek)
            updateUI()
        }

        progress_BTN_next.setOnClickListener {
            val newWeek = weeklyManager.getNextWeek()
            weeklyManager.updateWeek(newWeek)
            updateUI()
        }

        progress_BTN_upload.setOnClickListener {
            Log.d("ProgressPhoto", "Upload button clicked, isCurrentWeek: ${weeklyManager.isCurrentWeek()}")
            if (weeklyManager.isCurrentWeek()) {
                checkPermissionAndPickImage()
            } else {
                Toast.makeText(this, "You can only upload a photo for the current week", Toast.LENGTH_SHORT).show()
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

    //displays the current week and loads the relevant photo
    private fun updateUI() {
        progress_TV_week_range.text = weeklyManager.getFormattedWeekRange()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            weeklyManager.loadImageForCurrentWeek(userId)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // Checks for storage permission. If granted, opens the gallery
    private fun checkPermissionAndPickImage() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        Log.d("ProgressPhoto", "Checking permission: $permission")

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("ProgressPhoto", "Permission granted")
                weeklyManager.pickImageFromGallery()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Log.d("ProgressPhoto", "Should show permission rationale")
                Toast.makeText(this, "We need permission to access your images", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(permission)
            }
            else -> {
                Log.d("ProgressPhoto", "Requesting permission")
                requestPermissionLauncher.launch(permission)
            }
        }
    }

}
