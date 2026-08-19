package com.company.qurilishmarket.presentation.feature.orderconfirmation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.company.qurilishmarket.presentation.theme.SuccessGreen
import com.company.qurilishmarket.presentation.theme.TextSecondary

@Composable
fun OrderConfirmationScreen(
    orderId: String,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Buyurtmangiz qabul qilindi!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        // To'liq UUID emas — birinchi 8 belgisi, mijoz uchun operator bilan gaplashganda
        // aytish oson bo'lgan qisqa ko'rinish
        Text(
            "Buyurtma raqami: ${orderId.take(8)}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBackToHome, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Bosh sahifaga qaytish")
        }
    }
}
