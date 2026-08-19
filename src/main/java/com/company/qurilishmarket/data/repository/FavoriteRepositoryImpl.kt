package com.company.qurilishmarket.data.repository

import com.company.qurilishmarket.data.mapper.toDomain
import com.company.qurilishmarket.data.remote.dto.ProductDto
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.repository.FavoriteRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : FavoriteRepository {

    @Serializable
    private data class FavoriteWithProductDto(val products: ProductDto)

    @Serializable
    private data class FavoriteInsertDto(
        @SerialName("user_id") val userId: String,
        @SerialName("product_id") val productId: String
    )

    @Serializable
    private data class FavoriteIdDto(@SerialName("product_id") val productId: String)

    override suspend fun getFavoriteProducts(): Result<List<Product>> = runCatching {
        client.from("favorites")
            .select(Columns.raw("products(*)"))
            .decodeList<FavoriteWithProductDto>()
            .map { it.products.toDomain() }
    }

    override suspend fun isFavorite(productId: String): Result<Boolean> = runCatching {
        val userId = client.auth.currentUserOrNull()?.id ?: return@runCatching false
        client.from("favorites")
            .select(Columns.list("product_id")) {
                filter {
                    eq("user_id", userId)
                    eq("product_id", productId)
                }
            }
            .decodeList<FavoriteIdDto>()
            .isNotEmpty()
    }

    override suspend fun toggleFavorite(productId: String, isFavorite: Boolean): Result<Unit> = runCatching {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Sevimlilarga qo'shish uchun avval tizimga kirish kerak")

        if (isFavorite) {
            client.from("favorites").insert(FavoriteInsertDto(userId = userId, productId = productId))
        } else {
            client.from("favorites").delete {
                filter {
                    eq("user_id", userId)
                    eq("product_id", productId)
                }
            }
        }
        Unit
    }
}
