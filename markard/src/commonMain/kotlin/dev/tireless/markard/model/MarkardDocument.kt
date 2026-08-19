package dev.tireless.markard.model

data class MarkardDocument(val blocks: List<Block>)
sealed interface Block {
    data class Heading(val level: Int, val content: List<Inline>) : Block
    data class Paragraph(val content: List<Inline>) : Block
    data class Quote(val content: List<Inline>) : Block
    data class UnorderedList(val items: List<ListItem>) : Block
}
data class ListItem(val content: List<Inline>)
sealed interface Inline {
    data class Text(val value: String) : Inline
    data class Strong(val content: List<Inline>) : Inline
    data class Emphasis(val content: List<Inline>) : Inline
    data class Code(val value: String) : Inline
}
