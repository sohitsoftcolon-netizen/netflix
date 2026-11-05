package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = CyanAccent,
  onPrimary = Navy900,
  primaryContainer = Navy800,
  onPrimaryContainer = CyanAccentLight,
  secondary = EmeraldShield,
  onSecondary = Color.White,
  background = DarkBackground,
  onBackground = Color(0xFFF1F5F9),
  surface = DarkSurface,
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = Color(0xFF94A3B8)
)

private val LightColorScheme = lightColorScheme(
  primary = Color(0xFF0284C7),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE0F2FE),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = EmeraldShield,
  onSecondary = Color.White,
  background = LightBackground,
  onBackground = Color(0xFF0F172A),
  surface = LightSurface,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = LightSurfaceElevated,
  onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
