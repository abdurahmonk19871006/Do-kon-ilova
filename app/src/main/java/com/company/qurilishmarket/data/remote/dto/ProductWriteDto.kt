package com.company.qurilishmarket.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * O'qish uchun ProductDto'dan alohida: yozishda `id`/`created_at` yubormaymiz — ular
 * serverda avtomatik generatsiya qilinadi (supabase/schema.sql, §7). Xuddi shu shakl
 * create'da ham, update'da ham ishlatiladi.
 */
@Serializable
data class ProductWriteDto(
    val code: String,
    val name: String,
    @SerialName("category_id") val categoryId: String,
    val unit: String,
    val price: Long,
    @SerialName("old_price") val oldPrice: Long? = null,
    val stock: Int,
    @SerialName("short_description") val shortDescription: String,
    @SerialName("full_description") val fullDescription: String,
    val images: List<String> = emptyList(),
    @SerialName("is_popular") val isPopular: Boolean = false,
    @SerialName("is_new") val isNew: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class ActiveFlagUpdateDto(@SerialName("is_active") val isActive: Boolean)
