package com.example.ass2_travaler.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class City(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerializedName("poster") // 与API返回字段名匹配
    val imageUrl: String
)

