package com.company.qurilishmarket.domain.repository

import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.model.ProductSort

/**
 * Domain qatlamidagi interfeys — Supabase, Room yoki boshqa manbadan bexabar.
 * Implementatsiyasi data/repository/ProductRepositoryImpl.kt'da (§1).
 */
interface ProductRepository {
    suspend fun getPopularProducts(maxResults: Int = 10): Result<List<Product>>
    suspend fun getNewProducts(maxResults: Int = 10): Result<List<Product>>
    suspend fun getDiscountedProducts(maxResults: Int = 10): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product>
    suspend fun getProductsByCategory(categoryId: String, sort: ProductSort = ProductSort.NEWEST): Result<List<Product>>
    suspend fun searchProducts(query: String): Result<List<Product>>
}
