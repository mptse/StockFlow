package com.mptse.stockflow.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mptse.stockflow.data.local.ProductEntity
import com.mptse.stockflow.data.local.StockDatabase
import com.mptse.stockflow.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: InventoryRepository

    val products: StateFlow<List<ProductEntity>>

    private val _isProPlan = MutableStateFlow(false)
    val isProPlan: StateFlow<Boolean> = _isProPlan.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        val productDao = StockDatabase.getDatabase(application).productDao()
        repository = InventoryRepository(productDao)
        products = repository.allProducts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun setProPlan(isPro: Boolean) {
        _isProPlan.value = isPro
    }

    fun upgradeToPro() {
        _isProPlan.value = true
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun addProduct(name: String, sku: String, price: Double, stock: Double, minStock: Double, unit: String, warehouse: String) {
        if (!_isProPlan.value && products.value.size >= 50) {
            _errorMessage.value = "Has alcanzado el límite de 50 productos del Plan Gratuito. ¡Actualiza a Plan Pro para añadir más!"
            return
        }

        val finalWarehouse = if (!_isProPlan.value) "Almacén Principal" else warehouse

        viewModelScope.launch {
            val product = ProductEntity(
                name = name,
                sku = sku,
                price = price,
                stockQuantity = stock,
                minStock = minStock,
                unit = unit,
                warehouse = finalWarehouse
            )
            repository.insert(product)
        }
    }

    fun updateStock(productId: Long, delta: Double) {
        viewModelScope.launch {
            repository.updateStock(productId, delta)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }
}
