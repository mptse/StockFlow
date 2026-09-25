package com.mptse.stockflow.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mptse.stockflow.data.local.ProductEntity
import com.mptse.stockflow.data.local.StockDatabase
import com.mptse.stockflow.data.local.StockMovementEntity
import com.mptse.stockflow.data.local.UserEntity
import com.mptse.stockflow.data.local.UserDao
import com.mptse.stockflow.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: InventoryRepository
    private val userDao: UserDao

    val products: StateFlow<List<ProductEntity>>
    val movements: StateFlow<List<StockMovementEntity>>

    private val _isProPlan = MutableStateFlow(false)
    val isProPlan: StateFlow<Boolean> = _isProPlan.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        val database = StockDatabase.getDatabase(application)
        repository = InventoryRepository(database.productDao(), database.stockMovementDao())
        userDao = database.userDao()

        products = repository.allProducts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        movements = repository.allMovements.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    suspend fun registerUser(name: String, email: String, pass: String): Boolean {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            _errorMessage.value = "Ya existe una cuenta registrada con este correo."
            return false
        }
        userDao.insertUser(UserEntity(name = name, email = email, password = pass, isPro = false))
        _currentUserEmail.value = email
        _isProPlan.value = false
        return true
    }

    suspend fun loginUser(email: String, pass: String): Boolean {
        val user = userDao.getUserByEmail(email)
        if (user == null || user.password != pass) {
            _errorMessage.value = "Correo o contraseña incorrectos."
            return false
        }
        _currentUserEmail.value = email
        _isProPlan.value = user.isPro
        return true
    }

    fun setProPlan(isPro: Boolean) {
        _isProPlan.value = isPro
        val email = _currentUserEmail.value
        if (email != null) {
            viewModelScope.launch {
                userDao.updateUserPlan(email, isPro)
            }
        }
    }

    fun upgradeToPro() {
        _isProPlan.value = true
        val email = _currentUserEmail.value
        if (email != null) {
            viewModelScope.launch {
                userDao.updateUserPlan(email, true)
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
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
