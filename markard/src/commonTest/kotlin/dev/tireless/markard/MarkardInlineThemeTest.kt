package dev.tireless.markard

import dev.tireless.markard.model.Inline
import dev.tireless.markard.theme.MarkardTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkardInlineThemeTest {
    @Test
    fun everyInlineStyleUsesThemeSpacingAndStyles() {
        val theme = MarkardTheme.Xiaohongshu
        val rendered = buildThemedText(
            listOf(
                Inline.Bold(listOf(Inline.Text("S"))),
                Inline.Emphasis(listOf(Inline.Text("E"))),
                Inline.Strikethrough(listOf(Inline.Text("H"))),
                Inline.Code("C"),
                Inline.Hashtag("T"),
            ),
            theme,
        )

        listOf("S", "E", "H", "C", "T").forEach { token ->
            val index = rendered.text.text.indexOf(token)
            assertEquals(theme.inlines.spacing, rendered.text.text.substring(index - 1, index))
            assertEquals(theme.inlines.spacing, rendered.text.text.substring(index + 1, index + 2))
        }

        val emphasis = rendered.highlightRanges.single()
        assertEquals("E", rendered.text.text.substring(emphasis.start, emphasis.end))
        val hashtag = rendered.hashtagRanges.single()
        assertEquals("T", rendered.text.text.substring(hashtag.start, hashtag.end))
        assertTrue(rendered.text.spanStyles.any { it.item == theme.inlines.bold })
    }
}
