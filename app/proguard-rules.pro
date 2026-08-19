# kotlinx.serialization uchun (Supabase DTO'lar @Serializable bo'ladi) — standart qoidalar
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclasseswithmembers class com.company.qurilishmarket.data.remote.dto.** {
    *** Companion;
}
-keepclasseswithmembers class com.company.qurilishmarket.data.remote.dto.**$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
