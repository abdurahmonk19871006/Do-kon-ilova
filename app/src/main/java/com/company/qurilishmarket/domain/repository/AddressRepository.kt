package com.company.qurilishmarket.domain.repository

import com.company.qurilishmarket.domain.model.Address

interface AddressRepository {
    suspend fun createAddress(title: String, fullAddress: String): Result<String>
    suspend fun getMyAddresses(): Result<List<Address>>
    suspend fun deleteAddress(addressId: String): Result<Unit>
}
