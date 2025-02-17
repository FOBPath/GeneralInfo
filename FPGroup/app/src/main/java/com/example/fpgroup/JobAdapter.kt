package com.example.fpgroup

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobAdapter(private val jobList: List<Job>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jobTitle: TextView = itemView.findViewById(R.id.jobTitle)
        val jobCompany: TextView = itemView.findViewById(R.id.jobCompany)
        val jobLocation: TextView = itemView.findViewById(R.id.jobLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.jobTitle.text = job.title
        holder.jobCompany.text = job.company
        holder.jobLocation.text = job.location

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, JobDetailsActivity::class.java)
            intent.putExtra("JOB_TITLE", job.title)
            intent.putExtra("JOB_COMPANY", job.company)
            intent.putExtra("JOB_LOCATION", job.location)
            intent.putExtra("JOB_DESCRIPTION", job.description)
            intent.putExtra("JOB_URL", job.jobUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = jobList.size
}
