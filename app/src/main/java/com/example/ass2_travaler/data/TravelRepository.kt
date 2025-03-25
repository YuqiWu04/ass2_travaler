package com.example.ass2_travaler.data


import android.util.Log
import com.example.ass2_travaler.model.City
import com.example.ass2_travaler.network.CityApiService



interface TravelRepository {
    //Get city list from API
    suspend fun getCities(): List<City>

    //Find city by ID
    fun getCityById(cityId: String, cities: List<City>): City?
}



class NetworkCityRepository(
    private val cityApiService: CityApiService
) : TravelRepository {
    private val tag = "NetworkCityRepo"

   //Get a list of cities
    override suspend fun getCities(): List<City> {
        return try {
            val response = cityApiService.getCities()
            if (response.isSuccessful) {
                response.body()?.cities ?: emptyList() // Parsing Nested Data
            } else {
                Log.e(tag, "HTTP ${response.code()}: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(tag, "Network error: ${e.message}")
            emptyList()
        }
    }


  //Find city by ID
    override fun getCityById(cityId: String, cities: List<City>): City? {
        Log.d(tag, "Searching for city ID: $cityId")
        return cities.find { it.id == cityId } // Use the correct ID field
    }

}