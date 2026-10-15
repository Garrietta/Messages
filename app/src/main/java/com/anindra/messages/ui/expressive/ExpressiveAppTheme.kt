package com.anindra.messages.ui.expressive

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.anindra.messages.ui.theme.MessagesTheme

val LocalAppSeed = compositionLocalOf { Color.Unspecified }
val LocalSetAppSeed = staticCompositionLocalOf<(Color) -> Unit> { {} }

@Composable
fun ExpressiveAppTheme(
    dark: Boolean,
    content: @Composable () -> Unit
) {
    var appSeed by remember { mutableStateOf(Color.Unspecified) }
    val context = LocalContext.current
    val base: ColorScheme = if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    val scheme = if (appSeed != Color.Unspecified) {
        base.copy(
            primary = appSeed,
            primaryContainer = appSeed.copy(alpha = 0.28f),
            secondaryContainer = appSeed.copy(alpha = 0.2f),
            tertiary = appSeed.copy(alpha = 0.9f),
            tertiaryContainer = appSeed.copy(alpha = 0.32f)
        )
    } else base
    CompositionLocalProvider(
        LocalAppSeed provides appSeed,
        LocalSetAppSeed provides { appSeed = it }
    ) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
