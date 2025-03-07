package com.example.fpgroup

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.localizedMessage ?: "Registration failed")
                }
            }
    }

    /**
     * Logs in an existing user with email and password.
     * Calls onComplete with success status and optional error message.
     */
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    /**
     * Logs out the current user.
     */
    fun logoutUser() {
        auth.signOut()
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
}
