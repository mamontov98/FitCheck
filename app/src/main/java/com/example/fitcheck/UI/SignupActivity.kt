package com.example.fitcheck.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitcheck.R
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    //init
    private lateinit var auth: FirebaseAuth
    private lateinit var SingUp_ET_emailInput: EditText
    private lateinit var SingUp_ET_passwordInput: EditText
    private lateinit var SingUp_BTN_signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        //init FBAuth
        auth = FirebaseAuth.getInstance()
        findViews()
        setupListeners()
    }


    private fun findViews() {
        SingUp_ET_emailInput = findViewById(R.id.SingUp_ET_emailInput)
        SingUp_ET_passwordInput = findViewById(R.id.SingUp_ET_password)
        SingUp_BTN_signupButton = findViewById(R.id.SignUp_BTN_signupButton)
    }


    private fun setupListeners() {
        SingUp_BTN_signupButton.setOnClickListener {
            handleSignup()
        }
    }

    // andles the user sign-up
    private fun handleSignup() {
        val email = SingUp_ET_emailInput.text.toString().trim()
        val password = SingUp_ET_passwordInput.text.toString().trim()

        // Validate input fields
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        // Password must be at least 6 characters
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // Register the user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registered successfully! Please log in.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LogInActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
