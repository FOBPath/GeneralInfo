package com.example.fpgroup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class JobDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        val jobTitle: TextView = findViewById(R.id.detailJobTitle)
        val jobCompany: TextView = findViewById(R.id.detailJobCompany)
        val jobLocation: TextView = findViewById(R.id.detailJobLocation)
        val jobDescription: TextView = findViewById(R.id.detailJobDescription)
        val applyButton: Button = findViewById(R.id.applyButton)

        val title = intent.getStringExtra("JOB_TITLE")
        val company = intent.getStringExtra("JOB_COMPANY")
        val location = intent.getStringExtra("JOB_LOCATION")
        val description = intent.getStringExtra("JOB_DESCRIPTION")
        val jobUrl = intent.getStringExtra("JOB_URL")  // Link to job application

        jobTitle.text = title
        jobCompany.text = company
        jobLocation.text = location
        jobDescription.text = description

        applyButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(jobUrl))
            startActivity(intent)
        }
    }
}
