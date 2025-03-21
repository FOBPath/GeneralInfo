package com.example.fpgroup

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Registers a new user with email and password.
     * Calls onComplete with success status and optional error message.
     */
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

    /**
     * Logs in an existing user with email and password.
     * Ensures the email is verified before allowing login.
     */
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        onComplete(true, null)
                    } else {
                        auth.signOut() // Prevent unverified access
                        onComplete(false, "Please verify your email before logging in.")
                    }
                } else {
                    onComplete(false, task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    /**
     * Logs out the current user and clears local profile data.
     */
    fun logoutUser() {
        auth.signOut()
        // Clear shared preferences or local storage if needed
    }

    /**
     * Checks if a user is currently logged in.
     * @return FirebaseUser? - the current logged-in user, or null if no user is logged in.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Sends a password reset email to the given email.
     * Calls onComplete with success status and optional error message.
     */
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

    /**
     * Updates the user's profile with a new display name.
     * Calls onComplete with success status and optional error message.
     */
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


