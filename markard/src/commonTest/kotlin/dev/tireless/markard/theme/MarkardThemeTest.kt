package dev.tireless.markard.theme

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MarkardThemeTest {
    @Test
    fun themesOwnCardDocumentAndInlineRenderingDecisions() {
        val xiaohongshu = MarkardTheme.Xiaohongshu
        val midnight = MarkardTheme.Midnight

        assertEquals(3f / 4f, xiaohongshu.card.aspectRatio)
        assertEquals(3f / 4f, MarkardTheme.Default.card.aspectRatio)
        assertEquals(3f / 4f, MarkardTheme.Minimal.card.aspectRatio)
        assertEquals(Alignment.CenterStart, xiaohongshu.document.alignment)
        assertEquals(0.2f, xiaohongshu.inlines.highlightStripeHeightFraction)
        assertEquals(50.sp, xiaohongshu.blocks.heading1.fontSize)
        assertEquals(38.sp, xiaohongshu.blocks.heading2.fontSize)

        assertEquals(Alignment.TopStart, midnight.document.alignment)
        assertEquals(0.32f, midnight.inlines.highlightStripeHeightFraction)
        assertNotEquals(xiaohongshu.card.background, midnight.card.background)
        assertNotEquals(xiaohongshu.inlines.bold.color, midnight.inlines.bold.color)
    }

    @Test
    fun fontShowcaseUsesAllBundledFontFamilies() {
        val fonts = MarkardTheme.FontShowcase.fonts

        assertEquals(MarkardFont.NotoSerifCjk, fonts.heading)
        assertEquals(MarkardFont.NotoSansCjk, fonts.body)
        assertEquals(MarkardFont.LxgwWenKai, fonts.bold)
        assertEquals(MarkardFont.SarasaGothic, fonts.emphasis)
    }
}
