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

private val DarkColorScheme = darkColorScheme(
    primary = AccentIndigo,
    secondary = TogetherViolet,
    tertiary = UmmuRose,
    background = TextPrimary, // Slate-black canvas under dark mode
    surface = TextPrimary,
    onPrimary = BgBase,
    onSecondary = BgBase,
    onTertiary = BgBase,
    onBackground = BgCard,
    onSurface = BgCard
)

private val LightColorScheme = lightColorScheme(
    primary = AccentIndigo,
    secondary = TogetherViolet,
    tertiary = UmmuRose,
    background = BgBase,
    surface = BgCard,
    onPrimary = BgCard,
    onSecondary = BgCard,
    onTertiary = BgCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false by default to ensure our masterfully styled custom Luminous Frost theme is always applied perfectly
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Immediately synchronize the global state to prevent single-frame flickering
    isAppDarkMode = darkTheme

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
