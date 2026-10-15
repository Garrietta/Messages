package com.anindra.messages.ui.expressive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color

@Composable
fun SeedUpdater(address: String?) {
    val setSeed = LocalSetAppSeed.current
    val seed = if (address != null) {
        rememberConversationSeed(null, address)
    } else Color.Unspecified
    LaunchedEffect(seed) {
        setSeed(seed)
    }
}
