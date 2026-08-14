package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GrimoireDarkColorScheme = darkColorScheme(
    primary = PrimaryGold,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainerGold,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryParchment,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryPurple,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    background = BackgroundDark,
    onBackground = OnSurface,
    surface = SurfaceBase,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainerHighest,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    outline = OutlineGold,
    outlineVariant = OutlineVariant,
    error = DamageRed,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

@Composable
fun GrimoireTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GrimoireDarkColorScheme,
        typography = Typography,
        content = content
    )
}
