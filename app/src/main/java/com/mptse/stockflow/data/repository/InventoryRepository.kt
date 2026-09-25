package com.mptse.stockflow.data.repository

import com.mptse.stockflow.data.local.ProductDao
import com.mptse.stockflow.data.local.ProductEntity
import com.mptse.stockflow.data.local.StockMovementDao
import com.mptse.stockflow.data.local.StockMovementEntity
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val productDao: ProductDao,
    private val movementDao: StockMovementDao
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val allMovements: Flow<List<StockMovementEntity>> = movementDao.getAllMovements()

    suspend fun insert(product: ProductEntity) {
        productDao.insertProduct(product)
        movementDao.insertMovement(
            StockMovementEntity(
                productId = 0,
                productName = product.name,
                delta = product.stockQuantity,
                type = "Creación de Producto"
            )
        )
    }

    suspend fun update(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun delete(product: ProductEntity) {
        productDao.deleteProduct(product)
        movementDao.insertMovement(
            StockMovementEntity(
                productId = product.id,
                productName = product.name,
                delta = -product.stockQuantity,
                type = "Eliminación"
            )
        )
    }

    suspend fun updateStock(productId: Long, delta: Double) {
        val product = productDao.getProductById(productId)
        productDao.updateStock(productId, delta)
        if (product != null) {
            val type = if (delta > 0) "Entrada de Stock" else "Salida de Stock"
            movementDao.insertMovement(
                StockMovementEntity(
                    productId = productId,
                    productName = product.name,
                    delta = delta,
                    type = type
                )
            )
        }
    }
}
