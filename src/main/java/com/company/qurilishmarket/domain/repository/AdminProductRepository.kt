package com.company.qurilishmarket.domain.repository

import com.company.qurilishmarket.domain.model.Product

/**
 * Yozish (CRUD) faqat shu interfeys orqali — o'qish uchun oddiy ProductRepository yetarli
 * (RLS'da o'qish hammaga ochiq, §6). Ajratilgani sabab: kim shu interfeysni chaqirayotganini
 * ko'rib, "bu yerda admin huquqi kerak" ekanini kod darajasida ham aniq qilib turish.
 */
interface AdminProductRepository {
    suspend fun getAllProducts(): Result<List<Product>>
    suspend fun createProduct(product: Product): Result<String>
    suspend fun updateProduct(product: Product): Result<Unit>

    /** Haqiqiy o'chirish emas — is_active=false (§5: eski buyurtmalar tarixi buzilmasin). */
    suspend fun deactivateProduct(productId: String): Result<Unit>
}
