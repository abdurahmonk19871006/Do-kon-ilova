package com.company.qurilishmarket.data.mapper

import com.company.qurilishmarket.data.remote.dto.CategoryDto
import com.company.qurilishmarket.data.remote.dto.OrderDto
import com.company.qurilishmarket.data.remote.dto.OrderItemDto
import com.company.qurilishmarket.data.remote.dto.ProductDto
import com.company.qurilishmarket.data.remote.dto.ProductWriteDto
import com.company.qurilishmarket.domain.model.Category
import com.company.qurilishmarket.domain.model.DeliveryType
import com.company.qurilishmarket.domain.model.MeasureUnit
import com.company.qurilishmarket.domain.model.Order
import com.company.qurilishmarket.domain.model.OrderItem
import com.company.qurilishmarket.domain.model.OrderStatus
import com.company.qurilishmarket.domain.model.PaymentMethod
import com.company.qurilishmarket.domain.model.Product
import java.time.OffsetDateTime

fun ProductDto.toDomain(): Product = Product(
    id = id,
    code = code,
    name = name,
    categoryId = categoryId,
    // Bazada noto'g'ri/eski qiymat bo'lib qolsa ham ilova qulamasligi uchun himoyalangan parse
    unit = runCatching { MeasureUnit.valueOf(unit) }.getOrDefault(MeasureUnit.DONA),
    price = price,
    oldPrice = oldPrice,
    stock = stock,
    shortDescription = shortDescription,
    fullDescription = fullDescription,
    images = images,
    isPopular = isPopular,
    isNew = isNew,
    isActive = isActive,
    // OffsetDateTime, Instant.parse()'dan farqli — Postgres qaytaradigan "+00:00" kabi aniq
    // offsetli formatni ham to'g'ri o'qiydi (Instant.parse faqat "Z" bilan ishlaydi va bu
    // yerda deyarli har doim xato berib, sukut bo'yicha 0L'ga tushib qolar edi)
    createdAt = runCatching { OffsetDateTime.parse(createdAt).toInstant().toEpochMilli() }.getOrDefault(0L)
)

fun CategoryDto.toDomain(): Category = Category(
    id = id,
    name = name,
    iconUrl = iconUrl,
    parentId = parentId,
    order = sortOrder
)

fun OrderDto.toDomain(): Order = Order(
    id = id,
    userId = userId,
    items = items.map { it.toDomain() },
    status = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.QABUL_QILINDI),
    deliveryType = runCatching { DeliveryType.valueOf(deliveryType) }.getOrDefault(DeliveryType.YETKAZIB_BERISH),
    address = null, // hozircha ro'yxat/tafsilotda alohida so'ralmaydi — kerak bo'lsa keyin qo'shiladi (§10)
    paymentMethod = runCatching { PaymentMethod.valueOf(paymentMethod) }.getOrDefault(PaymentMethod.NAQD),
    comment = comment,
    subtotal = subtotal,
    deliveryFee = deliveryFee,
    total = total,
    createdAt = runCatching { OffsetDateTime.parse(createdAt).toInstant().toEpochMilli() }.getOrDefault(0L),
    statusHistory = emptyList() // order_status_history jadvalidan — kerak bo'lsa keyin qo'shiladi (§10)
)

fun OrderItemDto.toDomain(): OrderItem = OrderItem(
    productId = productId,
    name = name,
    price = price,
    quantity = quantity,
    unit = runCatching { MeasureUnit.valueOf(unit) }.getOrDefault(MeasureUnit.DONA)
)

/** Admin formadan Postgrest'ga yozish uchun — §7 (admin CRUD). */
fun Product.toWriteDto(): ProductWriteDto = ProductWriteDto(
    code = code,
    name = name,
    categoryId = categoryId,
    unit = unit.name,
    price = price,
    oldPrice = oldPrice,
    stock = stock,
    shortDescription = shortDescription,
    fullDescription = fullDescription,
    images = images,
    isPopular = isPopular,
    isNew = isNew,
    isActive = isActive
)
