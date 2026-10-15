package com.anindra.messages.ui.expressive

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpressiveColorSchemeTest {
    @Test
    fun `photo seed becomes primary with a contrasting foreground`() {
        val light = lightColorScheme()
        val brightSeed = Color.Yellow
        val lightScheme = expressiveColorScheme(light, brightSeed)

        assertEquals(brightSeed, lightScheme.primary)
        assertEquals(light.onSurface, lightScheme.onPrimary)
    }

    @Test
    fun `dark photo seed uses the theme foreground`() {
        val dark = darkColorScheme()
        val darkSeed = Color.Blue

        assertEquals(dark.onSurface, expressiveColorScheme(dark, darkSeed).onPrimary)
    }
}
