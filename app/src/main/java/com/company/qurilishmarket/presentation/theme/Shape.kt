package com.company.qurilishmarket.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// §4: tugmalar 12dp, kartalar 12-16dp radius
val QurilishMarketShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),   // tugmalar, standart kartalar
    large = RoundedCornerShape(16.dp)     // katta kartalar, bottom sheet
)
