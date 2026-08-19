package com.company.qurilishmarket.di

import android.content.Context
import androidx.room.Room
import com.company.qurilishmarket.data.local.AppDatabase
import com.company.qurilishmarket.data.local.dao.CartDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "qurilishmarket.db").build()

    @Provides
    fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()
}
