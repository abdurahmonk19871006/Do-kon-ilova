package com.company.qurilishmarket.presentation.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.Category
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.usecase.HomeData
import com.company.qurilishmarket.presentation.common.EmptyState
import com.company.qurilishmarket.presentation.common.ErrorView
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.UiState
import com.company.qurilishmarket.presentation.components.ProductCard
import com.company.qurilishmarket.presentation.theme.BackgroundSoft
import com.company.qurilishmarket.presentation.theme.NavyPrimary

@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::loadHomeData)
        is UiState.Empty -> EmptyState(message = "Hozircha mahsulot yo'q")
        is UiState.Success -> HomeContent(
            data = state.data,
            onProductClick = onProductClick,
            onCategoryClick = onCategoryClick,
            onSearchClick = onSearchClick
        )
    }
}

@Composable
private fun HomeContent(
    data: HomeData,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { SearchBarStub(onClick = onSearchClick) }
        item { BannerPlaceholder() }

        if (data.categories.isNotEmpty()) {
            item { SectionHeader(title = "Kategoriyalar", onSeeAll = null) }
            item { CategoryRow(categories = data.categories, onCategoryClick = onCategoryClick) }
        }
        if (data.popularProducts.isNotEmpty()) {
            item { SectionHeader(title = "Mashhur mahsulotlar", onSeeAll = {}) }
            item { ProductRow(products = data.popularProducts, onProductClick = onProductClick) }
        }
        if (data.discountedProducts.isNotEmpty()) {
            item { SectionHeader(title = "Chegirmadagi mahsulotlar", onSeeAll = {}) }
            item { ProductRow(products = data.discountedProducts, onProductClick = onProductClick) }
        }
        if (data.newProducts.isNotEmpty()) {
            item { SectionHeader(title = "Yangi mahsulotlar", onSeeAll = {}) }
            item { ProductRow(products = data.newProducts, onProductClick = onProductClick) }
        }
    }
}

@Composable
private fun SearchBarStub(onClick: () -> Unit) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        readOnly = true,
        enabled = false, // haqiqiy matn kiritish Qidiruv ekranida (§3) — bu yerda faqat trigger
        placeholder = { Text("Mahsulot yoki kod bo'yicha qidirish") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        colors = OutlinedTextFieldDefaults.colors(disabledContainerColor = BackgroundSoft),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun BannerPlaceholder() {
    // TODO: haqiqiy aksiya banneri — keyingi bosqichda (admin panelidan boshqariladigan
    // banner tizimi §10'ga qo'shiladigan kandidat)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(120.dp)
            .background(NavyPrimary, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Aksiya banneri", color = androidx.compose.ui.graphics.Color.White)
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (onSeeAll != null) {
            Text(
                "Barchasini ko'rish",
                style = MaterialTheme.typography.bodyMedium,
                color = NavyPrimary,
                modifier = Modifier.clickable(onClick = onSeeAll)
            )
        }
    }
}

@Composable
private fun CategoryRow(categories: List<Category>, onCategoryClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { onCategoryClick(category.id) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(BackgroundSoft, RoundedCornerShape(16.dp))
                )
                Text(
                    category.name,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ProductRow(products: List<Product>, onProductClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
                modifier = Modifier.width(160.dp)
            )
        }
    }
}
