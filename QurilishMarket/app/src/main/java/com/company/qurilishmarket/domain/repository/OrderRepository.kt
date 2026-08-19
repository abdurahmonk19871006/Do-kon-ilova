package com.company.qurilishmarket.domain.repository

import com.company.qurilishmarket.domain.model.CartItem
import com.company.qurilishmarket.domain.model.DeliveryType
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.model.OrderStatus
import com.company.qurilishmarket.domain.model.PaymentMethod
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    /** @return muvaffaqiyatli bo'lsa yaratilgan buyurtma ID'si. */
    suspend fun createOrder(
        items: List<CartItem>,
        deliveryType: DeliveryType,
        addressId: String?,
        paymentMethod: PaymentMethod,
        comment: String?,
        deliveryFee: Long
    ): Result<String>

    /** Joriy foydalanuvchining buyurtmalari, eng yangisidan boshlab (§3). */
    suspend fun getMyOrders(): Result<List<Order>>

    suspend fun getOrderById(orderId: String): Result<Order>

    /** Realtime orqali — admin statusni o'zgartirganda, ekran qayta so'ramasdan yangilanadi (§6). */
    fun observeOrderStatus(orderId: String): Flow<OrderStatus>
}
