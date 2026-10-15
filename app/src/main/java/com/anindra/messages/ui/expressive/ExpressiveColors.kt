package com.anindra.messages.ui.expressive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import com.anindra.messages.ui.avatarColor
import com.anindra.messages.ui.theme.expressive.ShapeCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

val LocalExpressiveSeed = staticCompositionLocalOf { Color.Unspecified }

@Composable
fun rememberConversationSeed(photoUri: String?, key: String): Color {
    val context = LocalContext.current
    val fallback = avatarColor(key)
    val seed by produceState(initialValue = fallback, photoUri, key) {
        value = withContext(Dispatchers.IO) {
            extractVibrantColor(context, photoUri) ?: fallback.toArgb()
        }.let { Color(it) }
    }
    return seed
}

private suspend fun extractVibrantColor(context: Context, photoUri: String?): Int? {
    if (photoUri.isNullOrBlank()) return null
    return try {
        val uri = Uri.parse(photoUri)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val boundsStream = context.contentResolver.openInputStream(uri) ?: return null
        boundsStream.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val options = BitmapFactory.Options().apply {
            inSampleSize = (maxOf(bounds.outWidth, bounds.outHeight) / 128).coerceAtLeast(1)
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return null
        try {
            Palette.from(bitmap).maximumColorCount(16).generate()
                .let { it.vibrantSwatch?.rgb ?: it.dominantSwatch?.rgb }
        } finally {
            bitmap.recycle()
        }
    } catch (error: IOException) {
        Log.w("ExpressiveColors", "Unable to read contact photo for palette", error)
        null
    } catch (error: SecurityException) {
        Log.w("ExpressiveColors", "Contact photo permission denied for palette", error)
        null
    } catch (error: IllegalArgumentException) {
        Log.w("ExpressiveColors", "Invalid contact photo URI for palette", error)
        null
    }
}

@Composable
fun ExpressiveChatTheme(seed: Color, content: @Composable () -> Unit) {
    val base: ColorScheme = MaterialTheme.colorScheme
    val scheme = expressiveColorScheme(base, seed)
    CompositionLocalProvider(LocalExpressiveSeed provides seed) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}

internal fun expressiveColorScheme(base: ColorScheme, seed: Color): ColorScheme {
    val darkTheme = base.surface.luminance() < 0.5f
    val onSeed = when {
        seed.luminance() > 0.5f && darkTheme -> base.inverseOnSurface
        seed.luminance() > 0.5f -> base.onSurface
        darkTheme -> base.onSurface
        else -> base.inverseOnSurface
    }
    return base.copy(
        primary = seed,
        onPrimary = onSeed,
        primaryContainer = seed.copy(alpha = 0.18f).compositeOver(base.surfaceContainerHigh),
        onPrimaryContainer = base.onSurface,
        secondaryContainer = seed.copy(alpha = 0.12f).compositeOver(base.surfaceContainerHigh),
        tertiary = seed,
        tertiaryContainer = seed.copy(alpha = 0.2f).compositeOver(base.surfaceContainerHigh),
        background = base.background.copy(alpha = 0.96f)
    )
}

object ExpressiveShapeSet {
    @Composable
    fun avatarShape() = ShapeCache.expressiveAvatar
}
