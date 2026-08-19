package com.company.qurilishmarket.presentation.common

/**
 * §1'da tilga olingan umumiy UI holat modeli — deyarli har bir ViewModel shundan foydalanadi.
 * `T` — muvaffaqiyatli holatdagi ma'lumot turi (masalan, List<Product>).
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
}
