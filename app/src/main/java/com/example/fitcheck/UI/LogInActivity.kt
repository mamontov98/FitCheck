package com.example.fitcheck.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitcheck.R
import com.google.firebase.auth.FirebaseAuth
import com.example.fitcheck.utilities.DemoDataManager


class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var LogIn_ET_emailInput: EditText
    private lateinit var LogIn_ET_passwordInput: EditText
    private lateinit var LogIn_BTN_loginButton: Button
    private lateinit var LogIn_BTN_signupButton: Button
    private lateinit var Login_BTN_InsertDemo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = FirebaseAuth.getInstance()
        findViews()
        setupListeners()
    }


    private fun findViews() {
        LogIn_ET_emailInput = findViewById(R.id.login_ET_emailInput)
        LogIn_ET_passwordInput = findViewById(R.id.login_ET_password)
        LogIn_BTN_loginButton = findViewById(R.id.login_BTN_loginButton)
        LogIn_BTN_signupButton = findViewById(R.id.login_BTN_signupButton)
        Login_BTN_InsertDemo = findViewById(R.id.Login_BTN_InsertDemo)
    }


    private fun setupListeners() {
        LogIn_BTN_signupButton.setOnClickListener {
            // Navigate to signup activity
            startActivity(Intent(this, SignupActivity::class.java))
        }

        LogIn_BTN_loginButton.setOnClickListener {
            // Attempt login with email and password
            handleLogin()
        }

        Login_BTN_InsertDemo.setOnClickListener {
            //inset 3 demo user for debug
            DemoDataManager.insertAllDemos(this)
        }
    }

    //  Handles the user login
    private fun handleLogin() {
        val email = LogIn_ET_emailInput.text.toString().trim()
        val password = LogIn_ET_passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        //// Firebase Auth sign-in
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login success
                    startActivity(Intent(this, MainMenu::class.java))
                } else {
                    // Login failed
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
