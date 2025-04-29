package com.example.fpgroup

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class JobsFragment : Fragment() {

    private lateinit var jobAdapter: JobAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var chipGroup: ChipGroup
    private lateinit var searchView: SearchView
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var noJobsText: TextView
    private var allJobs: MutableList<Job> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_jobs, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.jobsRecyclerView)
        chipGroup = view.findViewById(R.id.chipGroupFilters)
        searchView = view.findViewById(R.id.jobSearchView)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        noJobsText = view.findViewById(R.id.noJobsText)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobAdapter = JobAdapter(mutableListOf())
        recyclerView.adapter = jobAdapter

        fetchJobs()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                filterJobs(newText)
                return true
            }
        })

        chipGroup.setOnCheckedStateChangeListener { _, _ ->
            filterJobs(searchView.query.toString())
        }
    }

    private fun fetchJobs() {
        loadingSpinner.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        noJobsText.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val keywords = listOf("Cybersecurity", "Software Developer", "IT", "Computer Science", "Engineering")

                val jobResults = mutableListOf<Job>()

                for (keyword in keywords) {
                    val response = AdzunaApi.service.getJobs(
                        appId = "92d3a253",
                        apiKey = "fe907628eb40d34e35a55b83f237f9f5",
                        query = keyword
                    )
                    jobResults.addAll(response.results)
                }

                allJobs = jobResults.distinctBy { it.title }.toMutableList() // remove duplicates
                jobAdapter.updateJobs(allJobs)

                loadingSpinner.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                if (allJobs.isEmpty()) {
                    noJobsText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }

                Log.d("JobsFragment", "Fetched ${allJobs.size} jobs from multiple keywords")
            } catch (e: Exception) {
                e.printStackTrace()
                loadingSpinner.visibility = View.GONE
                noJobsText.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun filterJobs(query: String?) {
        val selectedTags = chipGroup.checkedChipIds
            .mapNotNull { id -> view?.findViewById<Chip>(id)?.text?.toString()?.lowercase() }
            ?: emptyList()

        val filtered = allJobs.filter { job ->
            val matchesQuery = query.isNullOrBlank() ||
                    job.title.contains(query, true) ||
                    job.company.display_name.contains(query, true) ||
                    job.description.contains(query, true)

            val matchesTags = selectedTags.isEmpty() || selectedTags.any { tag ->
                when (tag) {
                    "cyber" -> job.title.contains("cyber", true) || job.description.contains("cyber", true)
                    "software development" -> job.title.contains("software", true) || job.description.contains("developer", true)
                    "engineer" -> job.title.contains("engineer", true) || job.description.contains("engineer", true)
                    "it" -> job.title.contains("it", true) || job.description.contains("information technology", true)
                    else -> false
                }
            }

            matchesQuery && matchesTags
        }

        jobAdapter.updateJobs(filtered)

        if (filtered.isEmpty()) {
            noJobsText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            noJobsText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        Log.d("JobsFragment", "Filtered to ${filtered.size} jobs with query=$query and tags=$selectedTags")
    }
}
