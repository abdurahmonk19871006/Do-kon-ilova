package com.company.qurilishmarket.presentation.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.model.OrderStatus
import com.company.qurilishmarket.presentation.common.EmptyState
import com.company.qurilishmarket.presentation.common.ErrorView
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.SummaryRow
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.common.color
import com.company.qurilishmarket.presentation.common.displayName
import com.company.qurilishmarket.presentation.common.toSomText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    onBack: () -> Unit,
    viewModel: AdminOrderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isUpdating by viewModel.isUpdating.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buyurtma (admin)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::load)
                is UiState.Empty -> EmptyState(message = "Buyurtma topilmadi")
                is UiState.Success -> AdminOrderDetailContent(
                    order = state.data,
                    isUpdating = isUpdating,
                    error = error,
                    onStatusChange = viewModel::setStatus
                )
            }
        }
    }
}

@Composable
private fun AdminOrderDetailContent(
    order: Order,
    isUpdating: Boolean,
    error: String?,
    onStatusChange: (OrderStatus) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Buyurtma #${order.id.take(8)}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            "Hozirgi holat: ${order.status.displayName()}",
            color = order.status.color(),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(20.dp))
        Text("Statusni o'zgartirish", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 1.dp) {
            Column {
                OrderStatus.entries.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = order.status == status,
                            onClick = { if (!isUpdating) onStatusChange(status) },
                            enabled = !isUpdating
                        )
                        Text(status.displayName(), color = status.color())
                    }
                }
            }
        }
        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))
        Text("Mahsulotlar", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        order.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${item.name} × ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(item.lineTotal.toSomText(), style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))
        SummaryRow("Mahsulotlar", order.subtotal.toSomText())
        Spacer(Modifier.height(4.dp))
        SummaryRow("Yetkazib berish", if (order.deliveryFee == 0L) "Bepul" else order.deliveryFee.toSomText())
        Spacer(Modifier.height(8.dp))
        SummaryRow("Jami", order.total.toSomText(), emphasized = true)

        Spacer(Modifier.height(24.dp))
        Text("Yetkazib berish turi", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(order.deliveryType.displayName(), style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))
        Text("To'lov usuli", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(order.paymentMethod.displayName(), style = MaterialTheme.typography.bodyMedium)

        order.comment?.takeIf { it.isNotBlank() }?.let { comment ->
            Spacer(Modifier.height(16.dp))
            Text("Mijoz izohi", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(comment, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(24.dp))
    }
}
