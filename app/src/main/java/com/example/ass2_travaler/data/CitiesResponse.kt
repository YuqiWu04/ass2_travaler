package com.example.ass2_travaler.data

import com.example.ass2_travaler.model.City
import com.google.gson.annotations.SerializedName


data class CitiesResponse(
    @SerializedName("cities")
    val cities: List<City>
)