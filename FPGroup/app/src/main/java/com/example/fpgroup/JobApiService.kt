package com.example.fpgroup

import retrofit2.http.GET
import retrofit2.http.Query

interface JobApiService {
    @GET("search/1")
    suspend fun getJobs(
        @Query("app_id") appId: String,
        @Query("app_key") apiKey: String,
        @Query("results_per_page") resultsPerPage: Int = 10,
        @Query("what") query: String,
        @Query("content-type") contentType: String = "application/json"
    ): JobResponse
}
