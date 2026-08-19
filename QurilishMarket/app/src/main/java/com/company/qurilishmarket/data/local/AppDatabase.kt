package com.company.qurilishmarket.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.company.qurilishmarket.data.local.dao.CartDao
import com.company.qurilishmarket.data.local.entity.CartItemEntity

@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
