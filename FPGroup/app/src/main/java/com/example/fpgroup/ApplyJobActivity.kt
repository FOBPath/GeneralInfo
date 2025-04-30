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

        // Auto-fill from SharedPreferences
        nameInput.setText(prefs.getString("name", ""))
        emailInput.setText(prefs.getString("email", ""))

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
                "jobTitle" to (intent.getStringExtra("JOB_TITLE") ?: "Unknown"),
                "jobCompany" to (intent.getStringExtra("JOB_COMPANY") ?: "Unknown"),
                "jobLocation" to (intent.getStringExtra("JOB_LOCATION") ?: ""),
                "jobSalary" to (intent.getStringExtra("JOB_SALARY") ?: "Not listed"),
                "jobQualifications" to (intent.getStringExtra("JOB_QUALIFICATIONS") ?: "Not specified")
            )

            FirestoreHelper.submitApplication(application) { success ->
                if (success) {
                    FirestoreHelper.sendEmailConfirmation(application["email"]!!, application["jobTitle"]!!)
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
