import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

// local.properties'dan SUPABASE_URL / SUPABASE_PUBLISHABLE_KEY o'qiladi — kodga qattiq
// yozilmaydi, git'ga tushmaydi (.gitignore'da). Fayl bo'lmasa, bo'sh qiymat bilan davom
// etadi (build sinmaydi, lekin ilova ishga tushganda xato beradi — ataylab shunday).
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.company.qurilishmarket"
    compileSdk = 36 // 2026-08-31'dan Google Play shuni talab qiladi (§8)

    defaultConfig {
        applicationId = "com.company.qurilishmarket"
        minSdk = 26   // O'zbekiston bozoridagi arzonroq qurilmalarni ham qamrab olish uchun (§8);
                      // shuningdek supabase-kt'ning o'zi ham minimal shu versiyani talab qiladi
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"${localProperties.getProperty("SUPABASE_PUBLISHABLE_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true // buildConfigField ishlashi uchun shart
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    // Dependency Injection (§1)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Local cache (§1, §8)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Sozlamalar: til, tema, token (§8)
    implementation(libs.datastore.preferences)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json) // type-safe navigation + Supabase serializatsiya uchun

    implementation(libs.coil.compose)

    // Backend (§6, §8) — Supabase: Postgrest, Auth, Storage, Realtime
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.storage)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.okhttp) // Realtime WebSocket'ni qo'llab-quvvatlaydigan engine
}
