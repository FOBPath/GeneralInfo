package com.example.fpgroup

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.firebase.auth.FirebaseAuth

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var applyButton: Button
    private lateinit var saveButton: Button
    private lateinit var expandButton: Button
    private lateinit var descriptionText: TextView
    private lateinit var qualificationsText: TextView
    private lateinit var skillsText: TextView
    private lateinit var benefitsText: TextView

    private var isExpanded = false
    private var fullDescription: String? = null
    private var jobUrl: String? = null
    private lateinit var jobTitle: String
    private lateinit var jobCompany: String
    private lateinit var jobLocation: String
    private var jobSalary: String? = null
    private var jobQualifications: String? = null
    private var jobSkills: String? = null
    private var jobBenefits: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        val titleText: TextView = findViewById(R.id.jobTitleText)
        val companyText: TextView = findViewById(R.id.jobCompanyText)
        val locationText: TextView = findViewById(R.id.jobLocationText)
        val salaryText: TextView = findViewById(R.id.salaryText)
        descriptionText = findViewById(R.id.descriptionText)
        qualificationsText = findViewById(R.id.qualificationsText)
        skillsText = findViewById(R.id.skillsText)
        benefitsText = findViewById(R.id.benefitsText)

        applyButton = findViewById(R.id.applyButton)
        saveButton = findViewById(R.id.saveButton)
        expandButton = findViewById(R.id.expandButton)

        jobTitle = intent.getStringExtra("JOB_TITLE") ?: "No Title"
        jobCompany = intent.getStringExtra("JOB_COMPANY") ?: "No Company"
        jobLocation = intent.getStringExtra("JOB_LOCATION") ?: "No Location"
        jobSalary = intent.getStringExtra("JOB_SALARY") ?: "Salary not listed"
        fullDescription = intent.getStringExtra("JOB_DESCRIPTION")
        jobUrl = intent.getStringExtra("JOB_URL")
        jobQualifications = intent.getStringExtra("JOB_QUALIFICATIONS")
        jobSkills = intent.getStringExtra("JOB_SKILLS")
        jobBenefits = intent.getStringExtra("JOB_BENEFITS")

        titleText.text = jobTitle
        companyText.text = jobCompany
        locationText.text = jobLocation
        salaryText.text = "Salary: $jobSalary"

        if (!fullDescription.isNullOrBlank()) {
            if (fullDescription!!.length > 200) {
                descriptionText.text = fullDescription!!.take(200) + "..."
                expandButton.visibility = View.VISIBLE
            } else {
                descriptionText.text = fullDescription
                expandButton.visibility = View.GONE
            }
        } else {
            descriptionText.visibility = View.GONE
            expandButton.visibility = View.GONE
        }

        if (!jobQualifications.isNullOrBlank() && jobQualifications != "Not specified") {
            qualificationsText.text = "Qualifications:\n$jobQualifications"
        } else {
            qualificationsText.visibility = View.GONE
        }

        skillsText.text = "Skills:\n${jobSkills ?: "Not listed"}"
        benefitsText.text = "Benefits:\n${jobBenefits ?: "Not listed"}"

        expandButton.setOnClickListener { toggleDescription() }

        saveButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val job = Job(
                title = jobTitle,
                company = Company(jobCompany),
                location = Location(jobLocation),
                description = fullDescription ?: "",
                salary = jobSalary,
                qualifications = jobQualifications,
                redirect_url = jobUrl ?: ""
            )
            FirestoreHelper.saveJobToFirestore(job, userId) { success ->
                Toast.makeText(this, if (success) "Job saved!" else "Failed to save", Toast.LENGTH_SHORT).show()
            }
        }

        applyButton.setOnClickListener {
            val intent = Intent(this, JobApplicationFormActivity::class.java).apply {
                putExtra("JOB_TITLE", jobTitle)
                putExtra("JOB_COMPANY", jobCompany)
                putExtra("JOB_LOCATION", jobLocation)
                putExtra("JOB_SALARY", jobSalary)
                putExtra("JOB_QUALIFICATIONS", jobQualifications)
                putExtra("JOB_URL", jobUrl)
            }
            startActivity(intent)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun toggleDescription() {
        isExpanded = !isExpanded
        val initialHeight = descriptionText.height
        val targetText = if (isExpanded) fullDescription else fullDescription!!.take(200) + "..."
        val targetLines = if (isExpanded) Int.MAX_VALUE else 5

        descriptionText.text = targetText
        descriptionText.maxLines = targetLines
        descriptionText.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = descriptionText.measuredHeight

        descriptionText.text = if (!isExpanded) fullDescription else fullDescription!!.take(200) + "..."
        descriptionText.maxLines = if (!isExpanded) Int.MAX_VALUE else 5

        val animator = ValueAnimator.ofInt(initialHeight, targetHeight)
        animator.addUpdateListener {
            descriptionText.layoutParams.height = it.animatedValue as Int
            descriptionText.requestLayout()
        }
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()

        animator.addListener(onEnd = {
            descriptionText.text = targetText
            descriptionText.maxLines = targetLines
            descriptionText.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            expandButton.text = if (isExpanded) "Show Less ▲" else "Read More ▼"

            SpringAnimation(descriptionText, SpringAnimation.TRANSLATION_Y, 0f).apply {
                spring.stiffness = SpringForce.STIFFNESS_LOW
                spring.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
                setStartVelocity(1000f)
                start()
            }
        })

        expandButton.animate().rotationBy(if (isExpanded) 180f else -180f).setDuration(300).start()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
