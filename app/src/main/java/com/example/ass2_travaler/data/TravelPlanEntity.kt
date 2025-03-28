package com.example.ass2_travaler.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travel_plan")
data class TravelPlan(
    @PrimaryKey val id: String,
    val eventName: String,
    val dateTime: Long,
    val location: String
)
