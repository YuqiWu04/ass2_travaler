package com.example.ass2_travaler.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class City(
    @SerialName("id") val id: String ="",
    @SerialName("title") val title: String="",
    @SerialName("top_attractions") val top_attractions: String="",
    @SerialName("optimal_budget_usd") val optimal_budget_usd: Int = 0,
    @SerialName("rating") val rating: Int = 0,
    @SerialName("best_season") val best_season: String="",
    @SerializedName("poster") val imageUrl: List<String> = emptyList(),
)

