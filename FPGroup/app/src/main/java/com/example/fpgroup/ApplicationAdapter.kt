package com.example.fpgroup

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

open class ApplicationAdapter(
    private val list: List<Map<String, Any>>
) : RecyclerView.Adapter<ApplicationAdapter.ViewHolder>() {

    open fun onWithdrawClicked(position: Int) {} // For override

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jobTitle: TextView = itemView.findViewById(R.id.itemJobTitle)
        val company: TextView = itemView.findViewById(R.id.itemJobCompany)
        val email: TextView = itemView.findViewById(R.id.itemEmail)
        val date: TextView = itemView.findViewById(R.id.itemDate)
        val status: TextView = itemView.findViewById(R.id.itemStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_application, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = list[position]
        holder.jobTitle.text = app["jobTitle"] as? String ?: "Unknown"
        holder.company.text = app["jobCompany"] as? String ?: "Unknown"
        holder.email.text = "Contact: ${app["email"] as? String ?: "N/A"}"

        val timestamp = app["timestamp"] as? Long
        val dateString = if (timestamp != null) {
            android.text.format.DateFormat.format("MMM dd, yyyy", timestamp).toString()
        } else "N/A"
        holder.date.text = "Applied: $dateString"

        val statusText = app["status"] as? String ?: "Submitted"
        holder.status.text = statusText

        // Color code the status
        holder.status.setTextColor(
            when (statusText.lowercase()) {
                "withdrawn" -> Color.RED
                "under review" -> Color.parseColor("#FFA500") // orange
                "submitted" -> Color.BLUE
                "accepted" -> Color.parseColor("#228B22") // forest green
                else -> Color.DKGRAY
            }
        )

        // Click to withdraw
        holder.itemView.setOnLongClickListener {
            onWithdrawClicked(position)
            true
        }
    }
}
