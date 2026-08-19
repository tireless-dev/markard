package dev.tireless.markard.parser

import dev.tireless.markard.model.*

object MarkdownParser {
    fun parse(markdown: String): MarkardDocument {
        val lines = markdown.trim().lines()
        val blocks = buildList {
            var index = 0
            while (index < lines.size) {
                val line = lines[index].trimEnd()
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
                    else -> {
                        val paragraph = buildList {
                            while (index < lines.size && lines[index].isNotBlank() &&
                                lines[index].trimStart().let { !it.startsWith("# ") && !it.startsWith("## ") && !it.startsWith("> ") && !it.startsWith("- ") }) {
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

    private fun parseInline(value: String): List<Inline> {
        val result = mutableListOf<Inline>(); var remaining = value
        while (remaining.isNotEmpty()) {
            val match = Regex("(\\*\\*|__|\\*|_|`)").find(remaining)
            if (match == null) { result += Inline.Text(remaining); break }
            if (match.range.first > 0) result += Inline.Text(remaining.substring(0, match.range.first))
            val marker = match.value; val end = remaining.indexOf(marker, match.range.last + 1)
            if (end < 0) { result += Inline.Text(remaining); break }
            val raw = remaining.substring(match.range.last + 1, end)
            result += when (marker) {
                "`" -> Inline.Code(raw)
                "**", "__" -> Inline.Strong(parseInline(raw))
                else -> Inline.Emphasis(parseInline(raw))
            }
            remaining = remaining.substring(end + marker.length)
        }
        return result
    }
}
