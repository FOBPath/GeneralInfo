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

        val jobTitle = intent.getStringExtra("JOB_TITLE")
        val jobCompany = intent.getStringExtra("JOB_COMPANY")

        val successMessage = findViewById<TextView>(R.id.successMessage)
        successMessage.text = "You have successfully applied for $jobTitle at $jobCompany!"

        val returnHomeButton = findViewById<Button>(R.id.returnHomeButton)
        returnHomeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("SELECTED_TAB", R.id.nav_jobs)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
