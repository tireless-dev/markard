package dev.tireless.markard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.tireless.markard.model.*
import dev.tireless.markard.parser.MarkdownParser
import dev.tireless.markard.theme.MarkardTheme

@Composable
fun Markard(markdown: String, modifier: Modifier = Modifier, theme: MarkardTheme = MarkardTheme.Default) {
    val document = MarkdownParser.parse(markdown)
    Column(modifier.clip(RoundedCornerShape(theme.cornerRadius)).background(theme.background).padding(theme.padding), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        document.blocks.forEach { block ->
            when (block) {
                is Block.Heading -> Text(inlineText(block.content), style = theme.heading.copy(color = theme.foreground, fontWeight = FontWeight.Bold))
                is Block.Paragraph -> Text(inlineText(block.content), style = theme.body.copy(color = theme.foreground))
                is Block.Quote -> Row { Text("│ ", color = theme.accent); Text(inlineText(block.content), style = theme.body.copy(color = theme.foreground, fontStyle = FontStyle.Italic)) }
                is Block.UnorderedList -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { block.items.forEach { item -> Row { Text("• ", color = theme.accent); Text(inlineText(item.content), style = theme.body.copy(color = theme.foreground)) } } }
            }
        }
    }
}

private fun inlineText(inlines: List<Inline>): AnnotatedString = buildAnnotatedString {
    inlines.forEach { inline ->
        when (inline) {
            is Inline.Text -> append(inline.value)
            is Inline.Code -> { pushStyle(SpanStyle(background = androidx.compose.ui.graphics.Color(0xFFE8DED0))); append(inline.value); pop() }
            is Inline.Strong -> { pushStyle(SpanStyle(fontWeight = FontWeight.Bold)); append(inlineText(inline.content)); pop() }
            is Inline.Emphasis -> { pushStyle(SpanStyle(fontStyle = FontStyle.Italic)); append(inlineText(inline.content)); pop() }
        }
    }
}
