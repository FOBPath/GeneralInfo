package com.example.fpgroup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        val summaryText = findViewById<TextView>(R.id.summaryText)
        val name = intent.getStringExtra("name")
        val location = intent.getStringExtra("location")
        val work = intent.getStringExtra("work")
        val race = intent.getStringExtra("race")
        val gender = intent.getStringExtra("gender")
        val volunteer = intent.getStringExtra("volunteer")
        val education = intent.getStringExtra("education")

        val summary = """
            📝 Name: $name
            📍 Location: $location
            💼 Experience: $work
            🎓 Education: $education
            🧑 Race: $race
            ⚧ Gender: $gender
            🤝 Volunteer Work: $volunteer
        """.trimIndent()

        summaryText.text = summary

        findViewById<Button>(R.id.goHomeButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
