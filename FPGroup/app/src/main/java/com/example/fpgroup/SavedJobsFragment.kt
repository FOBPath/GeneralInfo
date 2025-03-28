package com.example.fpgroup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedJobsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private var savedJobs: MutableList<Job> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_saved_jobs, container, false)

        recyclerView = view.findViewById(R.id.savedJobsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter before loading saved jobs
        jobAdapter = JobAdapter(savedJobs)
        recyclerView.adapter = jobAdapter

        loadSavedJobs() // Load saved jobs after adapter is set

        return view
    }

    private fun loadSavedJobs() {
        val sharedPreferences =
            activity?.getSharedPreferences("AppliedJobs", Context.MODE_PRIVATE) ?: return
        val jobsSet = sharedPreferences.getStringSet("jobs", emptySet())

        savedJobs.clear()
        jobsSet?.forEach { jobData ->
            val parts = jobData.split("|")
            if (parts.size >= 4) {
                savedJobs.add(Job(parts[0], parts[1], parts[2], "Full-time", parts[3]))
            }
        }

        if (savedJobs.isEmpty()) {
            Toast.makeText(requireContext(), "No saved jobs found", Toast.LENGTH_SHORT).show()
        }

        jobAdapter.notifyDataSetChanged()
    }
}

