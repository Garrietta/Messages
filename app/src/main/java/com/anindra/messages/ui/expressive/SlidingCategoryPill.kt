package com.anindra.messages.ui.expressive

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anindra.messages.R
import com.anindra.messages.ui.theme.expressive.ShapeCache

@Composable
fun SlidingCategoryPill(
    tabs: List<CategoryTab>,
    selected: CategoryTab,
    onSelect: (CategoryTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val tabWidths = remember { mutableStateMapOf<CategoryTab, Dp>() }
    val tabOffsets = remember { mutableStateMapOf<CategoryTab, Dp>() }
    var accumulated = 0.dp
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier
            .shadow(8.dp, ShapeCache.smoothPill)
            .clip(ShapeCache.smoothPill)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        val selectedWidth = tabWidths[selected] ?: 0.dp
        val selectedOffset = tabOffsets[selected] ?: 0.dp
        val animatedWidth by animateDpAsState(
            selectedWidth,
            spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMediumLow),
            label = "pillWidth"
        )
        val animatedOffset by animateDpAsState(
            selectedOffset,
            spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMediumLow),
            label = "pillOffset"
        )
        if (selectedWidth > 0.dp) {
            Box(
                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(animatedWidth)
                    .matchParentHeight()
                    .clip(ShapeCache.smoothPill)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Row(
            modifier = Modifier.horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var currentOffset = 0.dp
            tabs.forEach { tab ->
                val isSelected = tab == selected
                val label = when (tab) {
                    is CategoryTab.All -> stringResource(R.string.category_all)
                    is CategoryTab.Custom -> tab.category.name
                    is CategoryTab.Archive -> stringResource(R.string.category_archive)
                }
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coords ->
                            with(density) {
                                val w = coords.size.width.toDp()
                                tabWidths[tab] = w
                                tabOffsets[tab] = currentOffset
                                currentOffset += w
                            }
                        }
                        .clip(ShapeCache.smoothPill)
                        .clickable { onSelect(tab) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
