package com.example.fpgroup

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var recentJobsAdapter: JobAdapter
    private var recentJobs: MutableList<Job> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val resumeStatusText: TextView = view.findViewById(R.id.resumeStatusText)
        val recyclerView: RecyclerView = view.findViewById(R.id.recentJobsRecyclerView)

        emailTextView.text = "Email: ${AuthManager.getCurrentUser()?.email ?: "user@example.com"}"
        resumeStatusText.text = "Resume: Not uploaded"

        recentJobsAdapter = JobAdapter(recentJobs)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recentJobsAdapter

        view.findViewById<Button>(R.id.goToStemJobsButton).setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.nav_jobs
        }

        view.findViewById<Button>(R.id.goToSavedJobsButton).setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.nav_saved_jobs
        }

        loadRecentJobs()
    }

    private fun loadRecentJobs() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppliedJobs", Context.MODE_PRIVATE)
        val jobsSet = sharedPreferences.getStringSet("jobs", emptySet()) ?: emptySet()

        val recent = jobsSet.toList().takeLast(2).mapNotNull { jobData ->
            val parts = jobData.split("|")
            if (parts.size >= 4) {
                Job(
                    title = parts[0],
                    company = Company(display_name = parts[1]),
                    location = Location(display_name = parts[2]),
                    redirect_url = parts[3],
                    description = parts.getOrNull(4) ?: "No description",
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
