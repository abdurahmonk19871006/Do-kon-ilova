package com.company.qurilishmarket.presentation.feature.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.presentation.common.EmptyState
import com.company.qurilishmarket.presentation.common.ErrorView
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.common.color
import com.company.qurilishmarket.presentation.common.displayName
import com.company.qurilishmarket.presentation.common.toSomText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onOrderClick: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn) viewModel.load()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Buyurtmalarim") }) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val auth = authState) {
                AuthState.Loading -> LoadingIndicator()
                AuthState.LoggedOut -> EmptyState(
                    message = "Buyurtmalaringizni ko'rish uchun tizimga kiring",
                    actionLabel = "Kirish",
                    onAction = onNavigateToLogin
                )
                is AuthState.LoggedIn -> when (val state = uiState) {
                    is UiState.Loading -> LoadingIndicator()
                    is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::load)
                    is UiState.Empty -> EmptyState(message = "Hali buyurtmalaringiz yo'q")
                    is UiState.Success -> OrdersList(orders = state.data, onOrderClick = onOrderClick)
                }
            }
        }
    }
}

@Composable
private fun OrdersList(orders: List<Order>, onOrderClick: (String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            Card(onClick = { onOrderClick(order.id) }, shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Buyurtma #${order.id.take(8)}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            order.status.displayName(),
                            color = order.status.color(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${order.items.size} mahsulot", style = MaterialTheme.typography.bodyMedium)
                        Text(order.total.toSomText(), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}
