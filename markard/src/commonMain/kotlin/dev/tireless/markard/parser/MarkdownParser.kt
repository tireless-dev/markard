package dev.tireless.markard.parser

import dev.tireless.markard.model.Block
import dev.tireless.markard.model.Inline
import dev.tireless.markard.model.ListItem
import dev.tireless.markard.model.MarkardDocument
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.CancellationToken
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.MarkdownParser as JetBrainsMarkdownParser

/** Maps JetBrains' multiplatform Markdown AST to Markard's render model. */
object MarkdownParser {
    private val sectionHeading = Regex("^##\\s+.+$")
    private val hashtag = Regex("#[\\p{L}\\p{N}_-]+")
    private val flavour = GFMFlavourDescriptor()

    fun parse(markdown: String): MarkardDocument {
        val source = markdown.trim()
        val root = JetBrainsMarkdownParser(flavour, cancellationToken = CancellationToken.NonCancellable)
            .buildMarkdownTreeFromString(source as CharSequence)
        return MarkardDocument(root.children.mapNotNull { it.toBlock(source) })
    }

    fun parseSections(markdown: String): List<MarkardDocument> = splitIntoSections(markdown).map(::parse)

    /** Returns the source Markdown for each card section. */
    fun splitIntoSections(markdown: String): List<String> = splitSectionLines(markdown)
        .map { lines -> lines.joinToString("\n").trim() }

    private fun splitSectionLines(markdown: String): List<List<String>> {
        val sections = mutableListOf<MutableList<String>>()
        var current = mutableListOf<String>()

        fun flush() {
            if (current.any { it.isNotBlank() }) sections += current
            current = mutableListOf()
        }

        markdown.lines().forEach { rawLine ->
            val line = rawLine.trim()
            when {
                sectionHeading.matches(line) && current.any { it.isNotBlank() } -> {
                    flush()
                    current += rawLine
                }
                line == "---" -> flush()
                else -> current += rawLine
            }
        }
        flush()
        return sections
    }

    private fun ASTNode.toBlock(source: String): Block? = when (type) {
        MarkdownElementTypes.ATX_1, MarkdownElementTypes.SETEXT_1 ->
            Block.Heading(1, inlineChildren(source).trimTextWhitespace())
        MarkdownElementTypes.ATX_2, MarkdownElementTypes.SETEXT_2 ->
            Block.Heading(2, inlineChildren(source).trimTextWhitespace())
        MarkdownElementTypes.ATX_3 -> Block.Heading(3, inlineChildren(source).trimTextWhitespace())
        MarkdownElementTypes.ATX_4 -> Block.Heading(4, inlineChildren(source).trimTextWhitespace())
        MarkdownElementTypes.ATX_5 -> Block.Heading(5, inlineChildren(source).trimTextWhitespace())
        MarkdownElementTypes.ATX_6 -> Block.Heading(6, inlineChildren(source).trimTextWhitespace())
        MarkdownElementTypes.PARAGRAPH -> Block.Paragraph(inlineChildren(source))
        MarkdownElementTypes.BLOCK_QUOTE -> Block.Quote(inlineDescendants(source))
        MarkdownTokenTypes.HORIZONTAL_RULE -> Block.HorizontalRule
        MarkdownElementTypes.UNORDERED_LIST -> Block.UnorderedList(
            children.filter { it.type == MarkdownElementTypes.LIST_ITEM }
                .map { it.toListItem(source) },
        )
        MarkdownElementTypes.ORDERED_LIST -> Block.OrderedList(
            items = children.filter { it.type == MarkdownElementTypes.LIST_ITEM }
                .map { it.toListItem(source) },
            start = orderedListStart(source),
        )
        MarkdownElementTypes.CODE_FENCE, MarkdownElementTypes.CODE_BLOCK ->
            Block.Paragraph(listOf(Inline.Code(source.substring(startOffset, endOffset).trim())))
        else -> null
    }

    private fun ASTNode.orderedListStart(source: String): Int {
        val firstLine = source.substring(startOffset, endOffset).lineSequence().firstOrNull().orEmpty()
        return Regex("^\\s*(\\d+)[.)]").find(firstLine)?.groupValues?.get(1)?.toIntOrNull() ?: 1
    }

    private fun ASTNode.inlineDescendants(source: String): List<Inline> =
        children.flatMap { it.toInline(source) }.coalesceText()

    private fun ASTNode.inlineChildren(source: String): List<Inline> = inlineDescendants(source)

    private fun ASTNode.toInline(source: String): List<Inline> = when (type) {
        MarkdownTokenTypes.TEXT, MarkdownTokenTypes.WHITE_SPACE ->
            parseHashtags(source.substring(startOffset, endOffset))
        MarkdownElementTypes.STRONG -> listOf(Inline.Bold(inlineDescendants(source)))
        MarkdownElementTypes.EMPH -> listOf(Inline.Emphasis(inlineDescendants(source)))
        GFMElementTypes.STRIKETHROUGH -> listOf(Inline.Strikethrough(inlineDescendants(source)))
        MarkdownElementTypes.CODE_SPAN -> listOf(Inline.Code(codeSpanText(source)))
        else -> if (children.isEmpty()) emptyList() else inlineDescendants(source)
    }

    private fun ASTNode.codeSpanText(source: String): String {
        val raw = source.substring(startOffset, endOffset)
        val fenceLength = raw.takeWhile { it == '`' }.length
        return raw.drop(fenceLength).dropLast(fenceLength).trim()
    }

    private fun ASTNode.toListItem(source: String): ListItem {
        val checkbox = children.firstOrNull { it.type == GFMTokenTypes.CHECK_BOX }
        val checked = checkbox?.let { source.substring(it.startOffset, it.endOffset).contains('x', ignoreCase = true) }
        return ListItem(inlineDescendants(source), checked)
    }

    private fun parseHashtags(value: String): List<Inline> {
        val result = mutableListOf<Inline>()
        var end = 0
        hashtag.findAll(value).forEach { match ->
            if (match.range.first > end) result += Inline.Text(value.substring(end, match.range.first))
            result += Inline.Hashtag(match.value)
            end = match.range.last + 1
        }
        if (end < value.length) result += Inline.Text(value.substring(end))
        return result
    }

    private fun List<Inline>.trimTextWhitespace(): List<Inline> {
        val result = toMutableList()
        while (result.firstOrNull() is Inline.Text) {
            val text = (result.first() as Inline.Text).value.trimStart()
            if (text.isEmpty()) result.removeAt(0) else {
                result[0] = Inline.Text(text)
                break
            }
        }
        while (result.lastOrNull() is Inline.Text) {
            val index = result.lastIndex
            val text = (result[index] as Inline.Text).value.trimEnd()
            if (text.isEmpty()) result.removeAt(index) else {
                result[index] = Inline.Text(text)
                break
            }
        }
        return result
    }

    private fun List<Inline>.coalesceText(): List<Inline> = buildList {
        for (inline in this@coalesceText) {
            val previous = lastOrNull()
            if (previous is Inline.Text && inline is Inline.Text) {
                removeAt(lastIndex)
                add(Inline.Text(previous.value + inline.value))
            } else {
                add(inline)
            }
        }
    }
}
