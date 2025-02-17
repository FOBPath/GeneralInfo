package com.example.fpgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val darkModeSwitch: Switch = view.findViewById(R.id.darkModeSwitch)
        val changePasswordButton: Button = view.findViewById(R.id.changePasswordButton)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Dark Mode Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        changePasswordButton.setOnClickListener {
            Toast.makeText(requireContext(), "Change Password feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
