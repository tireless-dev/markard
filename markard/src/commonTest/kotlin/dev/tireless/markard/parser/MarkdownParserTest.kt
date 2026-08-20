package dev.tireless.markard.parser

import dev.tireless.markard.model.Block
import dev.tireless.markard.model.Inline
import dev.tireless.markard.model.ListItem
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownParserTest {
    @Test fun parsesBasicBlocks() {
        val document = MarkdownParser.parse("# Hello\n\nSome **text**.")
        assertEquals(2, document.blocks.size)
        assertEquals(Block.Heading(1, listOf(Inline.Text("Hello"))), document.blocks.first())
    }

    @Test fun parsesStandardInlineFormatting() {
        val document = MarkdownParser.parse("# Make it **bold** and *italic* with `code`")
        assertEquals(
            Block.Heading(
                1,
                listOf(
                    Inline.Text("Make it "),
                    Inline.Bold(listOf(Inline.Text("bold"))),
                    Inline.Text(" and "),
                    Inline.Emphasis(listOf(Inline.Text("italic"))),
                    Inline.Text(" with "),
                    Inline.Code("code"),
                ),
            ),
            document.blocks.first(),
        )
    }

    @Test fun distinguishesUnorderedAndOrderedLists() {
        val document = MarkdownParser.parse("- Alpha\n- Beta\n\n3. Third\n4. Fourth")

        assertEquals(
            Block.UnorderedList(
                listOf(
                    ListItem(listOf(Inline.Text("Alpha"))),
                    ListItem(listOf(Inline.Text("Beta"))),
                ),
            ),
            document.blocks[0],
        )
        assertEquals(
            Block.OrderedList(
                items = listOf(
                    ListItem(listOf(Inline.Text("Third"))),
                    ListItem(listOf(Inline.Text("Fourth"))),
                ),
                start = 3,
            ),
            document.blocks[1],
        )
    }

    @Test fun parsesHashtagsAsInlineSemantics() {
        val document = MarkdownParser.parse("#效率提升 #自我管理 #growth_record")

        assertEquals(
            Block.Paragraph(
                listOf(
                    Inline.Hashtag("#效率提升"),
                    Inline.Text(" "),
                    Inline.Hashtag("#自我管理"),
                    Inline.Text(" "),
                    Inline.Hashtag("#growth_record"),
                ),
            ),
            document.blocks.single(),
        )
    }

    @Test fun splitsSectionsAtLevelTwoHeadingsOnly() {
        val documents = MarkdownParser.parseSections(
            """引言

## 第一张
内容一

---

## 第二张
内容二
""".trimIndent(),
        )

        assertEquals(3, documents.size)
        assertEquals(Block.Paragraph(listOf(Inline.Text("引言"))), documents[0].blocks.single())
        assertEquals(Block.Heading(2, listOf(Inline.Text("第一张"))), documents[1].blocks[0])
        assertEquals(Block.Heading(2, listOf(Inline.Text("第二张"))), documents[2].blocks[0])
    }

    @Test fun parsesHorizontalRuleForSingleDocuments() {
        val document = MarkdownParser.parse("## 标题\n\n---\n\n内容\n")

        assertEquals(3, document.blocks.size)
        assertEquals(Block.HorizontalRule, document.blocks[1])
    }

    @Test fun parsesAdditionalHeadingLevelsStrikethroughAndTaskItems() {
        val document = MarkdownParser.parse(
            "### Third\n#### Fourth\n##### Fifth\n###### Sixth\n\n~~removed~~\n\n- [x] Done\n- [ ] Todo",
        )

        assertEquals(listOf(3, 4, 5, 6), document.blocks.take(4).map { (it as Block.Heading).level })
        assertEquals(Block.Paragraph(listOf(Inline.Strikethrough(listOf(Inline.Text("removed"))))), document.blocks[4])
        assertEquals(true, (document.blocks[5] as Block.UnorderedList).items[0].checked)
        assertEquals(false, (document.blocks[5] as Block.UnorderedList).items[1].checked)
    }

    @Test fun exposesMarkdownForEveryPage() {
        assertEquals(
            listOf("## 第一页\n内容一", "第二页", "## 第三页\n内容三"),
            MarkdownParser.splitIntoSections(
                """## 第一页
内容一
---
第二页
## 第三页
内容三""",
            ),
        )
    }
}
