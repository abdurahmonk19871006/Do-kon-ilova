package com.company.qurilishmarket.presentation.feature.categoryproducts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.ProductSort
import com.company.qurilishmarket.presentation.common.EmptyState
import com.company.qurilishmarket.presentation.common.ErrorView
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.ProductGrid
import com.company.qurilishmarket.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: CategoryProductsViewModel = hiltViewModel()
) {
    val categoryName by viewModel.categoryName.collectAsStateWithLifecycle()
    val sort by viewModel.sort.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName.ifBlank { "Kategoriya" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            SortRow(current = sort, onSortSelected = viewModel::setSort)

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is UiState.Loading -> LoadingIndicator()
                    is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::load)
                    is UiState.Empty -> EmptyState(message = "Bu kategoriyada hozircha mahsulot yo'q")
                    is UiState.Success -> ProductGrid(products = state.data, onProductClick = onProductClick)
                }
            }
        }
    }
}

@Composable
private fun SortRow(current: ProductSort, onSortSelected: (ProductSort) -> Unit) {
    // §3'dagi saralash talabi: arzon/qimmat/mashhur/yangi
    val options = listOf(
        ProductSort.NEWEST to "Yangi",
        ProductSort.PRICE_ASC to "Arzon",
        ProductSort.PRICE_DESC to "Qimmat",
        ProductSort.POPULARITY to "Mashhur"
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options, key = { it.first.name }) { (sort, label) ->
            FilterChip(selected = current == sort, onClick = { onSortSelected(sort) }, label = { Text(label) })
        }
    }
}
