package dev.tireless.markard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import dev.tireless.markard.model.*
import dev.tireless.markard.parser.MarkdownParser
import dev.tireless.markard.theme.MarkardTheme
import dev.tireless.markard.theme.resolve

@Composable
fun Markard(markdown: String, modifier: Modifier = Modifier, theme: MarkardTheme = MarkardTheme.Default) {
    val document = MarkdownParser.parse(markdown)
    val card = theme.card
    val cardModifier = card.aspectRatio?.let { modifier.aspectRatio(it) } ?: modifier

    Box(
        cardModifier
            .clip(RoundedCornerShape(card.cornerRadius))
            .background(card.background)
            .padding(card.padding),
    ) {
        card.decoration(this)
        MarkdownContent(
            document = document,
            theme = theme,
            modifier = Modifier
                .align(theme.document.alignment)
                .fillMaxWidth(theme.document.widthFraction)
                .offset(theme.document.offset.x, theme.document.offset.y),
        )
    }
}

/** Renders one card for every section separated by a level-two heading or `---`. */
@Composable
fun MarkardPages(
    markdown: String,
    modifier: Modifier = Modifier,
    theme: MarkardTheme = MarkardTheme.Default,
) {
    val documents = remember(markdown) { MarkdownParser.parseSections(markdown) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(theme.document.blockSpacing)) {
        documents.forEach { document ->
            MarkardDocumentCard(document, theme)
        }
    }
}

@Composable
private fun MarkardDocumentCard(document: MarkardDocument, theme: MarkardTheme) {
    val card = theme.card
    Box(
        Modifier
            .then(card.aspectRatio?.let { Modifier.aspectRatio(it) } ?: Modifier)
            .clip(RoundedCornerShape(card.cornerRadius))
            .background(card.background)
            .padding(card.padding),
    ) {
        card.decoration(this)
        MarkdownContent(
            document = document,
            theme = theme,
            modifier = Modifier
                .align(theme.document.alignment)
                .fillMaxWidth(theme.document.widthFraction)
                .offset(theme.document.offset.x, theme.document.offset.y),
        )
    }
}

@Composable
private fun MarkdownContent(
    document: MarkardDocument,
    theme: MarkardTheme,
    modifier: Modifier = Modifier,
) {
    val headingFont = theme.fonts.heading.resolve()
    val bodyFont = theme.fonts.body.resolve()
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(theme.document.blockSpacing),
    ) {
        document.blocks.forEach { block ->
            when (block) {
                is Block.Heading -> ThemedInlineText(
                    inlines = block.content,
                    theme = theme,
                    style = (when (block.level) {
                        1 -> theme.blocks.heading1
                        2 -> theme.blocks.heading2
                        3 -> theme.blocks.heading3
                        4 -> theme.blocks.heading4
                        5 -> theme.blocks.heading5
                        else -> theme.blocks.heading6
                    })
                        .copy(color = theme.foreground, fontFamily = headingFont),
                )
                is Block.Paragraph -> ThemedInlineText(
                    inlines = block.content,
                    theme = theme,
                        style = theme.blocks.body.copy(color = theme.foreground, fontFamily = bodyFont),
                )
                Block.HorizontalRule -> Box(
                    Modifier.fillMaxWidth().height(1.dp).background(theme.blocks.quoteIndicator),
                )
                is Block.Quote -> Row {
                    Box(Modifier.width(5.dp).height(28.dp).background(theme.blocks.quoteIndicator, RoundedCornerShape(50)))
                    Spacer(Modifier.width(12.dp))
                    ThemedInlineText(
                        inlines = block.content,
                        theme = theme,
                        style = theme.blocks.body.copy(color = theme.foreground, fontFamily = bodyFont),
                        modifier = Modifier.weight(1f),
                    )
                }
                is Block.UnorderedList -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    block.items.forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(24.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (item.checked != null) {
                                    TaskMarker(item.checked, theme.blocks.unorderedListMarkerColor)
                                } else {
                                    Text(
                                        theme.blocks.unorderedListMarker,
                                        color = theme.blocks.unorderedListMarkerColor,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            ThemedInlineText(
                                inlines = item.content,
                                theme = theme,
                                style = theme.blocks.body.copy(color = theme.foreground, fontFamily = bodyFont),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
                is Block.OrderedList -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    block.items.forEachIndexed { index, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(24.dp).background(theme.blocks.orderedListMarkerBackground, RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (item.checked != null) {
                                    TaskMarker(item.checked, theme.blocks.orderedListMarkerForeground)
                                } else {
                                    Text(
                                        "${block.start + index}",
                                        color = theme.blocks.orderedListMarkerForeground,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            ThemedInlineText(
                                inlines = item.content,
                                theme = theme,
                                style = theme.blocks.body.copy(color = theme.foreground, fontFamily = bodyFont),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskMarker(checked: Boolean, color: Color) {
    Box(
        Modifier
            .size(16.dp)
            .border(1.5.dp, color, RoundedCornerShape(3.dp))
            .drawBehind {
                if (checked) {
                    val side = size.minDimension
                    drawLine(
                        color = color,
                        start = Offset(side * 0.2f, side * 0.52f),
                        end = Offset(side * 0.43f, side * 0.76f),
                        strokeWidth = side * 0.12f,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = color,
                        start = Offset(side * 0.43f, side * 0.76f),
                        end = Offset(side * 0.82f, side * 0.26f),
                        strokeWidth = side * 0.12f,
                        cap = StrokeCap.Round,
                    )
                }
            },
    )
}

internal data class ThemedText(
    val text: AnnotatedString,
    val highlightRanges: List<TextRange>,
    val hashtagRanges: List<TextRange>,
)

@Composable
private fun ThemedInlineText(
    inlines: List<Inline>,
    theme: MarkardTheme,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    val themedText = buildThemedText(
        inlines,
        theme,
        boldFont = theme.fonts.bold.resolve(),
        emphasisFont = theme.fonts.emphasis.resolve(),
    )
    var layoutResult by remember(themedText.text) { mutableStateOf<TextLayoutResult?>(null) }
    val stripeColor = theme.inlines.highlightStripe
    val stripeFraction = theme.inlines.highlightStripeHeightFraction.coerceIn(0f, 1f)
    val hashtagColor = theme.inlines.hashtagMarker
    val hashtagFraction = theme.inlines.hashtagMarkerHeightFraction.coerceIn(0f, 1f)

    Text(
        text = themedText.text,
        modifier = modifier.drawBehind {
            val layout = layoutResult ?: return@drawBehind
            themedText.highlightRanges.forEach { range ->
                layout.boundsByLine(range).forEach { bounds ->
                    val stripeHeight = bounds.height * stripeFraction
                    drawRect(
                        color = stripeColor,
                        topLeft = Offset(bounds.left - 0.5f, bounds.bottom - stripeHeight),
                        size = Size(bounds.width + 1f, stripeHeight),
                    )
                }
            }
            themedText.hashtagRanges.forEach { range ->
                layout.boundsByLine(range).forEach { bounds ->
                    drawMarkerWave(
                        bounds = bounds,
                        color = hashtagColor,
                        heightFraction = hashtagFraction,
                        amplitude = theme.inlines.hashtagWaveAmplitude.toPx(),
                        wavelength = theme.inlines.hashtagWaveLength.toPx(),
                    )
                }
            }
        },
        style = style,
        onTextLayout = { layoutResult = it },
    )
}

internal fun buildThemedText(
    inlines: List<Inline>,
    theme: MarkardTheme,
    boldFont: androidx.compose.ui.text.font.FontFamily? = null,
    emphasisFont: androidx.compose.ui.text.font.FontFamily? = null,
): ThemedText {
    val builder = AnnotatedString.Builder()
    val highlights = mutableListOf<TextRange>()
    val hashtags = mutableListOf<TextRange>()
    appendInlines(builder, highlights, hashtags, inlines, theme, boldFont, emphasisFont)
    return ThemedText(builder.toAnnotatedString(), highlights, hashtags)
}

private fun appendInlines(
    builder: AnnotatedString.Builder,
    highlights: MutableList<TextRange>,
    hashtags: MutableList<TextRange>,
    inlines: List<Inline>,
    theme: MarkardTheme,
    boldFont: androidx.compose.ui.text.font.FontFamily? = null,
    emphasisFont: androidx.compose.ui.text.font.FontFamily? = null,
) {
    inlines.forEach { inline ->
        when (inline) {
            is Inline.Text -> builder.append(inline.value)
            is Inline.Code -> appendStyled(builder, highlights, theme, theme.inlines.code) { builder.append(inline.value) }
            is Inline.Bold -> appendStyled(builder, highlights, theme, theme.inlines.bold.copy(fontFamily = boldFont ?: theme.inlines.bold.fontFamily)) {
                appendInlines(builder, highlights, hashtags, inline.content, theme, boldFont, emphasisFont)
            }
            is Inline.Strikethrough -> appendStyled(builder, highlights, theme, theme.inlines.strikethrough) {
                appendInlines(builder, highlights, hashtags, inline.content, theme, boldFont, emphasisFont)
            }
            is Inline.Emphasis -> appendStyled(builder, highlights, theme, theme.inlines.emphasis.copy(fontFamily = emphasisFont ?: theme.inlines.emphasis.fontFamily), emphasis = true) {
                appendInlines(builder, highlights, hashtags, inline.content, theme, boldFont, emphasisFont)
            }
            is Inline.Hashtag -> {
                builder.append(theme.inlines.spacing)
                val start = builder.length
                builder.pushStyle(theme.inlines.hashtag)
                builder.append(inline.value)
                builder.pop()
                hashtags += TextRange(start, builder.length)
                builder.append(theme.inlines.spacing)
            }
        }
    }
}

private fun TextLayoutResult.boundsByLine(range: TextRange): List<Rect> {
    val result = linkedMapOf<Int, Rect>()
    for (offset in range.start until range.end.coerceAtMost(layoutInput.text.length)) {
        val bounds = getBoundingBox(offset)
        if (bounds.width <= 0f || bounds.height <= 0f) continue
        val line = getLineForOffset(offset)
        val current = result[line]
        result[line] = if (current == null) {
            bounds
        } else {
            Rect(
                left = minOf(current.left, bounds.left),
                top = minOf(current.top, bounds.top),
                right = maxOf(current.right, bounds.right),
                bottom = maxOf(current.bottom, bounds.bottom),
            )
        }
    }
    return result.values.toList()
}

private fun DrawScope.drawMarkerWave(
    bounds: Rect,
    color: androidx.compose.ui.graphics.Color,
    heightFraction: Float,
    amplitude: Float,
    wavelength: Float,
) {
    val left = bounds.left - 1f
    val right = bounds.right + 1f
    val bottom = bounds.bottom
    val top = bottom - bounds.height * heightFraction
    val step = (wavelength / 2f).coerceAtLeast(1f)
    val path = Path().apply {
        moveTo(left, bottom)
        lineTo(left, top)
        var x = left
        var direction = -1f
        while (x < right) {
            x = (x + step).coerceAtMost(right)
            lineTo(x, top + amplitude * direction)
            direction *= -1f
        }
        lineTo(right, bottom)
        close()
    }
    drawPath(path, color)
}

private inline fun appendStyled(
    builder: AnnotatedString.Builder,
    highlights: MutableList<TextRange>,
    theme: MarkardTheme,
    style: SpanStyle,
    emphasis: Boolean = false,
    content: () -> Unit,
) {
    builder.append(theme.inlines.spacing)
    val start = builder.length
    builder.pushStyle(style)
    content()
    builder.pop()
    val end = builder.length
    if (emphasis && end > start) highlights += TextRange(start, end)
    builder.append(theme.inlines.spacing)
}
