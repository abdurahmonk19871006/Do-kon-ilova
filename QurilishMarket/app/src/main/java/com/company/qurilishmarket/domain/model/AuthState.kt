package com.company.qurilishmarket.domain.model

/**
 * Oddiy Boolean emas — chunki ilova ochilganda saqlangan sessiyani tekshirish o'zi ham
 * asinxron. Agar Boolean ishlatilsa, allaqachon login qilgan foydalanuvchi ham bir lahzaga
 * "chiqib ketgan" deb ko'rinib, Checkout uni bekorga Login'ga otib yuborishi mumkin edi.
 */
sealed interface AuthState {
    data object Loading : AuthState
    data object LoggedOut : AuthState
    data class LoggedIn(val userId: String) : AuthState
}
