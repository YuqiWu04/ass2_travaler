package com.example.ass2_travaler.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelPlanDao {
    @Query("SELECT * FROM travel_plan ORDER BY dateTime ASC")
    fun getAllTravelPlans(): Flow<List<TravelPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelPlan(plan: TravelPlan): Long

    @Update
    suspend fun updateTravelPlan(plan: TravelPlan): Int

    @Delete
    suspend fun deleteTravelPlan(plan: TravelPlan): Int
}