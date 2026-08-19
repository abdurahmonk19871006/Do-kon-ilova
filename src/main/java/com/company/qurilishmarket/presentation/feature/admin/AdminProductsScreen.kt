package com.company.qurilishmarket.presentation.feature.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.presentation.common.EmptyState
import com.company.qurilishmarket.presentation.common.ErrorView
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.common.toSomText
import com.company.qurilishmarket.presentation.theme.ErrorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onAddNew: () -> Unit,
    viewModel: AdminProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mahsulotlar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNew) {
                Icon(Icons.Filled.Add, contentDescription = "Yangi mahsulot")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::load)
                is UiState.Empty -> EmptyState(
                    message = "Hali mahsulot yo'q",
                    actionLabel = "Qo'shish",
                    onAction = onAddNew
                )
                is UiState.Success -> LazyColumn {
                    items(state.data, key = { it.id }) { product ->
                        AdminProductRow(product = product, onClick = { onProductClick(product.id) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProductRow(product: Product, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(
                product.name,
                textDecoration = if (!product.isActive) TextDecoration.LineThrough else null
            )
        },
        supportingContent = {
            Text("${product.code} · ${product.price.toSomText()} · qoldiq: ${product.stock}")
        },
        trailingContent = {
            if (!product.isActive) {
                Text("O'chirilgan", color = ErrorRed)
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
