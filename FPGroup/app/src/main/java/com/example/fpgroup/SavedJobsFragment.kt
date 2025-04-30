package com.example.fpgroup

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class SavedJobsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobAdapter
    private var savedJobsList: MutableList<Job> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_saved_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.savedJobsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = JobAdapter(savedJobsList)
        recyclerView.adapter = adapter

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirestoreHelper.fetchSavedJobs(userId) { jobs ->
            savedJobsList.clear()
            savedJobsList.addAll(jobs)
            adapter.notifyDataSetChanged()
        }
    }
}
