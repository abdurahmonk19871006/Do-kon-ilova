package com.company.qurilishmarket.domain.model

data class Address(
    val id: String,
    val title: String,          // "Uy", "Ish" kabi
    val fullAddress: String,
    val lat: Double? = null,
    val lng: Double? = null,
    val isDefault: Boolean = false
)
