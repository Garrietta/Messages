package com.anindra.messages.ui.expressive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anindra.messages.R
import com.anindra.messages.data.CategoryStore
import com.anindra.messages.data.Conversation

@Composable
fun ExpressiveBottomLayer(
    categoryStore: CategoryStore,
    selectedCategoryId: String,
    minimizedConversation: Conversation?,
    minimizedSeed: androidx.compose.ui.graphics.Color,
    onExpandMinimized: () -> Unit,
    onDismissMinimized: () -> Unit,
    onCategorySelect: (String) -> Unit,
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by categoryStore.categories.collectAsState()
    val tabs = remember(categories) {
        listOf(CategoryTab.All) +
            categories.map { CategoryTab.Custom(it) } +
            listOf(CategoryTab.Archive)
    }
    val selected = tabs.firstOrNull { tab ->
        when (tab) {
            CategoryTab.All -> selectedCategoryId == CategoryStore.ALL_ID
            is CategoryTab.Custom -> selectedCategoryId == tab.category.id
            CategoryTab.Archive -> selectedCategoryId == CategoryStore.ARCHIVE_ID
        }
    } ?: CategoryTab.All
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val isWide = maxWidth > 600.dp
        val pillMaxWidth = if (isWide) maxWidth * 0.6f else maxWidth - 72.dp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(
                visible = minimizedConversation != null,
                enter = fadeIn(spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)) +
                    expandVertically(spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)) +
                    slideInVertically(
                        spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium),
                        initialOffsetY = { it / 3 }
                    ),
                exit = fadeOut(spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)) +
                    shrinkVertically(spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)) +
                    slideOutVertically(
                        spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium),
                        targetOffsetY = { it / 3 }
                    )
            ) {
                minimizedConversation?.let {
                    MiniChatBar(
                        conversation = it,
                        seed = minimizedSeed,
                        onExpand = onExpandMinimized,
                        onDismiss = onDismissMinimized,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SlidingCategoryPill(
                    tabs = tabs,
                    selected = selected,
                    onSelect = {
                        onCategorySelect(
                            when (it) {
                                CategoryTab.All -> CategoryStore.ALL_ID
                                is CategoryTab.Custom -> it.category.id
                                CategoryTab.Archive -> CategoryStore.ARCHIVE_ID
                            }
                        )
                    },
                    modifier = Modifier.weight(1f, fill = false)
                )
                IconButton(
                    onClick = onNewChat,
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.access_start_chat),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
