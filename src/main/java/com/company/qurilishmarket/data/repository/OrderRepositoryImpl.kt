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
// DIQQAT: bu "Order" — Postgrest'ning saralash enum'i (ASCENDING/DESCENDING), domain
// qatlamidagi Order (buyurtma) modeli bilan bir xil nom to'qnashmasin deb SortDirection deb
// alias qildim.
import io.github.jan.supabase.postgrest.query.Order as SortDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

/**
 * Bu yerda mijoz o'zi summa hisoblab yubormaydi — faqat productId+quantity ro'yxatini
 * yuboradi. Narx, stock tekshiruvi va yakuniy summa har doim serverda, `create_order()`
 * Postgres funksiyasi ichida hisoblanadi (supabase/schema.sql, §6) — aks holda kimdir
 * ilovani o'zgartirib, narxni pasaytirib yuborishi mumkin edi.
 *
 * DIQQAT: `observeOrderStatus`dagi `selectSingleValueAsFlow` — Realtime + Postgrest
 * modulini birgalikda ishlatadigan qulaylik metodi. Qidiruv paytida shu metodning eng
 * so'nggi (2026-yil) docs'da eng barqaror variant ekanini aniqladim, lekin xuddi shu
 * natijani beruvchi muqobil yo'llar ham bor (`channel.postgresSingleDataFlow`,
 * `channel.postgresChangeFlow` — quyi darajadagi API). Agar compile qilishda bu metod
 * topilmasa, Android Studio autocomplete'da "Flow" bilan tugaydigan muqobillarni ko'rib
 * chiqing (https://supabase.com/docs/reference/kotlin/subscribe).
 */
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
        // RLS o'zi joriy foydalanuvchining buyurtmalari bilan cheklaydi (§6) — bu yerda
        // qo'shimcha user_id filtri shart emas.
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
