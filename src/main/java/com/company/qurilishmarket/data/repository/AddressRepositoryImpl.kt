package com.company.qurilishmarket.data.repository

import com.company.qurilishmarket.domain.model.Address
import com.company.qurilishmarket.domain.repository.AddressRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : AddressRepository {

    @Serializable
    private data class NewAddress(
        @SerialName("user_id") val userId: String,
        val title: String,
        @SerialName("full_address") val fullAddress: String
    )

    @Serializable
    private data class AddressDto(
        val id: String,
        val title: String,
        @SerialName("full_address") val fullAddress: String,
        val lat: Double? = null,
        val lng: Double? = null,
        @SerialName("is_default") val isDefault: Boolean = false
    )

    override suspend fun createAddress(title: String, fullAddress: String): Result<String> = runCatching {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("Manzil qo'shish uchun avval tizimga kirish kerak")

        client.from("addresses")
            .insert(NewAddress(userId = userId, title = title, fullAddress = fullAddress)) {
                select()
            }
            .decodeSingle<AddressDto>()
            .id
    }

    override suspend fun getMyAddresses(): Result<List<Address>> = runCatching {
        client.from("addresses")
            .select()
            .decodeList<AddressDto>()
            .map { it.toDomain() }
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> = runCatching {
        client.from("addresses").delete { filter { eq("id", addressId) } }
        Unit
    }

    private fun AddressDto.toDomain(): Address = Address(
        id = id,
        title = title,
        fullAddress = fullAddress,
        lat = lat,
        lng = lng,
        isDefault = isDefault
    )
}
