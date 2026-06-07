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
    primary = EditorialAccentDark,
    onPrimary = Color.Black,
    secondary = EditorialPillDark,
    onSecondary = Color.White,
    tertiary = EditorialAccentDark,
    background = EditorialBackgroundDark,
    surface = EditorialCardDark,
    onBackground = EditorialTextMainDark,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = EditorialAccentLight,
    onPrimary = Color.White,
    secondary = EditorialPillLight,
    onSecondary = EditorialAccentLight,
    tertiary = TerracottaRed,
    background = EditorialBackgroundLight,
    surface = EditorialCardLight,
    onBackground = EditorialTextMainLight,
    onSurface = EditorialTextMainLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is overridden to false to safeguard Editorial Aesthetic palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
