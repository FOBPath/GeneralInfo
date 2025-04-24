package com.example.fpgroup

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ApplicationAdapter(private val list: List<Map<String, Any>>) :
    RecyclerView.Adapter<ApplicationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jobTitle: TextView = itemView.findViewById(R.id.itemJobTitle)
        val company: TextView = itemView.findViewById(R.id.itemJobCompany)
        val status: TextView = itemView.findViewById(R.id.itemStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_application, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = list[position]
        holder.jobTitle.text = app["jobTitle"] as? String ?: "Unknown"
        holder.company.text = app["jobCompany"] as? String ?: ""
        holder.status.text = "Submitted"
    }
}
