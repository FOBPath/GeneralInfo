package com.example.fpgroup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var applyButton: Button
    private lateinit var saveButton: Button
    private var jobUrl: String? = null
    private var jobTitle: String? = null
    private var jobCompany: String? = null
    private var jobLocation: String? = null
    private var jobDescription: String? = null
    private var jobSalary: String? = null
    private var jobQualifications: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        val titleText: TextView = findViewById(R.id.jobTitleText)
        val companyText: TextView = findViewById(R.id.jobCompanyText)
        val locationText: TextView = findViewById(R.id.jobLocationText)
        val salaryText: TextView = findViewById(R.id.salaryText)
        val descriptionText: TextView = findViewById(R.id.descriptionText)
        val qualificationsText: TextView = findViewById(R.id.qualificationsText)

        applyButton = findViewById(R.id.applyButton)
        saveButton = findViewById(R.id.saveButton)

        // Fetch Intent extras
        jobTitle = intent.getStringExtra("JOB_TITLE")
        jobCompany = intent.getStringExtra("JOB_COMPANY")
        jobLocation = intent.getStringExtra("JOB_LOCATION")
        jobUrl = intent.getStringExtra("JOB_URL")
        jobDescription = intent.getStringExtra("JOB_DESCRIPTION")
        jobSalary = intent.getStringExtra("JOB_SALARY")
        jobQualifications = intent.getStringExtra("JOB_QUALIFICATIONS")

        // Set texts
        titleText.text = jobTitle ?: "No Title"
        companyText.text = jobCompany ?: "No Company"
        locationText.text = jobLocation ?: "No Location"
        salaryText.text = "Salary: ${jobSalary ?: "Not listed"}"

        if (!jobDescription.isNullOrBlank()) {
            descriptionText.text = "Job Summary:\n$jobDescription"
        } else {
            descriptionText.visibility = View.GONE
        }

        if (!jobQualifications.isNullOrBlank() && jobQualifications != "Not specified") {
            qualificationsText.text = "Qualifications:\n$jobQualifications"
        } else {
            qualificationsText.visibility = View.GONE
        }

        applyButton.setOnClickListener {
            openJobWebsite()
        }

        saveButton.setOnClickListener {
            saveJob()
        }
    }

    private fun openJobWebsite() {
        jobUrl?.let {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "No application link available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveJob() {
        val sharedPreferences = getSharedPreferences("SavedJobs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val existingJobs = sharedPreferences.getStringSet("jobs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        val jobData = "${jobTitle ?: ""}|${jobCompany ?: ""}|${jobLocation ?: ""}|${jobUrl ?: ""}|${jobDescription ?: ""}|${jobSalary ?: ""}|${jobQualifications ?: ""}"

        existingJobs.add(jobData)
        editor.putStringSet("jobs", existingJobs)
        editor.apply()

        Toast.makeText(this, "Job saved!", Toast.LENGTH_SHORT).show()
    }
}
