package com.company.qurilishmarket.domain.model

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val isAdmin: Boolean = false   // faqat ma'lumot uchun — haqiqiy tekshiruv doim serverda,
                                    // profiles.is_admin + RLS orqali (§6, §11)
)
