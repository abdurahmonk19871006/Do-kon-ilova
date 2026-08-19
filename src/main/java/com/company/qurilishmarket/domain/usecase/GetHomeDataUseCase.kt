package com.company.qurilishmarket.domain.usecase

import com.company.qurilishmarket.domain.model.Category
import com.company.qurilishmarket.domain.model.Product
import com.company.qurilishmarket.domain.repository.CategoryRepository
import com.company.qurilishmarket.domain.repository.ProductRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

data class HomeData(
    val categories: List<Category>,
    val popularProducts: List<Product>,
    val discountedProducts: List<Product>,
    val newProducts: List<Product>
) {
    val isEmpty: Boolean
        get() = popularProducts.isEmpty() && discountedProducts.isEmpty() && newProducts.isEmpty()
}

/**
 * Bosh sahifa uchun 4 ta so'rovni **parallel** yuboradi (ketma-ket emas) — sahifa
 * ochilish tezligi 4x so'rovning eng sekinisiga teng bo'ladi, hammasining yig'indisiga emas.
 */
class GetHomeDataUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): Result<HomeData> = coroutineScope {
        runCatching {
            val categoriesDeferred = async { categoryRepository.getCategories().getOrThrow() }
            val popularDeferred = async { productRepository.getPopularProducts().getOrThrow() }
            val discountedDeferred = async { productRepository.getDiscountedProducts().getOrThrow() }
            val newDeferred = async { productRepository.getNewProducts().getOrThrow() }

            HomeData(
                categories = categoriesDeferred.await(),
                popularProducts = popularDeferred.await(),
                discountedProducts = discountedDeferred.await(),
                newProducts = newDeferred.await()
            )
        }
    }
}
