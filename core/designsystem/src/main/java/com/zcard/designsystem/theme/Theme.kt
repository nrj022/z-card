package com.zcard.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ZBlack,
    onPrimary = ZWhite,

    secondaryContainer = ZBlack,
    onSecondaryContainer = ZWhite,

    tertiary = ZOrange500,
    error = ZRed500,

    background = ZWhite,
    onBackground = ZBlack,

    surface = ZWhite,
    onSurface = ZBlack,
    onSurfaceVariant = ZGray600,

    surfaceContainerLowest = ZWhite,
    surfaceContainerLow = ZGray25,
    surfaceContainer = ZGray50,
    surfaceContainerHigh = ZGray75,
    surfaceContainerHighest = ZGray100,

    outline = ZGray400,
    outlineVariant = ZGray100,

    surfaceTint = Color.Transparent,
)

private val DarkColorScheme = darkColorScheme(
    primary = ZGray100,
    onPrimary = ZBlack,

    secondaryContainer = ZGray100,
    onSecondaryContainer = ZBlack,

    tertiary = ZOrange500,

    background = ZGray950,
    onBackground = ZGray100,

    surface = ZGray950,
    onSurface = ZGray100,
    onSurfaceVariant = ZGray400,

    surfaceContainerLowest = ZGray975,
    surfaceContainerLow = ZGray925,
    surfaceContainer = ZGray900,
    surfaceContainerHigh = ZGray850,
    surfaceContainerHighest = ZBlack,

    outline = ZGray600,
    outlineVariant = ZGray700,

    surfaceTint = Color.Transparent,
)

@Composable
fun ZCardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}