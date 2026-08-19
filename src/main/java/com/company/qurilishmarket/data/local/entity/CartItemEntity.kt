package com.company.qurilishmarket.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val price: Long,
    val oldPrice: Long?,
    val imageUrl: String?,
    val unit: String,
    val quantity: Int,
    val availableStock: Int
)
