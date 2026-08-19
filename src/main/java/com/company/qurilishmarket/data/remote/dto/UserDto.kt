package com.company.qurilishmarket.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val phone: String,
    @SerialName("full_name") val fullName: String = "",
    @SerialName("is_admin") val isAdmin: Boolean = false
)
