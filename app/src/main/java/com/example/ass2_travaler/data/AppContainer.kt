
package com.example.ass2_travaler.data

import android.content.Context
import androidx.room.Room
import com.example.ass2_travaler.network.CityApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val cityRepository: TravelRepository
    val travelPlanRepository: TravelPlanRepository
    val budgetRepository: BudgetRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
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
    // TravelPlanRepository
    private val travelPlanDatabase = TravelPlanDatabase.getDatabase(context)
    override val travelPlanRepository: TravelPlanRepository by lazy {
        LocalTravelPlanRepository(travelPlanDatabase.travelPlanDao())
    }
    private val budgetDatabase = Room.databaseBuilder(
        context,
        BudgetDatabase::class.java, "budget.db"
    ).build()

    override val budgetRepository: BudgetRepository by lazy {
        LocalBudgetRepository(budgetDatabase.dao())

}

}