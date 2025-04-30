package com.example.fpgroup

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.jobsRecyclerView)
        chipGroup = view.findViewById(R.id.chipGroupFilters)
        searchView = view.findViewById(R.id.jobSearchView)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        noJobsText = view.findViewById(R.id.noJobsText)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobAdapter = JobAdapter(mutableListOf())
        recyclerView.adapter = jobAdapter

        addFilterChips()

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

    private fun addFilterChips() {
        val tags = listOf("Cyber", "Software Development", "Engineer", "IT", "Internship", "Remote")
        chipGroup.removeAllViews()
        for (tag in tags) {
            val chip = Chip(requireContext()).apply {
                text = tag
                isCheckable = true
                setChipBackgroundColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_default))
            }
            chipGroup.addView(chip)
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

                allJobs = jobResults.distinctBy { it.title }.toMutableList()
                jobAdapter.updateJobs(allJobs)
                loadingSpinner.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                if (allJobs.isEmpty()) {
                    noJobsText.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                loadingSpinner.visibility = View.GONE
                noJobsText.visibility = View.VISIBLE
            }
        }
    }

    private fun filterJobs(query: String?) {
        val selectedTags = chipGroup.checkedChipIds.mapNotNull {
            view?.findViewById<Chip>(it)?.text?.toString()?.lowercase()
        }

        val filtered = allJobs.filter { job ->
            val matchesQuery = query.isNullOrBlank() || job.title.contains(query, true)
            val matchesTags = selectedTags.isEmpty() || selectedTags.any { tag ->
                job.title.contains(tag, true) || job.description.contains(tag, true)
            }
            matchesQuery && matchesTags
        }

        jobAdapter.updateJobs(filtered)

        recyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        noJobsText.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }
}
