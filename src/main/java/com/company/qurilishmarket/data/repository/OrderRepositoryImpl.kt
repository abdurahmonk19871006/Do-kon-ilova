package com.company.qurilishmarket.data.repository

import com.company.qurilishmarket.data.mapper.toDomain
import com.company.qurilishmarket.data.remote.dto.OrderDto
import com.company.qurilishmarket.data.remote.dto.OrderStatusDto
import com.company.qurilishmarket.domain.model.CartItem
import com.company.qurilishmarket.domain.model.DeliveryType
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.model.OrderStatus
import com.company.qurilishmarket.domain.model.PaymentMethod
import com.company.qurilishmarket.domain.repository.OrderRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order as SortDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : OrderRepository {

    @Serializable
    private data class CreateOrderParams(
        @SerialName("p_items") val items: List<OrderItemParam>,
        @SerialName("p_delivery_type") val deliveryType: String,
        @SerialName("p_address_id") val addressId: String?,
        @SerialName("p_payment_method") val paymentMethod: String,
        @SerialName("p_comment") val comment: String?,
        @SerialName("p_delivery_fee") val deliveryFee: Long
    )

    @Serializable
    private data class OrderItemParam(
        @SerialName("product_id") val productId: String,
        val quantity: Int
    )

    override suspend fun createOrder(
        items: List<CartItem>,
        deliveryType: DeliveryType,
        addressId: String?,
        paymentMethod: PaymentMethod,
        comment: String?,
        deliveryFee: Long
    ): Result<String> = runCatching {
        client.postgrest.rpc(
            "create_order",
            CreateOrderParams(
                items = items.map { OrderItemParam(productId = it.productId, quantity = it.quantity) },
                deliveryType = deliveryType.name,
                addressId = addressId,
                paymentMethod = paymentMethod.name,
                comment = comment,
                deliveryFee = deliveryFee
            )
        ).decodeAs<String>()
    }

    override suspend fun getMyOrders(): Result<List<Order>> = runCatching {
        client.from("orders")
            .select(Columns.raw("*, order_items(*)")) {
                order("created_at", SortDirection.DESCENDING)
            }
            .decodeList<OrderDto>()
            .map { it.toDomain() }
    }

    override suspend fun getOrderById(orderId: String): Result<Order> = runCatching {
        client.from("orders")
            .select(Columns.raw("*, order_items(*)")) {
                filter { eq("id", orderId) }
            }
            .decodeSingle<OrderDto>()
            .toDomain()
    }

    override fun observeOrderStatus(orderId: String): Flow<OrderStatus> =
        client.from("orders")
            .selectSingleValueAsFlow(primaryKey = OrderStatusDto::id) {
                eq("id", orderId)
            }
            .map { dto -> runCatching { OrderStatus.valueOf(dto.status) }.getOrDefault(OrderStatus.QABUL_QILINDI) }
}
