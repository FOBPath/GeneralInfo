package com.example.fpgroup

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppliedJobsManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("AppliedJobs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveAppliedJob(job: Job) {
        val appliedJobs = getAppliedJobs().toMutableList()
        appliedJobs.add(job)
        val json = gson.toJson(appliedJobs)
        sharedPreferences.edit().putString("applied_jobs", json).apply()
    }

    fun getAppliedJobs(): List<Job> {
        val json = sharedPreferences.getString("applied_jobs", "[]")
        val type = object : TypeToken<List<Job>>() {}.type
        return gson.fromJson(json, type)
    }
}






