package com.anindra.messages.ui.expressive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.anindra.messages.ui.avatarColor
import com.anindra.messages.ui.theme.expressive.ShapeCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        val loader = ImageLoader(context)
        val result = loader.execute(
            ImageRequest.Builder(context).data(photoUri).size(128).build()
        )
        val bitmap: Bitmap? = (result as? SuccessResult)?.image?.toBitmap()
        if (bitmap == null) null
        else Palette.from(bitmap).generate()
            .let { it.vibrantSwatch?.rgb ?: it.dominantSwatch?.rgb }
    } catch (_: Exception) { null }
}

@Composable
fun ExpressiveChatTheme(
    seed: Color,
    dark: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val base: ColorScheme = if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    val scheme = base.copy(
        primary = seed,
        primaryContainer = seed.copy(alpha = 0.25f),
        secondaryContainer = seed.copy(alpha = 0.18f),
        tertiary = seed.copy(alpha = 0.9f),
        tertiaryContainer = seed.copy(alpha = 0.3f)
    )
    CompositionLocalProvider(LocalExpressiveSeed provides seed) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}

object ExpressiveShapeSet {
    @Composable
    fun avatarShape() = ShapeCache.expressiveAvatar
}
