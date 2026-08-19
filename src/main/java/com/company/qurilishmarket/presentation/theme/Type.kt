package com.company.qurilishmarket.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * §4'da tavsiya etilgan shrift — Golos Text (Kirill+Lotin uchun kuchli, kelajakda rus tili
 * qo'shilsa foydali). Hozircha FontFamily.Default (tizim shrifti) qo'yilgan — shu holda ham
 * loyiha qo'shimcha setup'siz compile bo'ladi. Golos Text'ni ulash uchun: Android Studio →
 * Resource Manager → Font → "Golos Text" (Downloadable Fonts, Google Fonts katalogida bor)
 * va shu qatorni GoogleFont(...) bilan almashtiring.
 */
val QurilishMarketFontFamily = FontFamily.Default

val QurilishMarketTypography = Typography(
    headlineMedium = TextStyle(   // H1 — ekran sarlavhasi
        fontFamily = QurilishMarketFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(       // H2 — bo'lim sarlavhasi
        fontFamily = QurilishMarketFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(        // asosiy matn
        fontFamily = QurilishMarketFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = QurilishMarketFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(       // tugma matni
        fontFamily = QurilishMarketFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(       // narx birligi, mahsulot kodi
        fontFamily = QurilishMarketFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)
