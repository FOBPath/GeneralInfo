package com.example.fpgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

class JobsFragment : Fragment() {

    private lateinit var jobRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var jobAdapter: JobAdapter

    private val jobList = mutableListOf<Job>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobRecyclerView = view.findViewById(R.id.jobsRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        jobAdapter = JobAdapter(jobList)
        jobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobRecyclerView.adapter = jobAdapter

        fetchJobs()
    }

    private fun fetchJobs() {
        progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.adzuna.com/v1/api/jobs/us/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jobApi = retrofit.create(JobApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = jobApi.getJobs(
                    appId = "92d3a253",
                    apiKey = "fe907628eb40d34e35a55b83f237f9f5",
                    query = "IT"
                )

                val jobs = response.results
                Log.d("JobAPI", "Fetched ${jobs.size} jobs")

                jobs.forEach {
                    Log.d("JobAPI", "Title: ${it.title}")
                }

                CoroutineScope(Dispatchers.Main).launch {
                    progressBar.visibility = View.GONE
                    jobList.clear()
                    jobList.addAll(jobs)
                    jobAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("JobAPI", "Exception: ${e.localizedMessage}")
                CoroutineScope(Dispatchers.Main).launch {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Failed to load jobs: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
