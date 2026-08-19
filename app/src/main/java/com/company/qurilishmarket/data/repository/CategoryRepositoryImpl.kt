package com.company.qurilishmarket.data.repository

import com.company.qurilishmarket.data.mapper.toDomain
import com.company.qurilishmarket.data.remote.dto.CategoryDto
import com.company.qurilishmarket.domain.model.Category
import com.company.qurilishmarket.domain.repository.CategoryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> = runCatching {
        client.from("categories")
            .select { order("sort_order", Order.ASCENDING) }
            .decodeList<CategoryDto>()
            .map { it.toDomain() }
    }

    override suspend fun getCategoryById(id: String): Result<Category> = runCatching {
        client.from("categories")
            .select { filter { eq("id", id) } }
            .decodeSingle<CategoryDto>()
            .toDomain()
    }
}
