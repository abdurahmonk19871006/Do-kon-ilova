package com.company.qurilishmarket.di

import com.company.qurilishmarket.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

/**
 * Supabase mijozini butun ilova bo'ylab bitta nusxada (Singleton) taqdim etadi. URL va
 * publishable key BuildConfig orqali keladi (local.properties'dan) — kodga qattiq
 * yozilmaydi, git'ga tushmaydi. Repository implementatsiyalari (keyingi bosqichda) shu
 * clientni constructor orqali oladi — Postgrest/Auth chaqiruvlari faqat data/ qatlamida
 * qoladi, Domain va Presentation ulardan umuman bexabar (§1'dagi Dependency Inversion).
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
        install(Realtime)
    }
}
