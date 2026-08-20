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

    @Test fun parsesCardEmphasisExtensions() {
        val document = MarkdownParser.parse("# Make it ^^red^^ and ==loud==")
        assertEquals(
            Block.Heading(
                1,
                listOf(
                    Inline.Text("Make it "),
                    Inline.Accent(listOf(Inline.Text("red"))),
                    Inline.Text(" and "),
                    Inline.Highlight(listOf(Inline.Text("loud"))),
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

    @Test fun splitsSectionsAtLevelTwoHeadingsAndDashSeparators() {
        val documents = MarkdownParser.parseSections(
            """引言

## 第一张
内容一
--
## 第二张
内容二
""".trimIndent(),
        )

        assertEquals(3, documents.size)
        assertEquals(Block.Paragraph(listOf(Inline.Text("引言"))), documents[0].blocks.single())
        assertEquals(Block.Heading(2, listOf(Inline.Text("第一张"))), documents[1].blocks[0])
        assertEquals(Block.Heading(2, listOf(Inline.Text("第二张"))), documents[2].blocks[0])
    }

    @Test fun ignoresEmptySectionsAndOnlySplitsStandaloneDashLine() {
        val documents = MarkdownParser.parseSections("--\n## 标题\n价格 -- 不是分隔线\n--\n")

        assertEquals(1, documents.size)
        assertEquals(2, documents.single().blocks.size)
    }

    @Test fun exposesMarkdownForEveryPage() {
        assertEquals(
            listOf("## 第一页\n内容一", "第二页", "## 第三页\n内容三"),
            MarkdownParser.splitIntoSections(
                """## 第一页
内容一
--
第二页
## 第三页
内容三""",
            ),
        )
    }
}
