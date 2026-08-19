package com.company.qurilishmarket.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `products` jadvaliga aynan mos (supabase/schema.sql). Ustun nomlari snake_case bo'lgani
 * uchun @SerialName aniq ko'rsatilgan — kutilmagan avto-konvertatsiyaga suyanmaslik uchun.
 */
@Serializable
data class ProductDto(
    val id: String,
    val code: String,
    val name: String,
    @SerialName("category_id") val categoryId: String,
    val unit: String,
    val price: Long,
    @SerialName("old_price") val oldPrice: Long? = null,
    val stock: Int,
    @SerialName("short_description") val shortDescription: String = "",
    @SerialName("full_description") val fullDescription: String = "",
    val images: List<String> = emptyList(),
    @SerialName("is_popular") val isPopular: Boolean = false,
    @SerialName("is_new") val isNew: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String = "" // Postgrest ISO-8601 satr qaytaradi
)
