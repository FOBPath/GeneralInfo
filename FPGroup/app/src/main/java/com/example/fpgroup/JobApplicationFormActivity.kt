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
import java.io.BufferedReader
import java.io.InputStreamReader

class JobApplicationFormActivity : AppCompatActivity() {

    private val RESUME_PICK_CODE = 2005
    private var selectedResumeUri: Uri? = null
    private lateinit var resumeStatusText: TextView
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_application_form)

        // Job data from intent
        val jobTitle = intent.getStringExtra("JOB_TITLE") ?: "Unknown"
        val jobCompany = intent.getStringExtra("JOB_COMPANY") ?: "Unknown"
        val jobLocation = intent.getStringExtra("JOB_LOCATION") ?: "N/A"
        val jobSalary = intent.getStringExtra("JOB_SALARY") ?: "Not listed"
        val jobQualifications = intent.getStringExtra("JOB_QUALIFICATIONS") ?: "Not specified"
        val jobUrl = intent.getStringExtra("JOB_URL") ?: "N/A"

        // Input fields
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val educationSpinner = findViewById<Spinner>(R.id.educationSpinner)
        val experienceEditText = findViewById<EditText>(R.id.experienceEditText)
        val coverLetterEditText = findViewById<EditText>(R.id.coverLetterEditText)
        val linkedinEditText = findViewById<EditText>(R.id.linkedinEditText)
        val portfolioEditText = findViewById<EditText>(R.id.portfolioEditText)
        resumeStatusText = findViewById(R.id.resumeStatusText)
        val selectResumeBtn = findViewById<Button>(R.id.selectResumeButton)

        // Submit button logic
        submitButton = findViewById<Button>(R.id.submitApplicationButton).apply {
            isEnabled = true
            isClickable = true
            setOnClickListener {
                Toast.makeText(context, "Submitting application...", Toast.LENGTH_SHORT).show()

                val application = mapOf(
                    "userId" to (FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"),
                    "jobTitle" to jobTitle,
                    "jobCompany" to jobCompany,
                    "jobLocation" to jobLocation,
                    "jobSalary" to jobSalary,
                    "jobQualifications" to jobQualifications,
                    "jobUrl" to jobUrl,
                    "name" to nameEditText.text.toString(),
                    "email" to emailEditText.text.toString(),
                    "phone" to phoneEditText.text.toString(),
                    "address" to addressEditText.text.toString(),
                    "education" to educationSpinner.selectedItem.toString(),
                    "experience" to experienceEditText.text.toString(),
                    "coverLetter" to coverLetterEditText.text.toString(),
                    "linkedin" to linkedinEditText.text.toString(),
                    "portfolio" to portfolioEditText.text.toString(),
                    "resumeUri" to (selectedResumeUri?.toString() ?: "None"),
                    "timestamp" to System.currentTimeMillis().toString(),
                    "status" to "Submitted"
                )

                FirebaseFirestore.getInstance().collection("applications")
                    .add(application)
                    .addOnSuccessListener {
                        sendConfirmationEmail(emailEditText.text.toString(), jobTitle)
                        Toast.makeText(this@JobApplicationFormActivity, "Application Submitted", Toast.LENGTH_SHORT).show()

                        val successIntent = Intent(this@JobApplicationFormActivity, ApplicationSuccessActivity::class.java)
                        successIntent.putExtra("name", nameEditText.text.toString())
                        startActivity(successIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@JobApplicationFormActivity, "Failed to save application", Toast.LENGTH_SHORT).show()
                        Log.e("JobForm", "Failed to save application", e)
                    }
            }
        }

        // Select resume and trigger parsing
        selectResumeBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, RESUME_PICK_CODE)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESUME_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedResumeUri = data?.data
            resumeStatusText.text = "Resume selected ✔️"
            selectedResumeUri?.let { parseResumeAndAutofill(it) }
        }
    }

    private fun sendConfirmationEmail(email: String, jobTitle: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, "FObPath Job Application Received")
            putExtra(
                Intent.EXTRA_TEXT,
                "Hello,\n\nThank you for applying for the position: $jobTitle.\nWe have received your application and will be in touch shortly.\n\n- FObPath Team"
            )
        }
        try {
            startActivity(Intent.createChooser(intent, "Send email..."))
        } catch (e: Exception) {
            Toast.makeText(this, "No email client found", Toast.LENGTH_SHORT).show()
            Log.e("JobForm", "Email client not found", e)
        }
    }

    private fun parseResumeAndAutofill(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val text = reader.readText()
            inputStream?.close()

            val name = Regex("(?i)name[:\\-\\s]+([\\w\\s]+)").find(text)?.groupValues?.get(1)?.trim()
            val email = Regex("(?i)[\\w.%-]+@[\\w.-]+\\.[a-zA-Z]{2,4}").find(text)?.value
            val phone = Regex("""\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}""").find(text)?.value
            val experience = Regex("(?i)(experience|work history)[:\\-\\s]+([\\s\\S]{0,300})").find(text)?.groupValues?.get(2)
            val skills = Regex("(?i)(skills)[:\\-\\s]+([\\s\\S]{0,200})").find(text)?.groupValues?.get(2)

            findViewById<EditText>(R.id.nameEditText).setText(name)
            findViewById<EditText>(R.id.emailEditText).setText(email)
            findViewById<EditText>(R.id.phoneEditText).setText(phone)
            findViewById<EditText>(R.id.experienceEditText).setText(experience)
            findViewById<EditText>(R.id.coverLetterEditText).setText(skills)

            Toast.makeText(this, "Autofilled from resume!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to parse resume", Toast.LENGTH_SHORT).show()
            Log.e("ResumeParse", "Error parsing resume", e)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
