package com.example.fpgroup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ApplyJobActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_job)

        val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        val nameInput: EditText = findViewById(R.id.inputName)
        val emailInput: EditText = findViewById(R.id.inputEmail)
        val locationInput: EditText = findViewById(R.id.inputLocation)
        val experienceInput: EditText = findViewById(R.id.inputExperience)
        val educationInput: EditText = findViewById(R.id.inputEducation)
        val volunteerInput: EditText = findViewById(R.id.inputVolunteer)
        val raceInput: EditText = findViewById(R.id.inputRace)
        val genderInput: EditText = findViewById(R.id.inputGender)
        val submitBtn: Button = findViewById(R.id.submitApplicationButton)

        // ✅ Autofill all fields
        nameInput.setText(prefs.getString("name", ""))
        emailInput.setText(prefs.getString("email", ""))
        locationInput.setText(prefs.getString("location", ""))
        experienceInput.setText(prefs.getString("experience", ""))
        educationInput.setText(prefs.getString("education", ""))
        volunteerInput.setText(prefs.getString("volunteer", ""))
        raceInput.setText(prefs.getString("race", ""))
        genderInput.setText(prefs.getString("gender", ""))

        val jobTitle = intent.getStringExtra("JOB_TITLE") ?: "Unknown"
        val jobCompany = intent.getStringExtra("JOB_COMPANY") ?: "Unknown"
        val jobLocation = intent.getStringExtra("JOB_LOCATION") ?: ""
        val jobSalary = intent.getStringExtra("JOB_SALARY") ?: "Not listed"
        val jobQualifications = intent.getStringExtra("JOB_QUALIFICATIONS") ?: "Not specified"
        val jobUrl = intent.getStringExtra("JOB_URL") ?: ""

        submitBtn.setOnClickListener {
            val application = mapOf(
                "name" to nameInput.text.toString(),
                "email" to emailInput.text.toString(),
                "location" to locationInput.text.toString(),
                "experience" to experienceInput.text.toString(),
                "education" to educationInput.text.toString(),
                "volunteer" to volunteerInput.text.toString(),
                "race" to raceInput.text.toString(),
                "gender" to genderInput.text.toString(),
                "jobTitle" to jobTitle,
                "jobCompany" to jobCompany,
                "jobLocation" to jobLocation,
                "jobSalary" to jobSalary,
                "jobQualifications" to jobQualifications,
                "jobUrl" to jobUrl
            )

            FirestoreHelper.submitApplication(application) { success ->
                if (success) {
                    FirestoreHelper.sendEmailConfirmation(
                        this@ApplyJobActivity,
                        application["email"]!!,
                        jobTitle,
                        jobUrl
                    )

                    val intent = Intent(this, ApplicationSuccessActivity::class.java)
                    intent.putExtra("name", application["name"])
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to submit to Firestore", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
