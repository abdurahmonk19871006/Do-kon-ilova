package com.company.qurilishmarket.di

import com.company.qurilishmarket.data.repository.AddressRepositoryImpl
import com.company.qurilishmarket.data.repository.AdminOrderRepositoryImpl
import com.company.qurilishmarket.data.repository.AdminProductRepositoryImpl
import com.company.qurilishmarket.data.repository.AuthRepositoryImpl
import com.company.qurilishmarket.data.repository.CartRepositoryImpl
import com.company.qurilishmarket.data.repository.CategoryRepositoryImpl
import com.company.qurilishmarket.data.repository.FavoriteRepositoryImpl
import com.company.qurilishmarket.data.repository.OrderRepositoryImpl
import com.company.qurilishmarket.data.repository.ProductRepositoryImpl
import com.company.qurilishmarket.domain.repository.AddressRepository
import com.company.qurilishmarket.domain.repository.AdminOrderRepository
import com.company.qurilishmarket.domain.repository.AdminProductRepository
import com.company.qurilishmarket.domain.repository.AuthRepository
import com.company.qurilishmarket.domain.repository.CartRepository
import com.company.qurilishmarket.domain.repository.CategoryRepository
import com.company.qurilishmarket.domain.repository.FavoriteRepository
import com.company.qurilishmarket.domain.repository.OrderRepository
import com.company.qurilishmarket.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * ViewModel/UseCase doim interfeys (domain/repository/) bilan ishlaydi — qaysi implementatsiya
 * ulanganini shu modul hal qiladi. Ertaga Supabase o'rniga boshqa narsaga o'tilsa, faqat shu
 * fayl (va data/repository/*Impl.kt) o'zgaradi, ViewModel'lar tegilmaydi (§1'dagi asosiy foyda).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(impl: AddressRepositoryImpl): AddressRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindAdminProductRepository(impl: AdminProductRepositoryImpl): AdminProductRepository

    @Binds
    @Singleton
    abstract fun bindAdminOrderRepository(impl: AdminOrderRepositoryImpl): AdminOrderRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository
}
