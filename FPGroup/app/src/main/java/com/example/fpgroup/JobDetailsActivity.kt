package com.example.fpgroup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class JobDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        val jobTitle = intent.getStringExtra("JOB_TITLE")
        val jobCompany = intent.getStringExtra("JOB_COMPANY")
        val jobLocation = intent.getStringExtra("JOB_LOCATION")
        val jobDescription = intent.getStringExtra("JOB_DESCRIPTION")
        val jobUrl = intent.getStringExtra("JOB_URL")

        findViewById<TextView>(R.id.detailJobTitle).text = jobTitle
        findViewById<TextView>(R.id.detailJobCompany).text = jobCompany
        findViewById<TextView>(R.id.detailJobLocation).text = jobLocation
        findViewById<TextView>(R.id.detailJobDescription).text = jobDescription

        val applyButton = findViewById<Button>(R.id.applyButton)
        val saveButton = findViewById<Button>(R.id.saveJobButton) // New save button

        applyButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(jobUrl))
            startActivity(intent)
            saveAppliedJob(jobTitle, jobCompany, jobLocation, jobUrl) // Save the job when applying
        }

        saveButton.setOnClickListener {
            saveAppliedJob(jobTitle, jobCompany, jobLocation, jobUrl)
        }
    }

    private fun saveAppliedJob(title: String?, company: String?, location: String?, url: String?) {
        if (title == null || company == null || location == null || url == null) {
            Toast.makeText(this, "Job details are incomplete", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = getSharedPreferences("AppliedJobs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val jobsSet = sharedPreferences.getStringSet("jobs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        jobsSet.add("$title|$company|$location|$url")

        editor.putStringSet("jobs", jobsSet)
        editor.apply()

        Toast.makeText(this, "Job saved successfully!", Toast.LENGTH_SHORT).show()
    }
}
