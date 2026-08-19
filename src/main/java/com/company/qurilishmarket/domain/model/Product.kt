package com.company.qurilishmarket.domain.model

data class Product(
    val id: String,
    val code: String,                 // mahsulot kodi, masalan "SEM-001"
    val name: String,
    val categoryId: String,
    val unit: MeasureUnit,
    val price: Long,                  // so'mda, butun son
    val oldPrice: Long? = null,       // chegirmagacha narx
    val stock: Int,
    val shortDescription: String,
    val fullDescription: String,
    val images: List<String> = emptyList(),
    val isPopular: Boolean = false,
    val isNew: Boolean = false,
    val isActive: Boolean = true,     // hard-delete o'rniga — buyurtma tarixi buzilmasligi uchun
    val createdAt: Long = 0L
) {
    val isInStock: Boolean get() = stock > 0
    val hasDiscount: Boolean get() = oldPrice != null && oldPrice > price

    /** Masalan 15 — UI'da "-15%" chegirma belgisi uchun (§4). */
    val discountPercent: Int?
        get() = if (hasDiscount) (((oldPrice!! - price) * 100) / oldPrice).toInt() else null
}

// Sotuvchi ekspertizasi asosida kengaytirilgan: sement "qop"da, plitka "m²"da sotiladi
enum class MeasureUnit { DONA, KG, METR, LITR, QOP, QUTI, RULON, M2, M3 }
