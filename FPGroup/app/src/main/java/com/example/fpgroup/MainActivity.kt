package com.example.fpgroup

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var emailTextView: TextView
    private lateinit var prefs: SharedPreferences
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailTextView = findViewById(R.id.emailTextView)
        prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Set initial welcome message
        updateWelcomeText()

        // Listen for profile changes
        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "name" || key == "email") {
                updateWelcomeText()
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_jobs -> JobsFragment()
                R.id.nav_saved_jobs -> SavedJobsFragment()
                R.id.nav_profile -> ProfileFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()
            true
        }

        // Launch default tab or restore
        val selectedTab = intent.getIntExtra("SELECTED_TAB", R.id.nav_home)
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = selectedTab
        }
    }

    private fun updateWelcomeText() {
        val savedName = prefs.getString("name", "User")
        val savedEmail = prefs.getString("email", "user@example.com")
        emailTextView.text = "Welcome, $savedName\n($savedEmail)"
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
