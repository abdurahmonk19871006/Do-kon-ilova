package com.company.qurilishmarket.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation: har bir ekran — oddiy @Serializable klass/object. Satr (string)
 * route'larni qo'lda yozish va argumentlarni qo'lda parse qilish shart emas — kompilyator
 * o'zi tekshiradi. §3'dagi screenlar ro'yxatiga mos.
 */
sealed interface Screen {

    @Serializable data object Home : Screen
    @Serializable data object Categories : Screen
    @Serializable data object Cart : Screen
    @Serializable data object Orders : Screen
    @Serializable data object Profile : Screen

    @Serializable data object Search : Screen
    @Serializable data object Login : Screen

    @Serializable data class CategoryProducts(val categoryId: String) : Screen
    @Serializable data class ProductDetail(val productId: String) : Screen

    @Serializable data object Checkout : Screen
    @Serializable data class OrderConfirmation(val orderId: String) : Screen
    @Serializable data class OrderDetail(val orderId: String) : Screen

    @Serializable data object Addresses : Screen
    @Serializable data object Favorites : Screen
    @Serializable data object Settings : Screen

    // §6/§11: yashirin kirish — bottom nav yoki asosiy menyuning hech birida ro'yxatga
    // olinmaydi, faqat Settings ekranidagi maxsus trigger shu route'larga navigate qiladi
    @Serializable data object AdminLogin : Screen
    @Serializable data object AdminDashboard : Screen
    @Serializable data object AdminProducts : Screen
    // productId=null -> yangi mahsulot, aks holda tahrirlash (§7)
    @Serializable data class AdminProductForm(val productId: String? = null) : Screen
    @Serializable data object AdminOrders : Screen
    @Serializable data class AdminOrderDetail(val orderId: String) : Screen
}

// Bottom nav'da ko'rinadigan 5 ta bo'lim — shu tartibda (§3)
val bottomNavScreens: List<Screen> = listOf(
    Screen.Home,
    Screen.Categories,
    Screen.Cart,
    Screen.Orders,
    Screen.Profile
)
