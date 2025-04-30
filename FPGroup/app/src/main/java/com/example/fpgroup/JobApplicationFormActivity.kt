package com.example.fpgroup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class JobApplicationFormActivity : AppCompatActivity() {

    private val RESUME_PICK_CODE = 2005
    private var selectedResumeUri: Uri? = null
    private lateinit var submitButton: Button
    private lateinit var jobTitle: String
    private var jobUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_application_form)

        jobTitle = intent.getStringExtra("JOB_TITLE") ?: "Unknown"
        jobUrl = intent.getStringExtra("JOB_URL")

        val selectResumeBtn: Button = findViewById(R.id.selectResumeButton)
        submitButton = findViewById(R.id.submitResumeButton)

        selectResumeBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, RESUME_PICK_CODE)
        }

        submitButton.setOnClickListener {
            selectedResumeUri?.let { uri ->
                uploadResumeToStorage(uri)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESUME_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedResumeUri = data?.data
            Toast.makeText(this, "Resume selected!", Toast.LENGTH_SHORT).show()
            submitButton.isEnabled = true
        }
    }

    private fun uploadResumeToStorage(uri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "resumes/$userId/${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.pdf"
        val fileRef = storageRef.child(fileName)

        fileRef.putFile(uri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveApplicationToFirestore(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload resume", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveApplicationToFirestore(resumeUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser?.email ?: "unknown"

        val application = mapOf(
            "userId" to userId,
            "email" to email,
            "jobTitle" to jobTitle,
            "jobUrl" to jobUrl,
            "resumeUrl" to resumeUrl,
            "status" to "Submitted",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("applications").add(application)
            .addOnSuccessListener {
                FirestoreHelper.sendEmailConfirmation(email, jobTitle)
                val intent = Intent(this, ApplicationSuccessActivity::class.java)
                intent.putExtra("name", email)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error saving application", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
