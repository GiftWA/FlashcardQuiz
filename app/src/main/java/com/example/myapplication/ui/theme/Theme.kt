package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary                  = BrandBlue,
    onPrimary                = OnPrimaryWhite,
    primaryContainer         = BlueTint,
    onPrimaryContainer       = BrandBlue,

    secondary                = TextSecondary,
    onSecondary              = OnPrimaryWhite,
    secondaryContainer       = BlueTint,
    onSecondaryContainer     = BrandBlue,

    tertiary                 = BrandBlueLight,
    onTertiary               = OnPrimaryWhite,
    tertiaryContainer        = BlueTintStrong,
    onTertiaryContainer      = BrandBlue,

    background               = SurfaceBackground,
    onBackground             = TextPrimary,

    surface                  = SurfaceCard,
    onSurface                = TextPrimary,
    surfaceVariant           = BlueTint,
    onSurfaceVariant         = TextSecondary,

    surfaceContainer         = SurfaceBackground,
    surfaceContainerHigh     = BlueTint,
    surfaceContainerHighest  = BlueTintStrong,

    outline                  = Outline,
    outlineVariant           = OutlineVariant,

    error                    = ErrorRed,
    onError                  = OnErrorWhite,
    errorContainer           = ErrorContainer,
    onErrorContainer         = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary                  = DarkBluePrimary,
    onPrimary                = OnPrimaryWhite,
    primaryContainer         = BrandBlueDark,
    onPrimaryContainer       = BlueTint,

    secondary                = DarkTextSecondary,
    onSecondary              = OnPrimaryWhite,
    secondaryContainer       = DarkSurfaceVar,
    onSecondaryContainer     = DarkTextPrimary,

    tertiary                 = BrandBlueLight,
    onTertiary               = OnPrimaryWhite,

    background               = DarkBackground,
    onBackground             = DarkTextPrimary,
    surface                  = DarkSurface,
    onSurface                = DarkTextPrimary,
    surfaceVariant           = DarkSurfaceVar,
    onSurfaceVariant         = DarkTextSecondary,

    outline                  = DarkOutline,
    outlineVariant           = DarkSurfaceVar,

    error                    = ErrorRed,
    onError                  = OnErrorWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic colour disabled so the brand palette always wins.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}