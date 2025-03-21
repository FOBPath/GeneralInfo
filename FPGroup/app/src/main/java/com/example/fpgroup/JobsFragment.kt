package com.example.fpgroup

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private lateinit var progressBar: ProgressBar
    private var jobList: MutableList<Job> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_jobs, container, false)

        recyclerView = view.findViewById(R.id.jobsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobAdapter = JobAdapter(jobList)
        recyclerView.adapter = jobAdapter

        progressBar = view.findViewById(R.id.progressBar)

        fetchJobs()
        return view
    }

    private fun fetchJobs() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val response = ApiClient.retrofit.create(JobApiService::class.java).getJobs(
                    appId = "92d3a253",
                    apiKey = "fe907628eb40d34e35a55b83f237f9f5",
                    query = "Computer Science OR IT OR Engineering OR Cybersecurity"
                )
                jobList.clear()
                jobList.addAll(response.results)
                jobAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}


}
