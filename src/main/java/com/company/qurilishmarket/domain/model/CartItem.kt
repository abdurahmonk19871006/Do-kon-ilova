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
    val quantity: Int,
    val unit: MeasureUnit,
    val imageUrl: String? = null
) {
    val lineTotal: Long get() = price * quantity
}
