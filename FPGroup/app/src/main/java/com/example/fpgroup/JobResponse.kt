package com.example.fpgroup

data class JobResponse(
    val results: List<Job>
)

data class Job(
    val title: String,
    val company: Company,
    val location: Location,
    val description: String,
    val redirect_url: String,
    val salary: String? = null,
    val qualifications: String? = null
)

data class Company(val display_name: String)
data class Location(val display_name: String)
