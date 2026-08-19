package com.company.qurilishmarket.data.repository

import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.domain.model.User
import com.company.qurilishmarket.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

/**
 * DIQQAT (haqiqiy ish uchun shart): telefon OTP orqali SMS yuborish uchun Supabase
 * loyihangizda SMS provayder ulangan bo'lishi kerak — Dashboard → Authentication →
 * Providers → Phone (Twilio, MessageBird va h.k. — bepul emas, alohida ro'yxatdan o'tish
 * kerak). Test paytida Dashboard'da "Test OTP" raqam+kod qo'shib, haqiqiy SMS'siz sinash
 * mumkin. Bu ilova kodi tomonidan hal qilinmaydi — sof infratuzilma sozlamasi.
 *
 * signInWith(OTP)/verifyPhoneOtp — https://supabase.com/docs/guides/auth/phone-login orqali
 * tasdiqlangan (2026-yil holatiga ko'ra).
 */
class AuthRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : AuthRepository {

    override val authState: Flow<AuthState> = client.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> AuthState.LoggedIn(status.session.user?.id.orEmpty())
            is SessionStatus.NotAuthenticated -> AuthState.LoggedOut
            else -> AuthState.Loading // LoadingFromStorage / RefreshFailure
        }
    }

    override val currentUserId: String?
        get() = client.auth.currentUserOrNull()?.id

    override suspend fun sendOtp(phone: String): Result<Unit> = runCatching {
        client.auth.signInWith(OTP) { this.phone = phone }
    }

    override suspend fun verifyOtp(phone: String, code: String): Result<Unit> = runCatching {
        client.auth.verifyPhoneOtp(type = OtpType.Phone.SMS, phone = phone, token = code)
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }

    @Serializable
    private data class AdminFlagDto(@SerialName("is_admin") val isAdmin: Boolean)

    override suspend fun isCurrentUserAdmin(): Boolean {
        val userId = client.auth.currentUserOrNull()?.id ?: return false
        return runCatching {
            client.from("profiles")
                .select(Columns.list("is_admin")) { filter { eq("id", userId) } }
                .decodeSingle<AdminFlagDto>()
                .isAdmin
        }.getOrDefault(false)
    }

    @Serializable
    private data class ProfileDto(
        val id: String,
        val name: String,
        val phone: String? = null,
        @SerialName("is_admin") val isAdmin: Boolean = false
    )

    override suspend fun getCurrentUserProfile(): Result<User> = runCatching {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Tizimga kirilmagan")
        client.from("profiles")
            .select { filter { eq("id", userId) } }
            .decodeSingle<ProfileDto>()
            .let { User(id = it.id, name = it.name, phone = it.phone.orEmpty(), isAdmin = it.isAdmin) }
    }

    @Serializable
    private data class NameUpdateDto(val name: String)

    override suspend fun updateProfileName(name: String): Result<Unit> = runCatching {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Tizimga kirilmagan")
        client.from("profiles").update(NameUpdateDto(name = name)) { filter { eq("id", userId) } }
        Unit
    }
}
