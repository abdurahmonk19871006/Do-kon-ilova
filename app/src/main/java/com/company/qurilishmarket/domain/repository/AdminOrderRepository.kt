package com.company.qurilishmarket.domain.repository

import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.model.OrderStatus

interface AdminOrderRepository {
    /** RLS orqali admin barcha buyurtmalarni ko'radi, oddiy foydalanuvchi — faqat o'zinikini (§6). */
    suspend fun getAllOrders(): Result<List<Order>>

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>
}
