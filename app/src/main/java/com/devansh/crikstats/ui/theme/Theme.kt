package com.devansh.crikstats.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF001D35),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    tertiary = Color(0xFFFF6F00),
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    error = Color(0xFFD32F2F)
)

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = Color(0xFF001D35),
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFF4DB6AC),
    onSecondary = Color(0xFF00251A),
    tertiary = Color(0xFFFFB74D),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFEF5350)
)

@Composable
fun CrikStatsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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