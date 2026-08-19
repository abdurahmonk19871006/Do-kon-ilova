package com.company.qurilishmarket.data.repository

import com.company.qurilishmarket.data.mapper.toDomain
import com.company.qurilishmarket.data.remote.dto.OrderDto
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.model.OrderStatus
import com.company.qurilishmarket.domain.repository.AdminOrderRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order as SortDirection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class AdminOrderRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : AdminOrderRepository {

    override suspend fun getAllOrders(): Result<List<Order>> = runCatching {
        client.from("orders")
            .select(Columns.raw("*, order_items(*)")) {
                order("created_at", SortDirection.DESCENDING)
            }
            .decodeList<OrderDto>()
            .map { it.toDomain() }
    }

    @Serializable
    private data class UpdateStatusParams(
        @SerialName("p_order_id") val orderId: String,
        @SerialName("p_new_status") val newStatus: String
    )

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> = runCatching {
        client.postgrest.rpc(
            "update_order_status",
            UpdateStatusParams(orderId = orderId, newStatus = status.name)
        )
        Unit
    }
}
