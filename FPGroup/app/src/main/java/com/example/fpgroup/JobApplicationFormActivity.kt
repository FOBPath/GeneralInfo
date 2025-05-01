package com.example.fpgroup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JobApplicationFormActivity : AppCompatActivity() {

    private val RESUME_PICK_CODE = 2005
    private var selectedResumeUri: Uri? = null
    private lateinit var submitButton: Button
    private lateinit var jobTitle: String
    private var jobUrl: String? = null
    private lateinit var resumeStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_application_form)

        // Retrieve job data from intent
        jobTitle = intent.getStringExtra("JOB_TITLE") ?: "Unknown"
        jobUrl = intent.getStringExtra("JOB_URL")

        val selectResumeBtn: Button = findViewById(R.id.selectResumeButton)
        resumeStatusText = findViewById(R.id.resumeStatusText)

        // Resume picker
        selectResumeBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, RESUME_PICK_CODE)
        }

        // Submit button logic (enabled by default)
        submitButton = findViewById<Button>(R.id.submitResumeButton).apply {
            isEnabled = true
            isClickable = true

            setOnClickListener {
                Log.d("JobForm", "Submit button clicked")
                Toast.makeText(context, "Submitting application...", Toast.LENGTH_SHORT).show()

                if (jobTitle.isBlank()) {
                    Toast.makeText(context, "Job title is missing!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                saveApplicationWithoutResume()
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESUME_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedResumeUri = data?.data
            resumeStatusText.text = "Resume selected ✔️"
        }
    }

    private fun saveApplicationWithoutResume() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val email = currentUser.email ?: "unknown"

        val application = mapOf(
            "userId" to userId,
            "email" to email,
            "jobTitle" to jobTitle,
            "jobUrl" to jobUrl,
            "resumeUri" to (selectedResumeUri?.toString() ?: "None"),
            "status" to "Submitted",
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance().collection("applications")
            .add(application)
            .addOnSuccessListener {
                Log.d("JobForm", "Application saved to Firestore")
                FirestoreHelper.sendEmailConfirmation(this, email, jobTitle, jobUrl ?: "")
                val intent = Intent(this, ApplicationSuccessActivity::class.java)
                intent.putExtra("name", email)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Log.e("JobForm", "Firestore save failed", it)
                Toast.makeText(this, "Failed to save application", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
