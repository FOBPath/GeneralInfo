package com.example.fpgroup

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.InputStreamReader

class ProfileFragment : Fragment() {

    private lateinit var resumeStatusText: TextView
    private lateinit var nameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var profileImage: ImageView
    private lateinit var skillsEdit: EditText
    private lateinit var experienceEdit: EditText
    private lateinit var contactEdit: EditText
    private val RESUME_PICK_CODE = 1001
    private val IMAGE_PICK_CODE = 2002

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resumeStatusText = view.findViewById(R.id.resumeStatus)
        nameEdit = view.findViewById(R.id.nameEdit)
        emailEdit = view.findViewById(R.id.emailEdit)
        profileImage = view.findViewById(R.id.profileImage)
        skillsEdit = view.findViewById(R.id.skillsEdit)
        experienceEdit = view.findViewById(R.id.experienceEdit)
        contactEdit = view.findViewById(R.id.contactEdit)

        val uploadBtn = view.findViewById<Button>(R.id.uploadResumeButton)
        val saveBtn = view.findViewById<Button>(R.id.saveProfileButton)

        val prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Load saved values
        nameEdit.setText(prefs.getString("name", ""))
        emailEdit.setText(prefs.getString("email", ""))
        skillsEdit.setText(prefs.getString("skills", ""))
        experienceEdit.setText(prefs.getString("experience", ""))
        contactEdit.setText(prefs.getString("contact", ""))

        val resumeUri = prefs.getString("resume_uri", null)
        resumeStatusText.text = if (resumeUri != null) "Resume: Uploaded ✔" else "Resume: Not uploaded"

        val imageUri = prefs.getString("profile_image_uri", null)
        if (imageUri != null) {
            profileImage.setImageURI(Uri.parse(imageUri))
        }

        // Upload resume
        uploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, RESUME_PICK_CODE)
        }

        // Upload profile image
        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // Save profile
        saveBtn.setOnClickListener {
            val name = nameEdit.text.toString()
            val email = emailEdit.text.toString()
            val skills = skillsEdit.text.toString()
            val experience = experienceEdit.text.toString()
            val contact = contactEdit.text.toString()

            prefs.edit()
                .putString("name", name)
                .putString("email", email)
                .putString("skills", skills)
                .putString("experience", experience)
                .putString("contact", contact)
                .apply()

            FirestoreHelper.saveProfile(name, email, skills, experience, contact) { success ->
                val msg = if (success) "Profile saved to cloud!" else "Saved locally only."
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val prefs = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        if (requestCode == RESUME_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                prefs.edit().putString("resume_uri", uri.toString()).apply()
                resumeStatusText.text = "Resume: Uploaded ✔"
                parseResumeAndStore(uri)
                Toast.makeText(requireContext(), "Resume uploaded and info extracted!", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                profileImage.setImageURI(uri)
                prefs.edit().putString("profile_image_uri", uri.toString()).apply()
                Toast.makeText(requireContext(), "Profile image updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseResumeAndStore(uri: Uri) {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val text = reader.readText()
        inputStream?.close()

        val name = Regex("(?i)name:\\s*(.*)").find(text)?.groupValues?.get(1)?.trim() ?: ""
        val email = Regex("(?i)email:\\s*(.*)").find(text)?.groupValues?.get(1)?.trim() ?: ""
        val skills = Regex("(?i)skills?:\\s*(.*)").find(text)?.groupValues?.get(1)?.trim() ?: ""
        val experience = Regex("(?i)(experience|work history):\\s*(.*)").find(text)?.groupValues?.get(2)?.trim() ?: ""

        prefs.edit()
            .putString("name", name)
            .putString("email", email)
            .putString("skills", skills)
            .putString("experience", experience)
            .apply()
    }
}
