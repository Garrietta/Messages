package com.anindra.messages.ui.expressive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anindra.messages.data.CategoryStore
import com.anindra.messages.data.Conversation

@Composable
fun ExpressiveBottomLayer(
    categoryStore: CategoryStore,
    minimizedConversation: Conversation?,
    minimizedSeed: androidx.compose.ui.graphics.Color,
    onExpandMinimized: () -> Unit,
    onDismissMinimized: () -> Unit,
    onCategorySelect: (CategoryTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by categoryStore.categories.collectAsState()
    var selected by remember { mutableStateOf<CategoryTab>(CategoryTab.All) }
    val tabs = remember(categories) {
        listOf(CategoryTab.All) +
            categories.map { CategoryTab.Custom(it) } +
            listOf(CategoryTab.Archive)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (minimizedConversation != null) {
            MiniChatBar(
                conversation = minimizedConversation,
                seed = minimizedSeed,
                onExpand = onExpandMinimized,
                onDismiss = onDismissMinimized,
                modifier = Modifier.fillMaxWidth()
            )
        }
        CategoryNavBar(
            tabs = tabs,
            selected = selected,
            onSelect = {
                selected = it
                onCategorySelect(it)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
