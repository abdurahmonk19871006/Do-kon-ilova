package com.company.qurilishmarket.presentation.feature.productdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.presentation.common.EmptyState
import com.company.qurilishmarket.presentation.common.ErrorView
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.common.displayName
import com.company.qurilishmarket.presentation.common.toSomText
import com.company.qurilishmarket.presentation.theme.ErrorRed
import com.company.qurilishmarket.presentation.theme.NavyPrimary
import com.company.qurilishmarket.presentation.theme.SuccessGreen
import com.company.qurilishmarket.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProductDetailEvent.AddedToCart -> snackbarHostState.showSnackbar("Savatchaga qo'shildi")
                is ProductDetailEvent.FavoriteError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mahsulot") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Sevimlilardan olib tashlash" else "Sevimlilarga qo'shish",
                            tint = if (isFavorite) ErrorRed else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::load)
                is UiState.Empty -> EmptyState(message = "Mahsulot topilmadi")
                is UiState.Success -> ProductDetailContent(
                    product = state.data,
                    quantity = quantity,
                    onIncrease = viewModel::increaseQuantity,
                    onDecrease = viewModel::decreaseQuantity,
                    onAddToCart = viewModel::addToCart
                )
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onAddToCart: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = product.name,
                modifier = Modifier.fillMaxWidth().height(280.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(product.name, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(4.dp))
                Text("Kod: ${product.code}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)

                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${product.price.toSomText()} / ${product.unit.displayName()}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NavyPrimary
                    )
                    if (product.hasDiscount) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            product.oldPrice!!.toSomText(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    if (product.isInStock) "Omborda: ${product.stock} ${product.unit.displayName()}" else "Mavjud emas",
                    color = if (product.isInStock) SuccessGreen else ErrorRed,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(16.dp))
                Text("Tavsif", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    product.fullDescription.ifBlank { product.shortDescription },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Pastki panel doim ko'rinadi — miqdor + "Savatchaga qo'shish" (§3, §4)
        Surface(shadowElevation = 8.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuantityStepper(quantity = quantity, onIncrease = onIncrease, onDecrease = onDecrease)
                Button(
                    onClick = onAddToCart,
                    enabled = product.isInStock,
                    modifier = Modifier.weight(1f).height(52.dp) // §4: min 52dp tugma balandligi
                ) {
                    Text("Savatchaga qo'shish")
                }
            }
        }
    }
}

@Composable
private fun QuantityStepper(quantity: Int, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onDecrease) {
            Icon(Icons.Filled.Remove, contentDescription = "Kamaytirish")
        }
        Text(quantity.toString(), style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = onIncrease) {
            Icon(Icons.Filled.Add, contentDescription = "Ko'paytirish")
        }
    }
}
