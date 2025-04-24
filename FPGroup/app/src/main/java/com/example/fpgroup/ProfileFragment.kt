package com.example.fpgroup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var resumeStatusText: TextView
    private lateinit var nameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var profileImage: ImageView
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

        val uploadBtn = view.findViewById<Button>(R.id.uploadResumeButton)
        val saveBtn = view.findViewById<Button>(R.id.saveProfileButton)

        val prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Load name/email
        nameEdit.setText(prefs.getString("name", ""))
        emailEdit.setText(prefs.getString("email", ""))

        // Load resume
        val resumeUri = prefs.getString("resume_uri", null)
        resumeStatusText.text = if (resumeUri != null) "Resume: Uploaded ✔" else "Resume: Not uploaded"

        // Load profile image
        val imageUri = prefs.getString("profile_image_uri", null)
        if (imageUri != null) {
            profileImage.setImageURI(Uri.parse(imageUri))
        }

        // Resume picker
        uploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, RESUME_PICK_CODE)
        }

        // Image picker
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

            prefs.edit()
                .putString("name", name)
                .putString("email", email)
                .apply()

            FirestoreHelper.saveProfile(name, email) { success ->
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
                Toast.makeText(requireContext(), "Resume uploaded successfully!", Toast.LENGTH_SHORT).show()
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
}
