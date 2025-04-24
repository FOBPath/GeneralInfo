package com.example.fpgroup

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            onComplete(true, null)
                        } else {
                            onComplete(false, "Failed to send verification email")
                        }
                    }
                } else {
                    val errorCode = task.exception?.message
                    if (errorCode?.contains("email address is already in use") == true) {
                        onComplete(false, "Email already registered. Please log in.")
                    } else {
                        onComplete(false, task.exception?.localizedMessage ?: "Registration failed")
                    }
                }
            }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        onComplete(true, null)
                    } else {
                        auth.signOut()
                        onComplete(false, "Please verify your email before logging in.")
                    }
                } else {
                    onComplete(false, task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun logout(context: Context) {
        logoutUser()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun sendPasswordReset(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.localizedMessage ?: "Failed to send password reset email")
                }
            }
    }

    fun updateUserProfile(displayName: String, onComplete: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onComplete(true, null)
                    } else {
                        onComplete(false, task.exception?.localizedMessage ?: "Failed to update profile")
                    }
                }
        } else {
            onComplete(false, "User not logged in")
        }
    }
}
