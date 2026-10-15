package com.anindra.messages.ui.expressive

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.anindra.messages.ui.theme.expressive.ShapeCache

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveChatContainer(
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    seed: Color,
    ambientBackground: Boolean = true,
    modifier: Modifier = Modifier,
    miniContent: @Composable () -> Unit,
    fullContent: @Composable () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val progress by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chatExpand"
    )
    var dragOffset by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        if (progress > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(progress)
                    .graphicsLayer {
                        translationY = dragOffset
                        scaleX = lerp(0.92f, 1f, progress)
                        scaleY = lerp(0.92f, 1f, progress)
                    }
            ) {
                if (ambientBackground) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(seed.copy(alpha = 0.15f))
                            .blur(60.dp)
                    )
                }
                ExpressiveBackground(
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.1f * progress
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            ShapeCache.smooth32
                        )
                ) {
                    fullContent()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragEnd = {
                                    if (dragOffset > 120f) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onExpandChange(false)
                                    }
                                    dragOffset = 0f
                                },
                                onDragCancel = { dragOffset = 0f }
                            ) { _, amount ->
                                if (amount > 0) dragOffset = (dragOffset + amount).coerceAtMost(400f)
                            }
                        }
                )
            }
        }
        if (progress < 0.99f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(1f - progress)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = { dragOffset = 0f },
                            onDragCancel = { dragOffset = 0f }
                        ) { _, _ -> }
                    }
            ) {
                miniContent()
            }
        }
    }
}
