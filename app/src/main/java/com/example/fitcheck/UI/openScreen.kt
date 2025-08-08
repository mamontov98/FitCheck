package com.example.fitcheck.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.fitcheck.R
import com.example.fitcheck.utilities.AppConstants

class openScreen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_open_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }, AppConstants.Timing.SPLASH_DELAY_MILLIS)


    }
}