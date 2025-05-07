package com.example.fpgroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ApplicationTrackingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ApplicationAdapter
    private val applications = mutableListOf<Map<String, Any>>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_tracking)

        recyclerView = findViewById(R.id.trackingRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = object : ApplicationAdapter(applications) {
            override fun onWithdrawClicked(position: Int) {
                val app = applications[position]
                val jobTitle = app["jobTitle"] as? String ?: "this job"
                val docId = app["docId"] as? String ?: return

                AlertDialog.Builder(this@ApplicationTrackingActivity)
                    .setTitle("Withdraw Application")
                    .setMessage("Are you sure you want to withdraw your application for \"$jobTitle\"?")
                    .setPositiveButton("Yes") { _, _ ->
                        db.collection("applications").document(docId)
                            .update("status", "Withdrawn")
                            .addOnSuccessListener {
                                Toast.makeText(this@ApplicationTrackingActivity, "Application withdrawn.", Toast.LENGTH_SHORT).show()
                                fetchApplications()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@ApplicationTrackingActivity, "Failed to withdraw.", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        recyclerView.adapter = adapter

        findViewById<Button>(R.id.exportApplicationsBtn).setOnClickListener {
            val content = applications.joinToString("\n\n") { app ->
                "Job: ${app["jobTitle"]}\nCompany: ${app["jobCompany"]}\nStatus: ${app["status"]}"
            }

            val file = java.io.File(cacheDir, "applications.txt")
            file.writeText(content)

            val uri = androidx.core.content.FileProvider.getUriForFile(
                this, "$packageName.provider", file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Share Applications"))
        }

        fetchApplications()
    }

    private fun fetchApplications() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "You are not signed in.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Applications", "Fetching apps for user: $currentUserId")

        db.collection("applications")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                applications.clear()
                for (doc in result) {
                    val data = doc.data.toMutableMap()
                    data["docId"] = doc.id
                    applications.add(data)
                }

                if (applications.isEmpty()) {
                    Toast.makeText(this, "No applications found.", Toast.LENGTH_SHORT).show()
                    Log.d("Applications", "No documents found.")
                } else {
                    Log.d("Applications", "Found ${applications.size} applications.")
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.e("Applications", "Failed to load applications", it)
                Toast.makeText(this, "Failed to load applications.", Toast.LENGTH_SHORT).show()
            }
    }
}
