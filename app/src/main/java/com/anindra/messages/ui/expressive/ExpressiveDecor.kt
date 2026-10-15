package com.anindra.messages.ui.expressive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.anindra.messages.ui.theme.expressive.ShapeCache

private val decorShapes: List<Shape> by lazy {
    listOf(
        ShapeCache.expressiveAvatar,
        ShapeCache.expressiveClover,
        ShapeCache.expressiveHero,
        ShapeCache.smooth32,
        ShapeCache.smoothPill
    )
}

@Composable
fun ExpressiveBackground(
    modifier: Modifier = Modifier,
    shapeCount: Int = 3,
    alpha: Float = 0.12f
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary
    )
    Box(modifier = modifier) {
        val placements = listOf(
            Triple((-60).dp, (-40).dp, 220.dp),
            Triple(280.dp, 120.dp, 160.dp),
            Triple((-40).dp, 420.dp, 180.dp)
        )
        placements.take(shapeCount).forEachIndexed { i, (x, y, size) ->
            Box(
                modifier = Modifier
                    .offset(x = x, y = y)
                    .size(size)
                    .alpha(alpha)
                    .background(colors[i % colors.size], decorShapes[i % decorShapes.size])
            )
        }
    }
}

private data class Triple<A, B, C>(val first: A, val second: B, val third: C)
