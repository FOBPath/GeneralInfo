package com.example.fpgroup

data class JobResponse(
    val results: List<Job>
)

data class Job(
    val title: String,
    val company: String,
    val location: String,
    val description: String,
    val redirect_url: String
)
