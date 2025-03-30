package com.example.ass2_travaler.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget_items ORDER BY createdAt DESC")
    fun getAll(): Flow<List<BudgetItem>>

    @Insert
    suspend fun insert(item: BudgetItem): Long

    @Update
    suspend fun update(item: BudgetItem)

    @Delete
    suspend fun delete(item: BudgetItem)

    @Query("SELECT SUM(amount) FROM budget_items")
    fun getTotalSpending(): Flow<Double>
}