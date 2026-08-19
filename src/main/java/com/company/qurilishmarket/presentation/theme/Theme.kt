package com.company.qurilishmarket.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val QurilishMarketLightColors = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = BackgroundWhite,
    secondary = OrangeAccent,
    onSecondary = BackgroundWhite,
    background = BackgroundWhite,
    surface = BackgroundSoft,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed,
    outline = BorderDivider
)

/**
 * §4: light mode — MVP uchun yagona rejim, shuning uchun hozircha faqat lightColorScheme
 * beriladi. Dark mode qo'shilganda (§10) shu funksiya ichida isSystemInDarkTheme() bilan
 * branch qo'shiladi — chaqiruvchi tomon (MainActivity) o'zgarmaydi.
 */
@Composable
fun QurilishMarketTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QurilishMarketLightColors,
        typography = QurilishMarketTypography,
        shapes = QurilishMarketShapes,
        content = content
    )
}
