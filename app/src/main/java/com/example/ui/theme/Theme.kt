package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = AccentBlueSoft,
  onPrimary = DarkBg,
  primaryContainer = AccentBlueContainerDark,
  onPrimaryContainer = DarkTextPrimary,
  secondary = AccentBlue,
  onSecondary = DarkBg,
  secondaryContainer = DarkSurfaceElevated,
  onSecondaryContainer = DarkTextPrimary,
  tertiary = StatusSuspicious,
  onTertiary = DarkBg,
  error = StatusDanger,
  onError = DarkBg,
  errorContainer = StatusDangerContainerDark,
  onErrorContainer = StatusDanger,
  background = DarkBg,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
  primary = AccentBlue,
  onPrimary = LightSurface,
  primaryContainer = AccentBlueContainerLight,
  onPrimaryContainer = LightTextPrimary,
  secondary = AccentBlueSoft,
  onSecondary = LightSurface,
  secondaryContainer = LightSurfaceElevated,
  onSecondaryContainer = LightTextPrimary,
  tertiary = StatusSuspicious,
  onTertiary = LightSurface,
  error = StatusDanger,
  onError = LightSurface,
  errorContainer = StatusDangerContainerLight,
  onErrorContainer = StatusDanger,
  background = LightBg,
  onBackground = LightTextPrimary,
  surface = LightSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightSurfaceElevated,
  onSurfaceVariant = LightTextSecondary,
  outline = LightBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      val statusBarColor = (if (darkTheme) DarkBg else LightBg).toArgb()
      val navBarColor = (if (darkTheme) DarkSurface else LightSurface).toArgb()
      window.statusBarColor = statusBarColor
      window.navigationBarColor = navBarColor
      val windowInsetsController = WindowCompat.getInsetsController(window, view)
      windowInsetsController.isAppearanceLightStatusBars = !darkTheme
      windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}
