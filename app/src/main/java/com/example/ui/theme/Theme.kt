package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = VibrantPurple,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = VibrantPurpleDark,
    onPrimaryContainer = VibrantLilacLight,
    secondary = VibrantPink,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = VibrantPinkMuted,
    onSecondaryContainer = VibrantPinkContainer,
    tertiary = VibrantGold,
    onTertiary = VibrantOnAmberContainer,
    tertiaryContainer = VibrantAmber,
    onTertiaryContainer = VibrantOnAmberContainer,
    background = VibrantDarkBg,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = VibrantSurface,
    onSurface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = VibrantSurfaceVariant,
    onSurfaceVariant = VibrantLilacLight,
    outline = VibrantCardBorder,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VibrantPurpleDark,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = VibrantPurpleContainer,
    onPrimaryContainer = VibrantOnPurpleContainer,
    secondary = VibrantPinkDeep,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = VibrantPinkContainer,
    onSecondaryContainer = VibrantOnPinkContainer,
    tertiary = VibrantOrange,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = VibrantAmberContainer,
    onTertiaryContainer = VibrantOnAmberContainer,
    background = VibrantDarkBg,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = VibrantSurface,
    onSurface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = VibrantSurfaceVariant,
    onSurfaceVariant = VibrantLilacLight,
    outline = VibrantCardBorder,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

