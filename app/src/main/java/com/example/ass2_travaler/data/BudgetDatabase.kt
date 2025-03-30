package com.example.ass2_travaler.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BudgetItem::class], version = 1)
abstract class BudgetDatabase : RoomDatabase() {
    abstract fun dao(): BudgetDao
}