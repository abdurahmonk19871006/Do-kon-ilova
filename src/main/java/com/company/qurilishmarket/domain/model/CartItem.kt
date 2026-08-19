package com.company.qurilishmarket.domain.model

/**
 * Savatcha ataylab **faqat qurilmada** (Room) saqlanadi, Supabase'da emas — §2'dagi
 * "ro'yxatdan o'tmasdan ko'rish va savatchaga solish" tamoyiliga mos: agar savatcha
 * serverga sinxron bo'lsa, birinchi mahsulotni qo'shishning o'zi login talab qilib qo'yardi.
 * Login faqat checkout bosqichida kerak bo'ladi (§2).
 */
data class CartItem(
    val productId: String,
    val name: String,
    val price: Long,          // qo'shilgan paytdagi snapshot
    val oldPrice: Long? = null,
    val imageUrl: String?,
    val unit: MeasureUnit,
    val quantity: Int,
    val availableStock: Int   // checkout'da server baribir qayta tekshiradi (§6) — bu faqat UI uchun
) {
    val lineTotal: Long get() = price * quantity
}
