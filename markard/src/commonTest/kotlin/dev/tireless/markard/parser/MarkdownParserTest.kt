package dev.tireless.markard.parser

import dev.tireless.markard.model.Block
import dev.tireless.markard.model.Inline
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownParserTest {
    @Test fun parsesBasicBlocks() {
        val document = MarkdownParser.parse("# Hello\n\nSome **text**.")
        assertEquals(2, document.blocks.size)
        assertEquals(Block.Heading(1, listOf(Inline.Text("Hello"))), document.blocks.first())
    }
}
