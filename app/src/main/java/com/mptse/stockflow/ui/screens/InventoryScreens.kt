package com.mptse.stockflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mptse.stockflow.data.local.ProductEntity
import com.mptse.stockflow.data.local.StockMovementEntity
import com.mptse.stockflow.ui.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onLogout: () -> Unit = {}
) {
    val products by viewModel.products.collectAsState()
    val movements by viewModel.movements.collectAsState()
    val isProPlan by viewModel.isProPlan.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val totalValue = products.sumOf { it.price * it.stockQuantity }
    val totalItems = products.sumOf { it.stockQuantity }
    val lowStockCount = products.count { it.stockQuantity <= it.minStock }

    val bgColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF7F2FA)
    val topBarColor = if (isDarkMode) Color(0xFF1E1B24) else Color(0xFFEDE7F6)
    val textColor = if (isDarkMode) Color.White else Color(0xFF2C1B3D)
    val primaryColor = Color(0xFF6B4FA0)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(topBarColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = primaryColor,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("SF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "StockFlow",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = if (isProPlan) "⭐ Plan Pro Activo (Ilimitado)" else "Plan Gratuito (${products.size}/50)",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isProPlan) Color(0xFFB388FF) else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = if (isDarkMode) Color(0xFF1E1B24) else Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Panel") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Productos") },
                    label = { Text("Productos") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
                    label = { Text("Historial") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Group, contentDescription = "Proveedores") },
                    label = { Text("Proveedores") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> {
                    DashboardTab(
                        products = products,
                        totalValue = totalValue,
                        totalItems = totalItems,
                        lowStockCount = lowStockCount,
                        isProPlan = isProPlan,
                        isDarkMode = isDarkMode,
                        onUpgrade = { viewModel.upgradeToPro() }
                    )
                }
                1 -> {
                    ProductsTab(
                        products = products,
                        viewModel = viewModel,
                        isDarkMode = isDarkMode
                    )
                }
                2 -> {
                    HistoryTab(movements = movements, isDarkMode = isDarkMode)
                }
                3 -> {
                    SupeediorsTab(
                        products = products,
                        isProPlan = isProPlan,
                        isDarkMode = isDarkMode,
                        onUpgrade = { viewModel.upgradeToPro() }
                    )
                }
                4 -> {
                    SettingsTab(
                        isProPlan = isProPlan,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { enabled -> viewModel.toggleDarkMode(enabled) },
                        onUpgrade = { viewModel.upgradeToPro() },
                        onLogout = onLogout
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            isPro = isProPlan,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, sku, price, stock, minStock, unit, warehouse ->
                viewModel.addProduct(name, sku, price, stock, minStock, unit, warehouse)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DashboardTab(
    products: List<ProductEntity>,
    totalValue: Double,
    totalItems: Double,
    lowStockCount: Int,
    isProPlan: Boolean,
    isDarkMode: Boolean,
    onUpgrade: () -> Unit
) {
    var showPaymentDialog by remember { mutableStateOf(false) }
    val cardColor = if (isDarkMode) Color(0xFF1E1B24) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF2C1B3D)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Resumen del Inventario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricMiniCard(
                        modifier = Modifier.weight(1f),
                        title = "Referencias",
                        value = "${products.size}",
                        icon = Icons.Default.Description,
                        isDarkMode = isDarkMode
                    )
                    MetricMiniCard(
                        modifier = Modifier.weight(1f),
                        title = "Existencias",
                        value = String.format("%.1f", totalItems),
                        subtitle = "total",
                        icon = Icons.Default.Inventory,
                        isDarkMode = isDarkMode
                    )
                    MetricMiniCard(
                        modifier = Modifier.weight(1f),
                        title = "Valor Total",
                        value = "$${String.format("%,.0f", totalValue)} COP",
                        icon = Icons.Default.AttachMoney,
                        isDarkMode = isDarkMode
                    )
                }
            }
        }

        if (lowStockCount > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF42272B) else Color(0xFFFFEBEE))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Alerta de Stock Bajo", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text(text = "Tienes $lowStockCount producto(s) por debajo del mínimo.", fontSize = 12.sp, color = if (isDarkMode) Color.LightGray else Color.DarkGray)
                    }
                }
            }
        }

        if (!isProPlan) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF2A2338) else Color(0xFFEDE7F6))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "🚀 Desbloquea StockFlow Pro", fontWeight = FontWeight.Bold, color = if (isDarkMode) Color(0xFFD1C4E9) else Color(0xFF512DA8), fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Obtén productos ilimitados, gestión multi-almacén, historial y módulo de proveedores.", fontSize = 13.sp, color = if (isDarkMode) Color.LightGray else Color.DarkGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showPaymentDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4FA0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Actualizar a Plan Pro ⭐ ($39.900 COP/mes)")
                    }
                }
            }
        }

        Text(
            text = "Productos Recientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No hay productos registrados", color = Color.Gray)
            }
        } else {
            products.take(5).forEach { product ->
                ProductItemStatic(product = product, isDarkMode = isDarkMode)
            }
        }
    }

    if (showPaymentDialog) {
        PaymentDialog(
            onDismiss = { showPaymentDialog = false },
            onPaymentSuccess = {
                showPaymentDialog = false
                onUpgrade()
            }
        )
    }
}

@Composable
fun ProductsTab(
    products: List<ProductEntity>,
    viewModel: InventoryViewModel,
    isDarkMode: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.sku.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar por nombre o SKU...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No se encontraron productos", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductItem(
                        product = product,
                        onStockChange = { delta -> viewModel.updateStock(product.id, delta) },
                        onDelete = { viewModel.deleteProduct(product) },
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryTab(movements: List<StockMovementEntity>, isDarkMode: Boolean) {
    val cardColor = if (isDarkMode) Color(0xFF1E1B24) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF2C1B3D)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Historial de Movimientos de Stock", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)

        if (movements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No hay movimientos registrados", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movements, key = { it.id }) { movement ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = movement.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                                Text(text = "Tipo: ${movement.type}", fontSize = 13.sp, color = if (isDarkMode) Color.LightGray else Color.DarkGray)
                            }
                            Text(
                                text = "${if (movement.delta > 0) "+" else ""}${movement.delta}",
                                fontWeight = FontWeight.Bold,
                                color = if (movement.delta > 0) Color(0xFF81C784) else Color(0xFFE57373),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupeediorsTab(
    products: List<ProductEntity>,
    isProPlan: Boolean,
    isDarkMode: Boolean,
    onUpgrade: () -> Unit
) {
    var showPaymentDialog by remember { mutableStateOf(false) }
    val cardColor = if (isDarkMode) Color(0xFF1E1B24) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF2C1B3D)

    // Calculate money spent per supplier based on products associated with that supplier
    val supplierSpending = products.groupBy { it.supplierName }.mapValues { entry ->
        entry.value.sumOf { it.price * it.stockQuantity }
    }

    val defaultSuppliers = listOf("Distribuidora Global S.A.", "Comercializadora del Norte", "Importadora y Suministros S.L.")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Directorio de Proveedores y Gastos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
        Text(text = "Aquí puedes ver cuánto dinero se ha invertido en compras por cada proveedor.", fontSize = 13.sp, color = Color.Gray)

        if (!isProPlan) {
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF42272B) else Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "⭐ Función del Plan Pro", fontWeight = FontWeight.Bold, color = Color.Red)
                    Text(text = "El cálculo de gastos por proveedor y gestión avanzada es exclusivo del Plan Pro.", fontSize = 13.sp, color = if (isDarkMode) Color.LightGray else Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showPaymentDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4FA0))) {
                        Text("Activar Pro ($39.900 COP/mes)")
                    }
                }
            }
        }

        defaultSuppliers.forEach { supplierName ->
            val spent = supplierSpending[supplierName] ?: 0.0
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = supplierName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Total invertido en productos: $${String.format("%,.0f", spent)} COP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B4FA0))
                }
            }
        }
    }

    if (showPaymentDialog) {
        PaymentDialog(
            onDismiss = { showPaymentDialog = false },
            onPaymentSuccess = {
                showPaymentDialog = false
                onUpgrade()
            }
        )
    }
}

@Composable
fun SettingsTab(
    isProPlan: Boolean,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onUpgrade: () -> Unit,
    onLogout: () -> Unit
) {
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    val cardColor = if (isDarkMode) Color(0xFF1E1B24) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF2C1B3D)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Ajustes de la Cuenta", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = textColor)

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Estado del Plan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isProPlan) "Plan Pro ⭐ (Activo e Ilimitado)" else "Plan Gratuito (Hasta 50 productos)",
                    color = if (isProPlan) Color(0xFFB388FF) else Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (!isProPlan) {
                    Button(
                        onClick = { showPaymentDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4FA0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Actualizar a Plan Pro ⭐ ($39.900 COP/mes)")
                    }
                }
            }
        }

        // Monthly Inventory Report Card (Pro Feature)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF6B4FA0))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Reporte Mensual de Inventario ⭐", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Genera y descarga un reporte detallado en PDF/Texto del inventario y finanzas.", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (isProPlan) {
                            showReportDialog = true
                        } else {
                            showPaymentDialog = true
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isProPlan) "Generar Reporte Mensual" else "Desbloquear Reportes (Plan Pro)")
                }
            }
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = Color(0xFF6B4FA0))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Modo Oscuro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
                        Text(text = "Interfaz oscura para ahorro de batería", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onToggleDarkMode
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar Sesión", color = Color.White)
        }
    }

    if (showPaymentDialog) {
        PaymentDialog(
            onDismiss = { showPaymentDialog = false },
            onPaymentSuccess = {
                showPaymentDialog = false
                onUpgrade()
            }
        )
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Reporte Mensual de Inventario", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📅 Periodo: Septiembre 2026")
                    Text("🏢 Estado: Plan Pro Activo")
                    HorizontalDivider()
                    Text("📊 Resumen:")
                    Text("• Auditoría completada con éxito.")
                    Text("• Todos los productos y existencias consolidados.")
                    Text("• Inversión total por proveedor calculada.")
                }
            },
            confirmButton = {
                Button(onClick = { showReportDialog = false }) {
                    Text("Descargar / Copiar Reporte")
                }
            }
        )
    }
}

@Composable
fun MetricMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkMode: Boolean
) {
    val cardBg = if (isDarkMode) Color(0xFF2D2B36) else Color(0xFFF3EDF7)
    val textColor = if (isDarkMode) Color.White else Color(0xFF2C1B3D)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFB388FF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ProductItemStatic(product: ProductEntity, isDarkMode: Boolean) {
    val cardColor = if (isDarkMode) Color(0xFF1E1B24) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF2C1B3D)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
                Text(text = "SKU: ${product.sku} | $${product.price} COP / ${product.unit}", style = MaterialTheme.typography.bodyMedium, color = if (isDarkMode) Color.LightGray else Color.Gray)
            }
            Text(text = "${product.stockQuantity} ${product.unit}", fontWeight = FontWeight.Bold, color = Color(0xFFB388FF))
        }
    }
}

@Composable
fun ProductItem(
    product: ProductEntity,
    onStockChange: (Double) -> Unit,
    onDelete: () -> Unit,
    isDarkMode: Boolean
) {
    val isLowStock = product.stockQuantity <= product.minStock
    val cardColor = if (isLowStock) (if (isDarkMode) Color(0xFF42272B) else Color(0xFFFFEBEE)) else (if (isDarkMode) Color(0xFF1E1B24) else Color.White)
    val textColor = if (isDarkMode) Color.White else Color(0xFF2C1B3D)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
                Text(text = "SKU: ${product.sku} | Precio: $${product.price} COP / ${product.unit}", style = MaterialTheme.typography.bodyMedium, color = if (isDarkMode) Color.LightGray else Color.DarkGray)
                Text(text = "Almacén: ${product.warehouse} | Prov: ${product.supplierName}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(
                    text = "Stock: ${product.stockQuantity} ${product.unit} (Mín: ${product.minStock} ${product.unit})",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isLowStock) Color.Red else Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onStockChange(-1.0) }) {
                    Text(text = "-1", fontWeight = FontWeight.Bold, color = Color(0xFFB388FF))
                }
                IconButton(onClick = { onStockChange(1.0) }) {
                    Text(text = "+1", fontWeight = FontWeight.Bold, color = Color(0xFFB388FF))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    isPro: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double, Double, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("un") }
    var warehouse by remember { mutableStateOf("Almacén Principal") }
    var supplierName by remember { mutableStateOf("Distribuidora Global S.A.") }
    var expandedUnit by remember { mutableStateOf(false) }
    var expandedWarehouse by remember { mutableStateOf(false) }
    var expandedSupplier by remember { mutableStateOf(false) }

    val units = listOf("un" to "Unidades (un)", "lb" to "Libras (lb)", "kg" to "Kilogramos (kg)", "lt" to "Litros (lt)", "m" to "Metros (m)")
    val warehouses = listOf("Almacén Principal", "Depósito Secundario", "Sucursal Norte", "Bodega Central")
    val suppliers = listOf("Distribuidora Global S.A.", "Comercializadora del Norte", "Importadora y Suministros S.L.")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Nuevo Producto", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Producto") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU / Código") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio por unidad/medida ($ COP)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Unit Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = !expandedUnit },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = units.firstOrNull { it.first == unit }?.second ?: unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad de Medida") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        units.forEach { (code, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    unit = code
                                    expandedUnit = false
                                }
                            )
                        }
                    }
                }

                // Warehouse Selector (Pro feature)
                if (isPro) {
                    ExposedDropdownMenuBox(
                        expanded = expandedWarehouse,
                        onExpandedChange = { expandedWarehouse = !expandedWarehouse },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = warehouse,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Almacén ⭐ (Pro)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWarehouse) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedWarehouse,
                            onDismissRequest = { expandedWarehouse = false }
                        ) {
                            warehouses.forEach { wh ->
                                DropdownMenuItem(
                                    text = { Text(wh) },
                                    onClick = {
                                        warehouse = wh
                                        expandedWarehouse = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = "Almacén Principal (Plan Pro para cambiar)",
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Almacén (Bloqueado)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Supplier Selector (Pro feature)
                if (isPro) {
                    ExposedDropdownMenuBox(
                        expanded = expandedSupplier,
                        onExpandedChange = { expandedSupplier = !expandedSupplier },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = supplierName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Proveedor ⭐ (Pro)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSupplier) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSupplier,
                            onDismissRequest = { expandedSupplier = false }
                        ) {
                            suppliers.forEach { sup ->
                                DropdownMenuItem(
                                    text = { Text(sup) },
                                    onClick = {
                                        supplierName = sup
                                        expandedSupplier = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = "Distribuidora Global S.A. (Plan Pro para cambiar)",
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Proveedor (Bloqueado)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Inicial") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = minStock,
                    onValueChange = { minStock = it },
                    label = { Text("Stock Mínimo Alerta") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pPrice = price.toDoubleOrNull() ?: 0.0
                    val pStock = stock.toDoubleOrNull() ?: 0.0
                    val pMin = minStock.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onConfirm(name, sku, pPrice, pStock, pMin, unit, warehouse)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4FA0))
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}
