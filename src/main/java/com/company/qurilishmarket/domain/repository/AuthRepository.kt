package com.company.qurilishmarket.domain.repository

import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>
    val currentUserId: String?
    suspend fun sendOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, code: String): Result<Unit>
    suspend fun signOut()

    /** §6/§11: haqiqiy tekshiruv har doim serverda (RLS) ham qaytariladi — bu faqat UI uchun. */
    suspend fun isCurrentUserAdmin(): Boolean

    suspend fun getCurrentUserProfile(): Result<User>

    /** Telefon OTP orqali ro'yxatdan o'tishda ism so'ralmagan (§2: minimal friction) —
     * shuning uchun Profil ekranida keyinroq to'ldiriladi/o'zgartiriladi. */
    suspend fun updateProfileName(name: String): Result<Unit>
}
