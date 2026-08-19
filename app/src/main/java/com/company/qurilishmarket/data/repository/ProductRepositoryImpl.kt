package com.company.qurilishmarket.data.repository

import com.company.qurilishmarket.data.mapper.toDomain
import com.company.qurilishmarket.data.remote.dto.ProductDto
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.model.ProductSort
import com.company.qurilishmarket.domain.repository.ProductRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject

/**
 * DIQQAT: Postgrest-kt'ning aniq query DSL imzolari (order/limit parametr turlari va h.k.)
 * jadal rivojlanayotgan community kutubxonada tez-tez o'zgaradi — shuning uchun bu faylni
 * birinchi marta compile qilganda kichik moslashtirish kerak bo'lishi mumkin (Android Studio
 * autocomplete aniq metodlarni ko'rsatadi). Umumiy struktura — from/select/filter/decodeList —
 * barqaror qoladi.
 */
class ProductRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : ProductRepository {

    override suspend fun getPopularProducts(maxResults: Int): Result<List<Product>> = runCatching {
        client.from("products")
            .select {
                filter {
                    eq("is_popular", true)
                    eq("is_active", true)
                }
                order("created_at", Order.DESCENDING)
                limit(maxResults.toLong())
            }
            .decodeList<ProductDto>()
            .map { it.toDomain() }
    }

    override suspend fun getNewProducts(maxResults: Int): Result<List<Product>> = runCatching {
        client.from("products")
            .select {
                filter {
                    eq("is_new", true)
                    eq("is_active", true)
                }
                order("created_at", Order.DESCENDING)
                limit(maxResults.toLong())
            }
            .decodeList<ProductDto>()
            .map { it.toDomain() }
    }

    override suspend fun getDiscountedProducts(maxResults: Int): Result<List<Product>> = runCatching {
        client.from("products")
            .select {
                filter {
                    // has_discount — generated column (supabase/schema.sql §9), murakkab
                    // "old_price IS NOT NULL" filtridan qochish uchun
                    eq("has_discount", true)
                    eq("is_active", true)
                }
                order("created_at", Order.DESCENDING)
                limit(maxResults.toLong())
            }
            .decodeList<ProductDto>()
            .map { it.toDomain() }
    }

    override suspend fun getProductById(id: String): Result<Product> = runCatching {
        client.from("products")
            .select { filter { eq("id", id) } }
            .decodeSingle<ProductDto>()
            .toDomain()
    }

    override suspend fun getProductsByCategory(categoryId: String, sort: ProductSort): Result<List<Product>> = runCatching {
        client.from("products")
            .select {
                filter {
                    eq("category_id", categoryId)
                    eq("is_active", true)
                }
                when (sort) {
                    ProductSort.PRICE_ASC -> order("price", Order.ASCENDING)
                    ProductSort.PRICE_DESC -> order("price", Order.DESCENDING)
                    // Reyting maydoni hali yo'q — hozircha "mashhur" belgisi bo'yicha
                    ProductSort.POPULARITY -> order("is_popular", Order.DESCENDING)
                    ProductSort.NEWEST -> order("created_at", Order.DESCENDING)
                }
            }
            .decodeList<ProductDto>()
            .map { it.toDomain() }
    }

    override suspend fun searchProducts(query: String): Result<List<Product>> = runCatching {
        client.from("products")
            .select {
                filter {
                    eq("is_active", true)
                    or {
                        ilike("name", "%$query%")
                        ilike("code", "%$query%")
                    }
                }
            }
            .decodeList<ProductDto>()
            .map { it.toDomain() }
    }
}
