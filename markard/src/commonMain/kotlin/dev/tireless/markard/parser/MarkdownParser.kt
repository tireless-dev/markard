package dev.tireless.markard.parser

import dev.tireless.markard.model.*

object MarkdownParser {
    private val orderedListItem = Regex("^(\\d+)\\.\\s+(.+)$")
    private val sectionHeading = Regex("^##\\s+.+$")

    fun parse(markdown: String): MarkardDocument {
        val lines = markdown.trim().lines()
        return parseDocument(lines)
    }

    /**
     * Splits a markdown document into card-sized sections.
     *
     * A level-two heading starts a new section and a line containing only
     * `--` separates two sections. Separators are not included in the output.
     * Empty sections are ignored, so leading/trailing or repeated separators
     * are harmless.
     */
    fun parseSections(markdown: String): List<MarkardDocument> = splitSections(markdown)
        .map(::parseDocument)

    internal fun splitSections(markdown: String): List<List<String>> {
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
                line == "--" -> flush()
                else -> current += rawLine
            }
        }
        flush()
        return sections
    }

    private fun parseDocument(lines: List<String>): MarkardDocument {
        val blocks = buildList {
            var index = 0
            while (index < lines.size) {
                val line = lines[index].trim()
                when {
                    line.isBlank() -> index++
                    line.startsWith("# ") || line.startsWith("## ") -> {
                        val level = line.takeWhile { it == '#' }.length.coerceIn(1, 2)
                        add(Block.Heading(level, parseInline(line.drop(level).trim())))
                        index++
                    }
                    line.startsWith("> ") -> { add(Block.Quote(parseInline(line.removePrefix("> ")))); index++ }
                    line.startsWith("- ") -> {
                        val items = buildList {
                            while (index < lines.size && lines[index].trimStart().startsWith("- ")) {
                                add(ListItem(parseInline(lines[index].trimStart().removePrefix("- ")))); index++
                            }
                        }
                        add(Block.UnorderedList(items))
                    }
                    orderedListItem.matches(line) -> {
                        val start = orderedListItem.matchEntire(line)!!.groupValues[1].toInt()
                        val items = buildList {
                            while (index < lines.size) {
                                val match = orderedListItem.matchEntire(lines[index].trim()) ?: break
                                add(ListItem(parseInline(match.groupValues[2])))
                                index++
                            }
                        }
                        add(Block.OrderedList(items, start))
                    }
                    else -> {
                        val paragraph = buildList {
                            while (index < lines.size && lines[index].isNotBlank() && !isBlockStart(lines[index])) {
                                add(lines[index].trim()); index++
                            }
                        }.joinToString(" ")
                        add(Block.Paragraph(parseInline(paragraph)))
                    }
                }
            }
        }
        return MarkardDocument(blocks)
    }

    private fun isBlockStart(value: String): Boolean {
        val line = value.trim()
        return line.startsWith("# ") ||
            line.startsWith("## ") ||
            line.startsWith("> ") ||
            line.startsWith("- ") ||
            orderedListItem.matches(line)
    }

    private fun parseInline(value: String): List<Inline> {
        val result = mutableListOf<Inline>(); var remaining = value
        while (remaining.isNotEmpty()) {
            val match = Regex("(\\*\\*|__|==|\\^\\^|\\*|_|`|#[\\p{L}\\p{N}_-]+)").find(remaining)
            if (match == null) { result += Inline.Text(remaining); break }
            if (match.range.first > 0) result += Inline.Text(remaining.substring(0, match.range.first))
            val marker = match.value
            if (marker.startsWith("#")) {
                result += Inline.Hashtag(marker)
                remaining = remaining.substring(match.range.last + 1)
                continue
            }
            val end = remaining.indexOf(marker, match.range.last + 1)
            if (end < 0) { result += Inline.Text(remaining); break }
            val raw = remaining.substring(match.range.last + 1, end)
            result += when (marker) {
                "`" -> Inline.Code(raw)
                "**", "__" -> Inline.Strong(parseInline(raw))
                "^^" -> Inline.Accent(parseInline(raw))
                "==" -> Inline.Highlight(parseInline(raw))
                else -> Inline.Emphasis(parseInline(raw))
            }
            remaining = remaining.substring(end + marker.length)
        }
        return result
    }
}
