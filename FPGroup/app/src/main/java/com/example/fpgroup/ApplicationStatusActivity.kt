package com.example.fpgroup

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ApplicationStatusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ApplicationAdapter
    private val applicationList: MutableList<Map<String, Any>> = mutableListOf()

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_status)

        recyclerView = findViewById(R.id.applicationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ApplicationAdapter(applicationList)
        recyclerView.adapter = adapter

        if (currentUser == null) {
            Toast.makeText(this, "Please log in to view application status.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchApplications()
    }

    private fun fetchApplications() {
        db.collection("applications")
            .whereEqualTo("userId", currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                applicationList.clear()
                for (doc in documents) {
                    val data = doc.data.toMutableMap()
                    data["docId"] = doc.id
                    applicationList.add(data)
                }

                if (applicationList.isEmpty()) {
                    Toast.makeText(this, "No submitted jobs found.", Toast.LENGTH_SHORT).show()
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.e("AppStatus", "Failed to fetch applications", it)
                Toast.makeText(this, "Failed to fetch status", Toast.LENGTH_SHORT).show()
            }
    }
}
