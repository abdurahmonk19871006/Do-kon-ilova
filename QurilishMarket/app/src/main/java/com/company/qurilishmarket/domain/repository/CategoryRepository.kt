package com.company.qurilishmarket.domain.repository

import com.company.qurilishmarket.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getCategoryById(id: String): Result<Category>
}
