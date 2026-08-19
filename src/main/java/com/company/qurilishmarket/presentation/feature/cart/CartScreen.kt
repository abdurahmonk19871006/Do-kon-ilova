package com.company.qurilishmarket.presentation.feature.cart

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.company.qurilishmarket.domain.model.CartItem
import com.company.qurilishmarket.presentation.common.EmptyState
import com.company.qurilishmarket.presentation.common.ErrorView
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.common.displayName
import com.company.qurilishmarket.presentation.common.toSomText
import com.company.qurilishmarket.presentation.theme.ErrorRed
import com.company.qurilishmarket.presentation.theme.NavyPrimary
import com.company.qurilishmarket.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onCheckout: () -> Unit,
    onBrowseProducts: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Savatcha") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorView(message = state.message, onRetry = {})
                is UiState.Empty -> EmptyState(
                    message = "Savatchangiz hali bo'sh",
                    actionLabel = "Xarid qilishni boshlash",
                    onAction = onBrowseProducts
                )
                is UiState.Success -> CartContent(
                    items = state.data,
                    onIncrease = viewModel::increaseQuantity,
                    onDecrease = viewModel::decreaseQuantity,
                    onRemove = viewModel::removeItem,
                    onCheckout = onCheckout
                )
            }
        }
    }
}

@Composable
private fun CartContent(
    items: List<CartItem>,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onRemove: (String) -> Unit,
    onCheckout: () -> Unit
) {
    val subtotal = items.sumOf { it.lineTotal }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.productId }) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = { onIncrease(item) },
                    onDecrease = { onDecrease(item) },
                    onRemove = { onRemove(item.productId) }
                )
            }
        }

        // §3: umumiy summa doim pastda ko'rinadi. Yetkazib berish narxi bu yerda hali
        // ko'rsatilmaydi — u yetkazib berish turi/manzil tanlangandan keyin, Checkout'da
        // aniqlanadi (§3'dagi asl talabdan ataylab og'ish: aks holda noaniq/soxta raqam
        // ko'rsatgan bo'lardik).
        Surface(shadowElevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Jami", style = MaterialTheme.typography.titleLarge)
                    Text(subtotal.toSomText(), style = MaterialTheme.typography.titleLarge, color = NavyPrimary)
                }
                Text(
                    "Yetkazib berish narxi keyingi bosqichda hisoblanadi",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onCheckout,
                    modifier = Modifier.fillMaxWidth().height(52.dp) // §4: min 52dp
                ) {
                    Text("Buyurtma berish")
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(shape = MaterialTheme.shapes.medium) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${item.price.toSomText()} / ${item.unit.displayName()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = NavyPrimary
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Remove, contentDescription = "Kamaytirish")
                    }
                    Text(item.quantity.toString(), style = MaterialTheme.typography.bodyLarge)
                    IconButton(
                        onClick = onIncrease,
                        enabled = item.quantity < item.availableStock,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Ko'paytirish")
                    }
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "O'chirish", tint = ErrorRed)
            }
        }
    }
}
