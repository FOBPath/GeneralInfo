package com.example.fpgroup

import android.content.Context
import android.os.Bundle
import android.util.Log
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
        jobAdapter = JobAdapter(savedJobs)
        recyclerView.adapter = jobAdapter

        loadSavedJobs()
        return view
    }

    private fun loadSavedJobs() {
        val sharedPreferences = activity?.getSharedPreferences("AppliedJobs", Context.MODE_PRIVATE) ?: return
        val jobsSet = sharedPreferences.getStringSet("jobs", emptySet())

        savedJobs.clear()
        jobsSet?.forEach { jobData ->
            val parts = jobData.split("|")
            Log.d("SavedJobs", "Raw job string: $jobData")
            if (parts.size >= 5) {
                val job = Job(
                    title = parts[0],
                    company = Company(display_name = parts[1]),
                    location = Location(display_name = parts[2]),
                    redirect_url = parts[3],
                    description = parts[4],
                    salary = parts.getOrNull(5),
                    qualifications = parts.getOrNull(6)
                )
                savedJobs.add(job)
            }
        }

        if (savedJobs.isEmpty()) {
            Toast.makeText(requireContext(), "No saved jobs found", Toast.LENGTH_SHORT).show()
        }

        jobAdapter.notifyDataSetChanged()
    }
}
