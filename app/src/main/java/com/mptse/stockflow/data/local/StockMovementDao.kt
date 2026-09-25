package com.mptse.stockflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMovementDao {
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC")
    fun getAllMovements(): Flow<List<StockMovementEntity>>

    @Insert
    suspend fun insertMovement(movement: StockMovementEntity)
}
