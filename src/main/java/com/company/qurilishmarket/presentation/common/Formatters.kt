package com.company.qurilishmarket.presentation.common

import com.company.qurilishmarket.domain.model.DeliveryType
import com.company.qurilishmarket.domain.model.MeasureUnit
import com.company.qurilishmarket.domain.model.PaymentMethod

/** 150000 → "150 000 so'm" — §4'dagi format (bo'sh joy bilan minglik ajratgich). */
fun Long.toSomText(): String {
    val grouped = this.toString().reversed().chunked(3).joinToString(" ").reversed()
    return "$grouped so'm"
}

/** §2'dagi o'lchov birliklari UI'da to'g'ri ko'rinishi uchun (masalan M2 -> "m²"). */
fun MeasureUnit.displayName(): String = when (this) {
    MeasureUnit.DONA -> "dona"
    MeasureUnit.KG -> "kg"
    MeasureUnit.METR -> "metr"
    MeasureUnit.LITR -> "litr"
    MeasureUnit.QOP -> "qop"
    MeasureUnit.QUTI -> "quti"
    MeasureUnit.RULON -> "rulon"
    MeasureUnit.M2 -> "m²"
    MeasureUnit.M3 -> "m³"
}

fun PaymentMethod.displayName(): String = when (this) {
    PaymentMethod.NAQD -> "Naqd (yetkazib berishda)"
    PaymentMethod.PAYME -> "Payme"
    PaymentMethod.CLICK -> "Click"
    PaymentMethod.KARTA -> "Bank kartasi"
}

fun DeliveryType.displayName(): String = when (this) {
    DeliveryType.YETKAZIB_BERISH -> "Yetkazib berish"
    DeliveryType.OLIB_KETISH -> "Do'kondan olib ketish"
}
