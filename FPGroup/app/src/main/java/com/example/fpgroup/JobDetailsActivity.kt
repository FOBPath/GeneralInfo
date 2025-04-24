package com.example.fpgroup

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class JobDetailsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        val jobTitle = intent.getStringExtra("JOB_TITLE")
        val jobCompany = intent.getStringExtra("JOB_COMPANY")
        val jobLocation = intent.getStringExtra("JOB_LOCATION")
        val jobDescription = intent.getStringExtra("JOB_DESCRIPTION")
        val jobUrl = intent.getStringExtra("JOB_URL")
        val jobSalary = intent.getStringExtra("JOB_SALARY") ?: "Not listed"
        val jobQualifications = intent.getStringExtra("JOB_QUALIFICATIONS") ?: "Not specified"

        // Populate basic info
        findViewById<TextView>(R.id.detailJobTitle).text = jobTitle
        findViewById<TextView>(R.id.detailJobCompany).text = jobCompany
        findViewById<TextView>(R.id.detailJobLocation).text = jobLocation
        findViewById<TextView>(R.id.detailJobSalary).text = "Salary: $jobSalary"
        findViewById<TextView>(R.id.detailJobQualifications).text = "Qualifications: $jobQualifications"

        // Parse job description into sections
        val summarySection = Regex("(?i)summary:").split(jobDescription ?: "").getOrNull(1)
            ?.split(Regex("(?i)skills:"))?.getOrNull(0)?.trim()
        val skillsSection = Regex("(?i)skills:").split(jobDescription ?: "").getOrNull(1)
            ?.split(Regex("(?i)benefits:"))?.getOrNull(0)?.trim()
        val benefitsSection = Regex("(?i)benefits:").split(jobDescription ?: "").getOrNull(1)?.trim()

        findViewById<TextView>(R.id.detailJobSummary).text = summarySection ?: "No summary available"
        findViewById<TextView>(R.id.detailJobSkills).text = skillsSection ?: "No skills listed"
        findViewById<TextView>(R.id.detailJobBenefits).text = benefitsSection ?: "No benefits provided"

        // Apply button opens external job URL
        findViewById<Button>(R.id.applyButton).setOnClickListener {
            if (!jobUrl.isNullOrEmpty()) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(jobUrl))
                startActivity(browserIntent)
            } else {
                Toast.makeText(this, "No application link provided", Toast.LENGTH_SHORT).show()
            }
        }

        // Save Job button saves to SharedPreferences
        findViewById<Button>(R.id.saveJobButton).setOnClickListener {
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
