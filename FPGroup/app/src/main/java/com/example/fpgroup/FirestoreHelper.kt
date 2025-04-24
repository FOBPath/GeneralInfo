package com.example.fpgroup

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
}
