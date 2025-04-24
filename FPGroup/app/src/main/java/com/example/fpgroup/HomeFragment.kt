package com.example.fpgroup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var recentJobsAdapter: JobAdapter
    private var recentJobs: MutableList<Job> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val resumeStatusText: TextView = view.findViewById(R.id.resumeStatusText)
        val recentJobsRecyclerView: RecyclerView = view.findViewById(R.id.recentJobsRecyclerView)

        // Set dummy email — replace with user data if stored
        emailTextView.text = "Email: user@example.com"
        resumeStatusText.text = "Resume: Not uploaded" // TODO: make dynamic

        recentJobsAdapter = JobAdapter(recentJobs)
        recentJobsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recentJobsRecyclerView.adapter = recentJobsAdapter

        loadRecentJobs()

        // Navigation to STEM Jobs Tab (assumes using BottomNavigationView)
        view.findViewById<Button>(R.id.goToStemJobsButton).setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.nav_jobs
        }

        view.findViewById<Button>(R.id.goToSavedJobsButton).setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.nav_saved_jobs
        }
    }

    private fun loadRecentJobs() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppliedJobs", Context.MODE_PRIVATE)
        val jobsSet = sharedPreferences.getStringSet("jobs", emptySet()) ?: emptySet()

        val recent = jobsSet.toList().takeLast(2).mapNotNull { jobData ->
        val parts = jobData.split("|")
            if (parts.size >= 5) {
                Job(
                    title = parts[0],
                    company = Company(display_name = parts[1]),
                    location = Location(display_name = parts[2]),
                    redirect_url = parts[3],
                    description = parts[4],
                    salary = parts.getOrNull(5),
                    qualifications = parts.getOrNull(6)
                )
            } else null
        }

        recentJobs.clear()
        recentJobs.addAll(recent)
        recentJobsAdapter.notifyDataSetChanged()
    }
}
