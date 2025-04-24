package com.example.fpgroup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var themeSwitch: SwitchCompat
    private lateinit var resumeStatusText: TextView
    private val RESUME_PICK_CODE = 2001

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Theme switch
        themeSwitch = view.findViewById(R.id.switchTheme)
        val sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)
        themeSwitch.isChecked = isDarkMode
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            sharedPreferences.edit().putBoolean("DARK_MODE", isChecked).apply()
        }

        // Resume upload
        resumeStatusText = view.findViewById(R.id.resumeStatusText)
        updateResumeStatus()
        view.findViewById<Button>(R.id.uploadResumeButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, RESUME_PICK_CODE)
        }

        // Change Password (placeholder)
        view.findViewById<Button>(R.id.changePasswordButton).setOnClickListener {
            Toast.makeText(requireContext(), "Password change coming soon!", Toast.LENGTH_SHORT).show()
        }

        // View Submitted Applications
        view.findViewById<Button>(R.id.viewSubmittedApplicationsButton).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SubmittedApplicationsFragment())
                .addToBackStack(null)
                .commit()
        }

        // Logout
        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            AuthManager.logout(requireContext())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESUME_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                saveResumeUri(uri)
                updateResumeStatus()
                Toast.makeText(requireContext(), "Resume uploaded!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveResumeUri(uri: Uri) {
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("resume_uri", uri.toString()).apply()
    }

    private fun updateResumeStatus() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val uriString = sharedPreferences.getString("resume_uri", null)
        resumeStatusText.text = if (uriString != null) "Resume: Uploaded ✔" else "Resume: Not uploaded"
    }
}
