package com.mptse.stockflow.data.repository

import com.mptse.stockflow.data.local.ProductDao
import com.mptse.stockflow.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow

class InventoryRepository(private val productDao: ProductDao) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun insert(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    suspend fun update(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun delete(product: ProductEntity) {
        productDao.deleteProduct(product)
    }

    suspend fun updateStock(productId: Long, delta: Double) {
        productDao.updateStock(productId, delta)
    }
}
