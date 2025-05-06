package com.example.fpgroup

import android.os.Bundle
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

    private lateinit var searchView: SearchView
    private lateinit var locationInput: EditText
    private lateinit var salaryRangeInput: EditText
    private lateinit var clearFiltersBtn: Button
    private lateinit var chipGroup: ChipGroup
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var noJobsText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private var allJobs: MutableList<Job> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchView = view.findViewById(R.id.jobSearchView)
        locationInput = view.findViewById(R.id.locationInput)
        salaryRangeInput = view.findViewById(R.id.salaryRangeInput)
        clearFiltersBtn = view.findViewById(R.id.clearFiltersBtn)
        chipGroup = view.findViewById(R.id.chipGroupFilters)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        noJobsText = view.findViewById(R.id.noJobsText)
        recyclerView = view.findViewById(R.id.jobsRecyclerView)

        jobAdapter = JobAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = jobAdapter

        addFilterChips()
        fetchJobs()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                filterJobs()
                return true
            }
        })

        chipGroup.setOnCheckedStateChangeListener { _, _ ->
            filterJobs()
        }

        clearFiltersBtn.setOnClickListener {
            searchView.setQuery("", false)
            locationInput.text.clear()
            salaryRangeInput.text.clear()
            chipGroup.clearCheck()
            filterJobs()
        }
    }

    private fun addFilterChips() {
        val tags = listOf("Cyber", "Software Development", "Engineer", "IT", "Internship", "Remote")
        chipGroup.removeAllViews()
        for (tag in tags) {
            val chip = Chip(requireContext()).apply {
                text = tag
                isCheckable = true
                isClickable = true
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                chipStrokeWidth = 1f
                chipStrokeColor = ContextCompat.getColorStateList(requireContext(), R.color.purple_500)
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
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

    private fun filterJobs() {
        val query = searchView.query.toString().trim()
        val locationFilter = locationInput.text.toString().trim()
        val salaryRange = salaryRangeInput.text.toString().trim()
        val selectedTags = chipGroup.checkedChipIds.mapNotNull {
            view?.findViewById<Chip>(it)?.text?.toString()?.lowercase()
        }

        val filtered = allJobs.filter { job ->
            val matchesQuery = query.isBlank() || job.title.contains(query, ignoreCase = true)

            val matchesLocation = locationFilter.isBlank() ||
                    job.location.display_name.contains(locationFilter, ignoreCase = true)

            val matchesSalary = if (salaryRange.contains("-")) {
                val parts = salaryRange.split("-")
                val min = parts[0].replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0
                val max = parts.getOrNull(1)?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: Int.MAX_VALUE

                val jobSalaryNum = job.salary?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0
                jobSalaryNum in min..max
            } else true

            val matchesTags = selectedTags.isEmpty() || selectedTags.any {
                job.title.lowercase().contains(it) || job.description.lowercase().contains(it)
            }

            matchesQuery && matchesLocation && matchesSalary && matchesTags
        }

        jobAdapter.updateJobs(filtered)
        recyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        noJobsText.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE

        if (filtered.isEmpty()) {
            Toast.makeText(requireContext(), "No jobs match your filters", Toast.LENGTH_SHORT).show()
        }
    }
}
