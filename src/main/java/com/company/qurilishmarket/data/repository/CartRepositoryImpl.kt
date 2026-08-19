package com.company.qurilishmarket.data.repository

import com.company.qurilishmarket.data.local.dao.CartDao
import com.company.qurilishmarket.data.local.entity.CartItemEntity
import com.company.qurilishmarket.domain.model.CartItem
import com.company.qurilishmarket.domain.model.MeasureUnit
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override fun observeCart(): Flow<List<CartItem>> =
        cartDao.observeCartItems().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addToCart(product: Product, quantity: Int) {
        val existing = cartDao.getCartItem(product.id)
        val newQuantity = (existing?.quantity ?: 0) + quantity
        cartDao.upsert(
            CartItemEntity(
                productId = product.id,
                name = product.name,
                price = product.price,
                quantity = newQuantity.coerceAtMost(product.stock),
                unit = product.unit.name,
                imageUrl = product.images.firstOrNull()
            )
        )
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.remove(productId)
            return
        }
        val existing = cartDao.getCartItem(productId) ?: return
        cartDao.upsert(existing.copy(quantity = quantity))
    }

    override suspend fun removeFromCart(productId: String) = cartDao.remove(productId)

    override suspend fun clearCart() = cartDao.clear()
}

private fun CartItemEntity.toDomain(): CartItem = CartItem(
    productId = productId,
    name = name,
    price = price,
    quantity = quantity,
    unit = runCatching { MeasureUnit.valueOf(unit) }.getOrDefault(MeasureUnit.DONA),
    imageUrl = imageUrl
)
