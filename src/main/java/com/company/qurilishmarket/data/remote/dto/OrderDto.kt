package com.company.qurilishmarket.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val status: String,
    @SerialName("delivery_type") val deliveryType: String,
    @SerialName("payment_method") val paymentMethod: String,
    val comment: String? = null,
    val subtotal: Long,
    @SerialName("delivery_fee") val deliveryFee: Long,
    val total: Long,
    @SerialName("created_at") val createdAt: String,
    // Postgrest'ning "resource embedding"i orqali keladi — select("*, order_items(*)")
    // qilinganda shu maydon avtomatik to'ladi, alohida so'rov kerak emas (§6).
    @SerialName("order_items") val items: List<OrderItemDto> = emptyList()
)

@Serializable
data class OrderItemDto(
    @SerialName("product_id") val productId: String,
    val name: String,
    val price: Long,
    val quantity: Int,
    val unit: String
)

/** `observeOrderStatus` faqat status kerak bo'lganda butun buyurtmani qayta o'qimasin deb. */
@Serializable
data class OrderStatusDto(
    val id: String,
    val status: String
)
