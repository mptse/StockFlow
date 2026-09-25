package com.mptse.stockflow.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mptse.stockflow.ui.components.SFLogo

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val deepPurple = Color(0xFF36204B)
    val cardBackground = Color(0xFFFFFFFF)
    val primaryButtonColor = Color(0xFF6B4FA0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepPurple)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SFLogo(size = 56)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "StockFlow",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Control de Inventario",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Seguridad cifrada de extremo a extremo.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Su correo de trabajo...") },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••••") },
                        label = { Text("Contraseña (Mín. 6 caracteres)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage!!, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || !email.contains("@")) {
                                errorMessage = "Por favor, introduce un correo electrónico válido."
                            } else if (password.length < 6) {
                                errorMessage = "La contraseña debe tener al menos 6 caracteres."
                            } else {
                                errorMessage = null
                                onLoginSuccess()
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryButtonColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Ingresar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "¿No tiene una cuenta? Regístrese gratis aquí.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val deepPurple = Color(0xFF36204B)
    val cardBackground = Color(0xFFFFFFFF)
    val primaryButtonColor = Color(0xFF6B4FA0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepPurple)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Registro Seguro",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Crea tu cuenta cifrada en StockFlow.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del Negocio / Usuario") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña (Mín. 6 caracteres)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage!!, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Por favor, introduce el nombre del negocio."
                            } else if (email.isBlank() || !email.contains("@")) {
                                errorMessage = "Por favor, introduce un correo electrónico válido."
                            } else if (password.length < 6) {
                                errorMessage = "La contraseña debe tener al menos 6 caracteres."
                            } else {
                                errorMessage = null
                                onRegisterSuccess()
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryButtonColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Continuar a Elegir Plan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackToLogin) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun PlanSelectionScreen(
    onSelectPlan: (Boolean) -> Unit
) {
    val deepPurple = Color(0xFF36204B)
    val primaryColor = Color(0xFF6B4FA0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepPurple)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Elige tu Plan en StockFlow",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selecciona el plan que mejor se adapte a tu negocio.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Plan Gratuito", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Text(text = "$0 / siempre", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Hasta 50 productos\n• 1 almacén\n• Control básico de stock", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { onSelectPlan(false) },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Elegir Plan Gratuito")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                border = BorderStroke(2.dp, primaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Plan Pro ⭐", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = primaryColor
                        ) {
                            Text(
                                text = "RECOMENDADO",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(text = "$9.99 / mes", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Productos ilimitados\n• -------------------\n• Múltiples almacenes y empleados\n• Reportes avanzados y soporte prioritario", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onSelectPlan(true) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Elegir Plan Pro")
                    }
                }
            }
        }
    }
}
