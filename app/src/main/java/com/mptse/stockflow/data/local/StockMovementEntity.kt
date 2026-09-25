package com.mptse.stockflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val productId: Long,
    val productName: String,
    val delta: Double,
    val type: String, // "Entrada" o "Salida"
    val timestamp: Long = System.currentTimeMillis()
)
