package com.example.fpgroup

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AdzunaApi {
    private const val BASE_URL = "https://api.adzuna.com/v1/api/jobs/us/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: JobApiService by lazy {
        retrofit.create(JobApiService::class.java)
    }
}
