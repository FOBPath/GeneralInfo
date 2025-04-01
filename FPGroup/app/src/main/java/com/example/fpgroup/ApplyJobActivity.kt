package com.example.fpgroup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ApplyJobActivity : AppCompatActivity() {

    private lateinit var nameField: EditText
    private lateinit var emailField: EditText
    private lateinit var coverLetterField: EditText
    private lateinit var uploadResumeButton: Button
    private lateinit var submitApplicationButton: Button
    private lateinit var resumeStatus: TextView

    private var resumeUri: Uri? = null
    private var jobTitle: String? = null
    private var jobCompany: String? = null

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_job)

        jobTitle = intent.getStringExtra("JOB_TITLE")
        jobCompany = intent.getStringExtra("JOB_COMPANY")
        val jobDescription = intent.getStringExtra("JOB_DESCRIPTION")

        findViewById<TextView>(R.id.applyJobTitle).text = "$jobTitle at $jobCompany"
        findViewById<TextView>(R.id.applyJobDescription).text = jobDescription

        nameField = findViewById(R.id.nameField)
        emailField = findViewById(R.id.emailField)
        coverLetterField = findViewById(R.id.coverLetterField)
        uploadResumeButton = findViewById(R.id.uploadResumeButton)
        submitApplicationButton = findViewById(R.id.submitApplicationButton)
        resumeStatus = findViewById(R.id.resumeStatus)

        uploadResumeButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, 101)
        }

        submitApplicationButton.setOnClickListener {
            submitApplication()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            resumeUri = data?.data
            resumeStatus.text = "Resume Selected"
        }
    }

    private fun submitApplication() {
        val name = nameField.text.toString().trim()
        val email = emailField.text.toString().trim()
        val coverLetter = coverLetterField.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || coverLetter.isEmpty() || resumeUri == null || jobTitle == null || jobCompany == null) {
            Toast.makeText(this, "Please fill out all fields and upload your resume.", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "resumes/${UUID.randomUUID()}.pdf"
        val ref = storage.reference.child(fileName)

        ref.putFile(resumeUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                val applicationData = hashMapOf(
                    "jobTitle" to jobTitle,
                    "jobCompany" to jobCompany,
                    "applicantName" to name,
                    "email" to email,
                    "coverLetter" to coverLetter,
                    "resumeUrl" to uri.toString()
                )

                firestore.collection("applications")
                    .add(applicationData)
                    .addOnSuccessListener {
                        val intent = Intent(this, ApplicationSuccessActivity::class.java)
                        intent.putExtra("JOB_TITLE", jobTitle)
                        intent.putExtra("JOB_COMPANY", jobCompany)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to submit application.", Toast.LENGTH_LONG).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload resume.", Toast.LENGTH_SHORT).show()
        }
    }
}



















