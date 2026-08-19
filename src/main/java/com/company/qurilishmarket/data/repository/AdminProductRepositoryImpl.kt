package com.company.qurilishmarket.data.repository

import com.company.qurilishmarket.data.mapper.toDomain
import com.company.qurilishmarket.data.mapper.toWriteDto
import com.company.qurilishmarket.data.remote.dto.ActiveFlagUpdateDto
import com.company.qurilishmarket.data.remote.dto.ProductDto
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.repository.AdminProductRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order as SortDirection
import javax.inject.Inject

/**
 * DIQQAT: bu yerdagi yozish (insert/update) chaqiruvlari Postgrest'ning odatiy REST
 * semantikasiga amal qiladi — lekin `insert(...) { select() }` orqali yaratilgan qatorni
 * qaytarib olish naqshi docs versiyasiga qarab farq qilishi mumkin (Android Studio
 * autocomplete'ga qarang). Haqiqiy himoya baribir RLS'da (§6) — bu klass chaqirilganda ham,
 * agar joriy foydalanuvchi admin bo'lmasa, Supabase serverning o'zi rad etadi.
 */
class AdminProductRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : AdminProductRepository {

    private val table get() = client.from("products")

    override suspend fun getAllProducts(): Result<List<Product>> = runCatching {
        table.select { order("created_at", SortDirection.DESCENDING) }
            .decodeList<ProductDto>()
            .map { it.toDomain() }
    }

    override suspend fun createProduct(product: Product): Result<String> = runCatching {
        table.insert(product.toWriteDto()) { select() }
            .decodeSingle<ProductDto>()
            .id
    }

    override suspend fun updateProduct(product: Product): Result<Unit> = runCatching {
        table.update(product.toWriteDto()) { filter { eq("id", product.id) } }
        Unit
    }

    override suspend fun deactivateProduct(productId: String): Result<Unit> = runCatching {
        table.update(ActiveFlagUpdateDto(isActive = false)) { filter { eq("id", productId) } }
        Unit
    }
}
