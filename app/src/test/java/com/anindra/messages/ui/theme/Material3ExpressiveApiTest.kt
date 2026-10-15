package com.anindra.messages.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import com.anindra.messages.ui.theme.expressive.ShapeCache
import org.junit.Assert.assertNotNull
import org.junit.Test

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
class Material3ExpressiveApiTest {

    @Test
    fun expressiveShapesAndMotionSchemeAreAvailable() {
        assertNotNull(ShapeCache.expressiveAvatar)
        assertNotNull(ShapeCache.expressiveClover)
        assertNotNull(ShapeCache.expressiveHero)
        assertNotNull(MotionScheme.expressive())
    }
}
