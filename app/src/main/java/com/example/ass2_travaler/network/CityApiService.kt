package com.example.ass2_travaler.network

import com.example.ass2_travaler.data.CitiesResponse
import retrofit2.Response
import retrofit2.http.GET

interface CityApiService {
    @GET("data.json")
    suspend fun getCities(): Response<CitiesResponse>
}