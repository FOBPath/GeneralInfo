package com.example.fpgroup

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class SubmittedApplicationsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ApplicationAdapter
    private val applications: MutableList<Map<String, Any>> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_submitted_applications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.applicationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ApplicationAdapter(applications)
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.exportButton).setOnClickListener {
            exportApplications()
        }

        fetchUserApplications()
    }

    private fun fetchUserApplications() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("applications")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                applications.clear()
                for (doc in result) {
                    applications.add(doc.data)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load applications", Toast.LENGTH_SHORT).show()
            }
    }

    private fun exportApplications() {
        val content = applications.joinToString("\n\n") { app ->
            "Job: ${app["jobTitle"]}\nCompany: ${app["jobCompany"]}\nStatus: Submitted"
        }

        val file = File(requireContext().cacheDir, "submitted_jobs.txt")
        file.writeText(content)

        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Export Applications"))
    }
}
