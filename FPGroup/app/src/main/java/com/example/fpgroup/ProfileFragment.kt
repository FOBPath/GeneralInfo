package com.example.fpgroup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var userEmailText: TextView
    private lateinit var userNameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button
    private lateinit var uploadResumeButton: Button
    private lateinit var uploadProfileImageButton: Button
    private lateinit var resumeTextView: TextView

    private var imageUri: Uri? = null
    private var resumeUri: Uri? = null

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val authManager = AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profileImage)
        userEmailText = view.findViewById(R.id.userEmailText)
        userNameEditText = view.findViewById(R.id.userNameEditText)
        bioEditText = view.findViewById(R.id.bioEditText)
        saveButton = view.findViewById(R.id.saveButton)
        logoutButton = view.findViewById(R.id.logoutButton)
        uploadResumeButton = view.findViewById(R.id.uploadResumeButton)
        uploadProfileImageButton = view.findViewById(R.id.uploadProfileImageButton)
        resumeTextView = view.findViewById(R.id.resumeTextView)

        val user = authManager.getCurrentUser()
        userEmailText.text = user?.email ?: "Not Available"

        loadProfileData()

        uploadProfileImageButton.setOnClickListener { selectProfileImage() }
        uploadResumeButton.setOnClickListener { selectResume() }
        saveButton.setOnClickListener { saveProfileData() }
        logoutButton.setOnClickListener { logoutUser() }

        return view
    }

    private fun saveProfileData() {
        val user = authManager.getCurrentUser() ?: return
        val userId = user.uid

        val sharedPreferences = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("name", userNameEditText.text.toString().trim())
            putString("bio", bioEditText.text.toString().trim())
            apply()
        }

        // Upload new profile image if selected
        imageUri?.let { uri ->
            val profileRef = storageReference.child("profile_pictures/$userId.jpg")
            profileRef.putFile(uri)
                .addOnSuccessListener { showToast("Profile image updated!") }
                .addOnFailureListener { showToast("Failed to upload profile image.") }
        }

        // Upload new resume if selected
        resumeUri?.let { uri ->
            val resumeRef = storageReference.child("resumes/$userId.pdf")
            resumeRef.putFile(uri)
                .addOnSuccessListener { showToast("Resume uploaded successfully!") }
                .addOnFailureListener { showToast("Failed to upload resume.") }
        }

        showToast("Profile updated successfully!")
    }

    @SuppressLint("SetTextI18n")
    private fun loadProfileData() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        userNameEditText.setText(sharedPreferences.getString("name", ""))
        bioEditText.setText(sharedPreferences.getString("bio", ""))

        val user = authManager.getCurrentUser() ?: return
        val userId = user.uid

        val profileRef = storageReference.child("profile_pictures/$userId.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this).load(uri).into(profileImage)
        }

        val resumeRef = storageReference.child("resumes/$userId.pdf")
        resumeRef.downloadUrl.addOnSuccessListener { uri ->
            resumeTextView.text = "View Resume"
            resumeTextView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setDataAndType(uri, "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
            }
        }.addOnFailureListener {
            resumeTextView.text = "No Resume Uploaded"
        }
    }

    private val profileImagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            profileImage.setImageURI(it) // Show selected image before uploading
        }
    }

    private val resumePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            resumeUri = it
            val fileName = getFileNameFromUri(it) ?: "Selected Resume"
            resumeTextView.text = fileName
        }
    }

    private fun selectProfileImage() {
        profileImagePicker.launch("image/*")
    }

    private fun selectResume() {
        resumePicker.launch("application/pdf")
    }

    private fun logoutUser() {
        authManager.logoutUser()
        requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE).edit().clear().apply()

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        requireActivity().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex >= 0) {
                    return cursor.getString(columnIndex)
                }
            }
        }
        return null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
