package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Material 3 Light Color Scheme for GasOn
 * Styled with:
 * - GasOn Royal Blue (Primary)
 * - GasOn Power Orange (Secondary & Primary CTA)
 * - GasOn Deep Blue (Dark surfaces / Top Bar)
 * - Clean white cards & slate accents
 */
private val LightColorScheme = lightColorScheme(
    primary = GasOnBlue,
    onPrimary = Color.White,
    primaryContainer = GasOnBlueContainer,
    onPrimaryContainer = GasOnBlueDark,
    inversePrimary = GasOnBlueLight,

    secondary = GasOnOrange,
    onSecondary = Color.White,
    secondaryContainer = GasOnOrangeContainer,
    onSecondaryContainer = GasOnOrangeDark,

    tertiary = FlameRed,
    onTertiary = Color.White,
    tertiaryContainer = FlameRedContainer,
    onTertiaryContainer = FlameRedDark,

    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = TextSecondary,
    surfaceTint = GasOnBlue,
    inverseSurface = GasOnBlueDeep,
    inverseOnSurface = Color.White,

    outline = BorderLight,
    outlineVariant = Color(0xFFCBD5E1),

    error = FlameRed,
    onError = Color.White,
    errorContainer = FlameRedContainer,
    onErrorContainer = FlameRedDark
)

/**
 * Material 3 Dark Color Scheme for GasOn
 */
private val DarkColorScheme = darkColorScheme(
    primary = GasOnBlueLight,
    onPrimary = GasOnBlueDeep,
    primaryContainer = GasOnBlueDark,
    onPrimaryContainer = GasOnBlueContainer,
    inversePrimary = GasOnBlue,

    secondary = GasOnOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF8A2E00),
    onSecondaryContainer = GasOnOrangeContainer,

    tertiary = FlameRedLight,
    onTertiary = GasOnBlueDeep,
    tertiaryContainer = Color(0xFF7F1D1D),
    onTertiaryContainer = FlameRedContainer,

    background = GasOnBlueDeep,
    onBackground = Color.White,
    surface = GasOnBlueDark,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF0F3260),
    onSurfaceVariant = Color(0xFFCBD5E1),
    surfaceTint = GasOnBlueLight,
    inverseSurface = SurfaceLight,
    inverseOnSurface = TextPrimary,

    outline = Color(0xFF1E4578),
    outlineVariant = Color(0xFF0F3260),

    error = FlameRedLight,
    onError = GasOnBlueDeep,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = FlameRedContainer
)

@Immutable
data class GasOnBrandPalette(
    val brandBlue: Color = GasOnBlue,
    val brandBlueDark: Color = GasOnBlueDark,
    val brandBlueDeep: Color = GasOnBlueDeep,
    val brandOrange: Color = GasOnOrange,
    val brandOrangeDark: Color = GasOnOrangeDark,
    val brandYellow: Color = GasOnYellow,
    val flameRed: Color = FlameRed,
    val deliveryGreen: Color = DeliveryGreen
)

val LocalGasOnBrandPalette = staticCompositionLocalOf { GasOnBrandPalette() }

val MaterialTheme.gasOnColors: GasOnBrandPalette
    @Composable
    get() = LocalGasOnBrandPalette.current

@Composable
fun GasOnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val brandPalette = GasOnBrandPalette(
        brandBlue = if (darkTheme) GasOnBlueLight else GasOnBlue,
        brandBlueDark = GasOnBlueDark,
        brandBlueDeep = GasOnBlueDeep,
        brandOrange = GasOnOrange,
        brandOrangeDark = GasOnOrangeDark,
        brandYellow = GasOnYellow,
        flameRed = FlameRed,
        deliveryGreen = DeliveryGreen
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = (if (darkTheme) GasOnBlueDeep else GasOnBlueDark).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalGasOnBrandPalette provides brandPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
