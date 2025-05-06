package com.example.fpgroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

open class ApplicationAdapter(
    private val list: List<Map<String, Any>>
) : RecyclerView.Adapter<ApplicationAdapter.ViewHolder>() {

    open fun onWithdrawClicked(position: Int) {
        // To be overridden in fragment
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jobTitle: TextView = itemView.findViewById(R.id.itemJobTitle)
        val company: TextView = itemView.findViewById(R.id.itemJobCompany)
        val status: TextView = itemView.findViewById(R.id.itemStatus)
        val withdrawButton: Button = itemView.findViewById(R.id.withdrawButton)

        init {
            withdrawButton.setOnClickListener {
                onWithdrawClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_application, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = list[position]
        val status = app["status"] as? String ?: "Submitted"

        holder.jobTitle.text = app["jobTitle"] as? String ?: "Unknown"
        holder.company.text = app["jobCompany"] as? String ?: "Unknown"
        holder.status.text = status

        // Hide withdraw button if already withdrawn
        holder.withdrawButton.visibility =
            if (status.equals("Withdrawn", ignoreCase = true)) View.GONE else View.VISIBLE
    }
}
