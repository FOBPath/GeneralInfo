package com.example.fpgroup

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {

    // Submit job application to Firestore
    fun submitApplication(application: Map<String, String>, onComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        val docRef = db.collection("applications").document()

        val data = application + mapOf(
            "userId" to userId,
            "timestamp" to System.currentTimeMillis().toString()
        )

        docRef.set(data)
            .addOnSuccessListener {
                Log.d("FirestoreHelper", "Application submitted")
                onComplete(true)
            }
            .addOnFailureListener {
                Log.e("FirestoreHelper", "Application submission failed", it)
                onComplete(false)
            }
    }

    // Save profile (name and email) to Firestore
    fun saveProfile(name: String, email: String, onComplete: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onComplete(false)
        val db = FirebaseFirestore.getInstance()

        val userMap = mapOf(
            "name" to name,
            "email" to email
        )

        db.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Log.d("FirestoreHelper", "Profile saved")
                onComplete(true)
            }
            .addOnFailureListener {
                Log.e("FirestoreHelper", "Failed to save profile", it)
                onComplete(false)
            }
    }

    // Fetch profile from Firestore
    fun fetchProfile(onResult: (String?, String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(null, null)
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name")
                val email = document.getString("email")
                Log.d("FirestoreHelper", "Fetched profile: $name <$email>")
                onResult(name, email)
            }
            .addOnFailureListener {
                Log.e("FirestoreHelper", "Failed to fetch profile", it)
                onResult(null, null)
            }
    }

    // Save job to Firestore
    fun saveJobToFirestore(job: Job, userId: String, onComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val jobData = hashMapOf(
            "title" to job.title,
            "company" to job.company.display_name,
            "location" to job.location.display_name,
            "description" to job.description,
            "salary" to job.salary,
            "qualifications" to job.qualifications,
            "url" to job.redirect_url,
            "userId" to userId,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("saved_jobs").add(jobData)
            .addOnSuccessListener {
                Log.d("FirestoreHelper", "Saved job: ${job.title}")
                onComplete(true)
            }
            .addOnFailureListener {
                Log.e("FirestoreHelper", "Failed to save job", it)
                onComplete(false)
            }
    }

    // Fetch saved jobs from Firestore
    fun fetchSavedJobs(userId: String, onResult: (List<Job>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("saved_jobs")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val jobs = result.mapNotNull { doc ->
                    Job(
                        title = doc.getString("title") ?: return@mapNotNull null,
                        company = Company(doc.getString("company") ?: ""),
                        location = Location(doc.getString("location") ?: ""),
                        description = doc.getString("description") ?: "",
                        salary = doc.getString("salary"),
                        qualifications = doc.getString("qualifications"),
                        redirect_url = doc.getString("url") ?: ""
                    )
                }
                Log.d("FirestoreHelper", "Fetched ${jobs.size} saved jobs")
                onResult(jobs)
            }
            .addOnFailureListener {
                Log.e("FirestoreHelper", "Failed to fetch saved jobs", it)
                onResult(emptyList())
            }
    }

    // Send confirmation email after application
    fun sendEmailConfirmation(email: String, jobTitle: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "FObPath Application Confirmation")
                putExtra(Intent.EXTRA_TEXT, "Thank you for applying for the position: $jobTitle.\n\nWe wish you success and will keep you updated.")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            MyApplication.instance?.startActivity(intent)
        } catch (e: Exception) {
            Log.e("FirestoreHelper", "Failed to send confirmation email", e)
        }
    }
}
