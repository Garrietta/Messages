package com.anindra.messages.ui.expressive

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anindra.messages.R
import com.anindra.messages.data.MessageCategory
import com.anindra.messages.ui.theme.expressive.ShapeCache

sealed interface CategoryTab {
    data object All : CategoryTab
    data class Custom(val category: MessageCategory) : CategoryTab
    data object Archive : CategoryTab
}

@Composable
fun CategoryNavBar(
    tabs: List<CategoryTab>,
    selected: CategoryTab,
    onSelect: (CategoryTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(12.dp, ShapeCache.smoothPill)
            .clip(ShapeCache.smoothPill)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selected
                val bg by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceContainerHighest,
                    label = "tabBg"
                )
                val fg by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "tabFg"
                )
                val label = when (tab) {
                    is CategoryTab.All -> stringResource(R.string.category_all)
                    is CategoryTab.Custom -> tab.category.name
                    is CategoryTab.Archive -> stringResource(R.string.category_archive)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(ShapeCache.smoothPill)
                        .background(bg)
                        .clickable { onSelect(tab) }
                        .padding(horizontal = if (isSelected) 18.dp else 14.dp, vertical = 12.dp)
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(fg, androidx.compose.foundation.shape.CircleShape)
                        )
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = fg
                    )
                }
            }
        }
    }
}
