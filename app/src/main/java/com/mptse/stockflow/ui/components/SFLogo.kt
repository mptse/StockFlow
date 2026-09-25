package com.mptse.stockflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SFLogo(modifier: Modifier = Modifier, size: Int = 48) {
    val deepPurple = Color(0xFF36204B)
    val goldBorder = Color(0xFFD4AF37)

    Surface(
        modifier = modifier
            .size(size.dp)
            .border(1.5.dp, goldBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE8DEF8), Color(0xFFD0BCFF))
                    )
                )
        ) {
            Text(
                text = "SF",
                color = deepPurple,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = (size * 0.45).sp,
                letterSpacing = (-1).sp
            )
        }
    }
}
