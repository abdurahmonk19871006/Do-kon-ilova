package com.company.qurilishmarket.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val street: String,
    val city: String,
    val region: String?,
    val zipcode: String?,
    @SerialName("is_default") val isDefault: Boolean = false
)
