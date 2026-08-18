package com.company.qurilishmarket.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.company.qurilishmarket.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    // Flow — Cart ekrani real vaqtda o'zgarishni ko'rishi uchun (miqdor tugmasi bosilganda
    // qayta so'rov yubormasdan, Room o'zi push qiladi)
    @Query("SELECT * FROM cart_items ORDER BY productId")
    fun observeCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItem(productId: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun remove(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clear()
}
