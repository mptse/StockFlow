package com.mptse.stockflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val sku: String,
    val price: Double,
    val stockQuantity: Double,
    val minStock: Double,
    val unit: String = "un",
    val warehouse: String = "Almacén Principal"
)
