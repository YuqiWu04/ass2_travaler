
package com.example.ass2_travaler.data

import com.example.ass2_travaler.network.CityApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val cityRepository: TravelRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://yuqiwu04.github.io/my_api_repo/"

    // Building Retrofit Instances Serially with Kotlin
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Urban data interface services
    private val cityApiService: CityApiService by lazy {
        retrofit.create(CityApiService::class.java)
    }

    // Dependency Injection Implementation
    override val cityRepository: TravelRepository by lazy {
        NetworkCityRepository(cityApiService)
    }
}