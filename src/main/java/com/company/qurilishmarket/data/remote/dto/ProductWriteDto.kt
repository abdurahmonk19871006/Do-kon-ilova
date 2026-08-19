package com.company.qurilishmarket.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Admin formadan Postgrest'ga yozish uchun. ID avtomatik generatsiya qilinadi.
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
    @SerialName("short_description") val shortDescription: String = "",
    @SerialName("full_description") val fullDescription: String = "",
    val images: List<String> = emptyList(),
    @SerialName("is_popular") val isPopular: Boolean = false,
    @SerialName("is_new") val isNew: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true
)
