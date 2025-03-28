package com.example.ass2_travaler.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TravelPlan::class], version = 1, exportSchema = false)
abstract class TravelPlanDatabase : RoomDatabase() {
    abstract fun travelPlanDao(): TravelPlanDao

    companion object {
        @Volatile
        private var INSTANCE: TravelPlanDatabase? = null

        fun getDatabase(context: Context): TravelPlanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TravelPlanDatabase::class.java,
                    "travel_plan_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}