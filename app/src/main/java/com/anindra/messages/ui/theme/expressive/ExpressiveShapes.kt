package com.anindra.messages.ui.theme.expressive

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 Expressive shape scale, ported from the PixelMusic theme.
 * Larger, rounder corners than the M3 defaults — the expressive look.
 */
val ExpressiveShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)
