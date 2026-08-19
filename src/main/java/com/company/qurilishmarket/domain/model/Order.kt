package com.company.qurilishmarket.domain.model

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val deliveryType: DeliveryType,
    val address: Address? = null,
    val paymentMethod: PaymentMethod,
    val comment: String? = null,
    val subtotal: Long,
    val deliveryFee: Long,
    val total: Long,
    val createdAt: Long = 0L,
    val statusHistory: List<OrderStatusEvent> = emptyList()
)

data class OrderItem(
    val productId: String,
    val name: String,     // buyurtma vaqtidagi snapshot
    val price: Long,      // buyurtma vaqtidagi snapshot — narx keyin o'zgarsa ham tarix buzilmaydi
    val quantity: Int,
    val unit: MeasureUnit
) {
    val lineTotal: Long get() = price * quantity
}

data class OrderStatusEvent(
    val status: OrderStatus,
    val timestamp: Long
)

// BEKOR_QILINDI — so'ralgan 4 statusga qo'shimcha, real buyurtma tizimida shart (§5)
enum class OrderStatus { QABUL_QILINDI, TAYYORLANMOQDA, YETKAZILMOQDA, YETKAZILDI, BEKOR_QILINDI }
enum class DeliveryType { YETKAZIB_BERISH, OLIB_KETISH }
enum class PaymentMethod { NAQD, PAYME, CLICK, KARTA }
