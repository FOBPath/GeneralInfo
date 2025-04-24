package com.example.fpgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var jobRecyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private var allJobs: MutableList<Job> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobRecyclerView = view.findViewById(R.id.jobRecyclerView)
        jobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobAdapter = JobAdapter(allJobs)
        jobRecyclerView.adapter = jobAdapter

        fetchJobs()

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

    private fun fetchJobs() {
        lifecycleScope.launch {
            try {
                val response = AdzunaApi.service.getJobs(
                    appId = "92d3a253",      // replace with your actual Adzuna App ID
                    apiKey = "fe907628eb40d34e35a55b83f237f9f5",    // replace with your actual Adzuna API key
                    query = "Computer Science"
                )
                val jobs = response.results
                allJobs.clear()
                allJobs.addAll(jobs)
                jobAdapter.updateJobs(jobs)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load jobs", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun filterJobs(query: String?) {
        val filtered = if (!query.isNullOrEmpty()) {
            allJobs.filter { it.title.contains(query, ignoreCase = true) }
        } else {
            allJobs
        }
        jobAdapter.updateJobs(filtered)
    }
}
