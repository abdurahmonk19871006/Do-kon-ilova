package com.company.qurilishmarket.domain.repository

import com.company.qurilishmarket.domain.model.Product

interface FavoriteRepository {
    suspend fun getFavoriteProducts(): Result<List<Product>>
    suspend fun isFavorite(productId: String): Result<Boolean>
    suspend fun toggleFavorite(productId: String, isFavorite: Boolean): Result<Unit>
}
