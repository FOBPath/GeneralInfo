package com.example.fpgroup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ApplicationSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_success)

        val successText = findViewById<TextView>(R.id.successMessage)
        val backButton = findViewById<Button>(R.id.returnHomeButton)

        // Optional: Show personalized message
        val name = intent.getStringExtra("name")
        if (!name.isNullOrEmpty()) {
            successText.text = "Thanks, $name!\nYour application has been submitted!"
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("SELECTED_TAB", R.id.nav_home)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}

