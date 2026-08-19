package com.company.qurilishmarket.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.presentation.components.ProductCard

/**
 * ProductCard o'zi kenglikni majburlamaydi (modifier orqali chaqiruvchi belgilaydi) —
 * shu tufayli Home'da gorizontal qatorda ham (sobit kenglik), bu yerda grid ichida ham
 * (to'liq katak kengligi) bir xil komponent qayta ishlatiladi.
 */
@Composable
fun ProductGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
