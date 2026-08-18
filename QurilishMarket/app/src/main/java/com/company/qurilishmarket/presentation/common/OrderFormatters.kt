package com.company.qurilishmarket.presentation.common

import androidx.compose.ui.graphics.Color
import com.company.qurilishmarket.domain.model.OrderStatus
import com.company.qurilishmarket.presentation.theme.ErrorRed
import com.company.qurilishmarket.presentation.theme.NavyPrimary
import com.company.qurilishmarket.presentation.theme.SuccessGreen
import com.company.qurilishmarket.presentation.theme.WarningAmber

fun OrderStatus.displayName(): String = when (this) {
    OrderStatus.QABUL_QILINDI -> "Qabul qilindi"
    OrderStatus.TAYYORLANMOQDA -> "Tayyorlanmoqda"
    OrderStatus.YETKAZILMOQDA -> "Yetkazilmoqda"
    OrderStatus.YETKAZILDI -> "Yetkazildi"
    OrderStatus.BEKOR_QILINDI -> "Bekor qilindi"
}

/** §4'dagi semantik ranglar: muvaffaqiyat yashil, bekor qilingan qizil, jarayondagi amber. */
fun OrderStatus.color(): Color = when (this) {
    OrderStatus.QABUL_QILINDI -> NavyPrimary
    OrderStatus.TAYYORLANMOQDA -> WarningAmber
    OrderStatus.YETKAZILMOQDA -> WarningAmber
    OrderStatus.YETKAZILDI -> SuccessGreen
    OrderStatus.BEKOR_QILINDI -> ErrorRed
}
