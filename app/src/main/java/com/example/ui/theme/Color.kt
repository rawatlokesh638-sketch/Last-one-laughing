package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Vibrant Palette - Primary & Purple Range
val VibrantPurple = Color(0xFF7C4DFF)
val VibrantPurpleLight = Color(0xFF8B5CF6)
val VibrantPurpleDark = Color(0xFF6750A4)
val VibrantPurpleContainer = Color(0xFFEADDFF)
val VibrantOnPurpleContainer = Color(0xFF21005D)
val VibrantLilac = Color(0xFFD0BCFF)
val VibrantLilacLight = Color(0xFFF3E8FF)

// Vibrant Palette - Secondary & Hot Pink / Magenta Range
val VibrantPink = Color(0xFFFF4081)
val VibrantPinkDeep = Color(0xFFEC4899)
val VibrantPinkContainer = Color(0xFFFFD8E4)
val VibrantOnPinkContainer = Color(0xFF31111D)
val VibrantPinkMuted = Color(0xFF633B48)
val VibrantFuchsia = Color(0xFFD946EF)

// Vibrant Palette - Warm Energy (Amber, Orange & Gold)
val VibrantAmber = Color(0xFFFFB300)
val VibrantOrange = Color(0xFFFF6D00)
val VibrantGold = Color(0xFFFFD54F)
val VibrantAmberContainer = Color(0xFFFFE082)
val VibrantOnAmberContainer = Color(0xFF3E2723)

// Vibrant Palette - Success & Emerald Accents
val VibrantEmerald = Color(0xFF10B981)
val VibrantCyan = Color(0xFF06B6D4)
val VibrantTeal = Color(0xFF14B8A6)

// Vibrant Palette - Surfaces & Deep Cosmic Backgrounds
val VibrantDarkBg = Color(0xFF0D0720)
val VibrantDarkBgMid = Color(0xFF1B1038)
val VibrantDarkBgEnd = Color(0xFF0F071D)
val VibrantSurface = Color(0xFF231545)
val VibrantSurfaceVariant = Color(0xFF2F1D5C)
val VibrantSurfaceElevated = Color(0xFF3B2474)
val VibrantCardBorder = Color(0x668B5CF6)
val VibrantGlowBorder = Color(0x99D0BCFF)

// Vibrant Gradients
val VibrantBgBrush = Brush.verticalGradient(
    listOf(VibrantDarkBg, VibrantDarkBgMid, VibrantDarkBgEnd)
)
val VibrantPlayCtaBrush = Brush.horizontalGradient(
    listOf(Color(0xFF7C4DFF), Color(0xFFEC4899))
)
val VibrantSecondaryCtaBrush = Brush.horizontalGradient(
    listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4))
)
val VibrantGoldBrush = Brush.horizontalGradient(
    listOf(VibrantAmber, VibrantOrange)
)
val VibrantCardBrush = Brush.linearGradient(
    listOf(Color(0xEE2A1752), Color(0xEE1A0E38))
)
val VibrantWarningBrush = Brush.horizontalGradient(
    listOf(Color(0xFFFF1744), Color(0xFFFF5252))
)

// Legacy Material 3 tokens mapped to Vibrant theme
val Purple80 = VibrantLilac
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = VibrantPinkContainer

val Purple40 = VibrantPurpleDark
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = VibrantPinkMuted
