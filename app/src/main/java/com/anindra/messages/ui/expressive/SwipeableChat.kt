package com.anindra.messages.ui.expressive

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun SwipeableChat(
    onMinimize: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var dragOffset by remember { mutableFloatStateOf(0f) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { translationY = dragOffset }
    ) {
        content()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (dragOffset > 120f) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onMinimize()
                            }
                            dragOffset = 0f
                        },
                        onDragCancel = { dragOffset = 0f }
                    ) { _, dragAmount ->
                        if (dragAmount > 0) dragOffset = (dragOffset + dragAmount).coerceAtMost(400f)
                    }
                }
        )
    }
}
