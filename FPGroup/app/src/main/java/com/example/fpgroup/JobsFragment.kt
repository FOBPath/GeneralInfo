package com.example.fpgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class JobsFragment : Fragment() {

    private lateinit var jobRecyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private var jobList: MutableList<Job> = mutableListOf()

    private val selectedTags = mutableSetOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobRecyclerView = view.findViewById(R.id.jobRecyclerView)
        jobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobAdapter = JobAdapter(jobList)
        jobRecyclerView.adapter = jobAdapter

        // Animate new jobs with fade-in
        jobRecyclerView.itemAnimator = DefaultItemAnimator().apply {
            addDuration = 500
        }

        val chipGroup = view.findViewById<ChipGroup>(R.id.tagChipGroup)
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                val tagText = buttonView.text.toString()
                if (isChecked) selectedTags.add(tagText) else selectedTags.remove(tagText)
                fetchJobs(selectedTags)
            }
        }

        // Initial fetch (empty = fallback to CS jobs)
        fetchJobs(setOf("Computer Science"))

        val searchView = view.findViewById<SearchView>(R.id.jobSearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterJobs(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterJobs(newText)
                return true
            }
        })
    }

    private fun fetchJobs(tags: Set<String>) {
        lifecycleScope.launch {
            try {
                val queries = if (tags.isEmpty()) listOf("Computer Science") else tags.toList()
                val combinedJobs = mutableListOf<Job>()
                for (query in queries) {
                    val response = AdzunaApi.service.getJobs(
                        appId = "92d3a253",
                        apiKey = "fe907628eb40d34e35a55b83f237f9f5",
                        query = query
                    )
                    combinedJobs.addAll(response.results)
                }

                jobList.clear()
                jobList.addAll(combinedJobs)
                jobAdapter.updateJobs(combinedJobs)

                // Optional animation
                jobRecyclerView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load jobs", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterJobs(query: String?) {
        val filtered = if (!query.isNullOrEmpty()) {
            jobList.filter { it.title.contains(query, ignoreCase = true) }
        } else jobList
        jobAdapter.updateJobs(filtered)
    }
}
