package com.example.fpgroup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    private val auth = AuthManager // ✅ Use AuthManager directly

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val emailEditText: EditText = findViewById(R.id.emailSignUpEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordSignUpEditText)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val loginRedirect: Button = findViewById(R.id.loginRedirectButton)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (!validateInput(email, password)) return@setOnClickListener

            auth.registerUser(email, password) { success, error ->
                if (success) {
                    Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                } else {
                    showToast("Sign-Up Failed: $error")
                }
            }
        }

        loginRedirect.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                showToast("Please fill in all fields")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Please enter a valid email address")
                false
            }
            password.length < 6 -> {
                showToast("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
