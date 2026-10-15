package com.anindra.messages.ui.expressive

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
            .clip(ShapeCache.smoothPill)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selected
                val bg by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceContainerHigh
                )
                val fg by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                val label = when (tab) {
                    is CategoryTab.All -> stringResource(R.string.category_all)
                    is CategoryTab.Custom -> tab.category.name
                    is CategoryTab.Archive -> stringResource(R.string.category_archive)
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = fg,
                    modifier = Modifier
                        .clip(ShapeCache.smoothPill)
                        .background(bg)
                        .selectable(
                            selected = isSelected,
                            role = Role.Tab,
                            onClick = { onSelect(tab) }
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}
