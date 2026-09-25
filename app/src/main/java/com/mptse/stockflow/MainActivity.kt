package com.mptse.stockflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mptse.stockflow.ui.InventoryViewModel
import com.mptse.stockflow.ui.screens.InventoryScreen
import com.mptse.stockflow.ui.screens.LoginScreen
import com.mptse.stockflow.ui.screens.PlanSelectionScreen
import com.mptse.stockflow.ui.screens.RegisterScreen

enum class AuthState {
    LOGIN,
    REGISTER,
    PLAN_SELECTION,
    DASHBOARD
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: InventoryViewModel = viewModel()
            var authState by remember { mutableStateOf(AuthState.LOGIN) }
            val errorMessage by viewModel.errorMessage.collectAsState()

            when (authState) {
                AuthState.LOGIN -> {
                    LoginScreen(
                        onLogin = { email, pass -> viewModel.loginUser(email, pass) },
                        onLoginSuccess = { authState = AuthState.DASHBOARD },
                        onNavigateToRegister = { authState = AuthState.REGISTER }
                    )
                }
                AuthState.REGISTER -> {
                    RegisterScreen(
                        onRegister = { name, email, pass -> viewModel.registerUser(name, email, pass) },
                        onRegisterSuccess = { authState = AuthState.PLAN_SELECTION },
                        onBackToLogin = { authState = AuthState.LOGIN }
                    )
                }
                AuthState.PLAN_SELECTION -> {
                    PlanSelectionScreen(
                        onSelectPlan = { isPro ->
                            viewModel.setProPlan(isPro)
                            authState = AuthState.DASHBOARD
                        }
                    )
                }
                AuthState.DASHBOARD -> {
                    InventoryScreen(
                        viewModel = viewModel,
                        onLogout = { authState = AuthState.LOGIN }
                    )
                }
            }

            if (errorMessage != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.clearError() },
                    title = { Text("Aviso de Sistema") },
                    text = { Text(errorMessage!!) },
                    confirmButton = {
                        Button(onClick = { viewModel.clearError() }) {
                            Text("Entendido")
                        }
                    }
                )
            }
        }
    }
}
