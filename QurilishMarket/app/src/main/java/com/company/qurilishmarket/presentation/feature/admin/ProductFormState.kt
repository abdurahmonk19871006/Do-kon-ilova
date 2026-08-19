package com.company.qurilishmarket.presentation.feature.admin

import com.company.qurilishmarket.domain.model.MeasureUnit
import com.company.qurilishmarket.domain.model.Product

/**
 * Matn maydonlari string sifatida ushlanadi (TextField shunday ishlaydi), saqlashda
 * raqamga aylantiriladi. Rasm — hozircha vergul bilan ajratilgan URL ro'yxati: haqiqiy
 * yuklash (Supabase Storage'ga) UI'si keyingi bosqichda qo'shiladi (§10), hozircha admin
 * rasmni boshqa joyga (masalan, o'zining Storage bucket'iga qo'lda) joylab, havolasini
 * shu yerga qo'yadi.
 */
data class ProductFormState(
    val code: String = "",
    val name: String = "",
    val categoryId: String = "",
    val unit: MeasureUnit = MeasureUnit.DONA,
    val price: String = "",
    val oldPrice: String = "",
    val stock: String = "",
    val shortDescription: String = "",
    val fullDescription: String = "",
    val imagesCsv: String = "",
    val isPopular: Boolean = false,
    val isNew: Boolean = false,
    val isActive: Boolean = true
) {
    val isValid: Boolean
        get() = code.isNotBlank() && name.isNotBlank() && categoryId.isNotBlank() &&
            price.toLongOrNull() != null && stock.toIntOrNull() != null

    fun toProduct(id: String): Product = Product(
        id = id,
        code = code.trim(),
        name = name.trim(),
        categoryId = categoryId,
        unit = unit,
        price = price.toLongOrNull() ?: 0L,
        oldPrice = oldPrice.toLongOrNull(),
        stock = stock.toIntOrNull() ?: 0,
        shortDescription = shortDescription.trim(),
        fullDescription = fullDescription.trim(),
        images = imagesCsv.split(",").map { it.trim() }.filter { it.isNotBlank() },
        isPopular = isPopular,
        isNew = isNew,
        isActive = isActive
    )

    companion object {
        fun fromProduct(product: Product): ProductFormState = ProductFormState(
            code = product.code,
            name = product.name,
            categoryId = product.categoryId,
            unit = product.unit,
            price = product.price.toString(),
            oldPrice = product.oldPrice?.toString() ?: "",
            stock = product.stock.toString(),
            shortDescription = product.shortDescription,
            fullDescription = product.fullDescription,
            imagesCsv = product.images.joinToString(", "),
            isPopular = product.isPopular,
            isNew = product.isNew,
            isActive = product.isActive
        )
    }
}
