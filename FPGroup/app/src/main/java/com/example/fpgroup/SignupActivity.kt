package com.example.fpgroup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val emailEditText = findViewById<EditText>(R.id.emailSignUpEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordSignUpEditText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Simulate successful sign-up
                Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()




                // Navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_EMAIL", email)
                startActivity(intent)
                finish()
            }
        }
    }
}
