package com.example.fpgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class JobsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jobs, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.jobsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val jobsList = listOf(
            Job(
                "Software Engineer", "Google", "Remote", "Develop and maintain software solutions.",
                jobUrl = TODO()
            ),
            Job(
                "Data Analyst",
                "Microsoft",
                "Seattle, WA",
                "Analyze and interpret complex data sets.",
                jobUrl = TODO()
            ),
            Job(
                "Cybersecurity Analyst",
                "IBM",
                "New York, NY",
                "Ensure security of company networks.",
                jobUrl = TODO()
            ),
            Job(
                "AI Researcher",
                "OpenAI",
                "San Francisco, CA",
                "Develop cutting-edge AI technologies.",
                jobUrl = TODO()
            ),
            Job(
                "Mechanical Engineer", "Tesla", "Austin, TX", "Design and test mechanical systems.",
                jobUrl = TODO()
            )
        )

        val adapter = JobAdapter(jobsList)
        recyclerView.adapter = adapter

        return view
    }
}