package dev.tireless.markard.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import markard.markard.generated.resources.*
import org.jetbrains.compose.resources.Font as ResourceFont

typealias MarkardDecoration = @Composable BoxScope.() -> Unit

data class MarkardCardTheme(
    val background: Color,
    val aspectRatio: Float? = null,
    val padding: Dp = 0.dp,
    val cornerRadius: Dp = 0.dp,
    val decoration: MarkardDecoration = {},
)

data class MarkardDocumentTheme(
    val alignment: Alignment = Alignment.TopStart,
    val widthFraction: Float = 1f,
    val offset: DpOffset = DpOffset.Zero,
    val blockSpacing: Dp = 14.dp,
)

/** Local font families shipped with Markard. Noto is always the last fallback. */
object MarkardFonts {
    @Composable
    fun notoSansCjk(fallbackOnly: Boolean = false) = androidx.compose.ui.text.font.FontFamily(
        *if (fallbackOnly) {
            arrayOf(ResourceFont(Res.font.noto_sans_cjk_sc, FontWeight.Normal))
        } else {
            arrayOf(
                ResourceFont(Res.font.noto_sans_cjk_sc, FontWeight.Normal),
                ResourceFont(Res.font.noto_sans_cjk_sc, FontWeight.Light),
                ResourceFont(Res.font.noto_sans_cjk_sc, FontWeight.Medium),
                ResourceFont(Res.font.noto_sans_cjk_sc, FontWeight.SemiBold),
                ResourceFont(Res.font.noto_sans_cjk_sc, FontWeight.Bold),
                ResourceFont(Res.font.noto_sans_cjk_sc, FontWeight.ExtraBold),
                ResourceFont(Res.font.noto_sans_cjk_sc, FontWeight.Black),
            )
        },
    )

    @Composable
    fun lxgwWenKai() = androidx.compose.ui.text.font.FontFamily(
        ResourceFont(Res.font.lxgw_wenkai_regular, FontWeight.Normal),
        ResourceFont(Res.font.lxgw_wenkai_regular, FontWeight.Light),
        ResourceFont(Res.font.lxgw_wenkai_regular, FontWeight.Medium),
        ResourceFont(Res.font.lxgw_wenkai_regular, FontWeight.SemiBold),
        ResourceFont(Res.font.lxgw_wenkai_regular, FontWeight.Bold),
        ResourceFont(Res.font.lxgw_wenkai_regular, FontWeight.ExtraBold),
        ResourceFont(Res.font.lxgw_wenkai_regular, FontWeight.Black),
    )

    @Composable
    fun notoSerifCjk() = androidx.compose.ui.text.font.FontFamily(
        ResourceFont(Res.font.noto_serif_cjk_sc, FontWeight.Normal),
        ResourceFont(Res.font.noto_serif_cjk_sc, FontWeight.Light),
        ResourceFont(Res.font.noto_serif_cjk_sc, FontWeight.Medium),
        ResourceFont(Res.font.noto_serif_cjk_sc, FontWeight.SemiBold),
        ResourceFont(Res.font.noto_serif_cjk_sc, FontWeight.Bold),
        ResourceFont(Res.font.noto_serif_cjk_sc, FontWeight.ExtraBold),
        ResourceFont(Res.font.noto_serif_cjk_sc, FontWeight.Black),
    )

    @Composable
    fun sarasaGothic() = androidx.compose.ui.text.font.FontFamily(
        ResourceFont(Res.font.sarasa_gothic_sc_regular, FontWeight.Normal),
        ResourceFont(Res.font.sarasa_gothic_sc_regular, FontWeight.Light),
        ResourceFont(Res.font.sarasa_gothic_sc_regular, FontWeight.Medium),
        ResourceFont(Res.font.sarasa_gothic_sc_regular, FontWeight.SemiBold),
        ResourceFont(Res.font.sarasa_gothic_sc_regular, FontWeight.Bold),
        ResourceFont(Res.font.sarasa_gothic_sc_regular, FontWeight.ExtraBold),
        ResourceFont(Res.font.sarasa_gothic_sc_regular, FontWeight.Black),
    )
}

enum class MarkardFont { NotoSansCjk, LxgwWenKai, NotoSerifCjk, SarasaGothic }

data class MarkardFontTheme(
    val heading: MarkardFont,
    val body: MarkardFont,
    val accent: MarkardFont,
    val highlight: MarkardFont,
)

@Composable
internal fun MarkardFont.resolve(): androidx.compose.ui.text.font.FontFamily = when (this) {
    MarkardFont.NotoSansCjk -> MarkardFonts.notoSansCjk()
    MarkardFont.LxgwWenKai -> androidx.compose.ui.text.font.FontFamily(
        (MarkardFonts.lxgwWenKai() as FontListFontFamily).fonts +
            (MarkardFonts.notoSansCjk(fallbackOnly = true) as FontListFontFamily).fonts,
    )
    MarkardFont.NotoSerifCjk -> androidx.compose.ui.text.font.FontFamily(
        (MarkardFonts.notoSerifCjk() as FontListFontFamily).fonts +
            (MarkardFonts.notoSansCjk(fallbackOnly = true) as FontListFontFamily).fonts,
    )
    MarkardFont.SarasaGothic -> androidx.compose.ui.text.font.FontFamily(
        (MarkardFonts.sarasaGothic() as FontListFontFamily).fonts +
            (MarkardFonts.notoSansCjk(fallbackOnly = true) as FontListFontFamily).fonts,
    )
}

data class MarkardBlockTheme(
    val heading1: TextStyle,
    val heading2: TextStyle,
    val body: TextStyle,
    val quoteIndicator: Color,
    val unorderedListMarker: String = "•",
    val unorderedListMarkerColor: Color,
    val orderedListMarkerBackground: Color,
    val orderedListMarkerForeground: Color,
)

data class MarkardInlineTheme(
    val strong: SpanStyle,
    val emphasis: SpanStyle,
    val code: SpanStyle,
    val accent: SpanStyle,
    val highlight: SpanStyle,
    val highlightStripe: Color,
    val highlightStripeHeightFraction: Float = 0.2f,
    val hashtag: SpanStyle = accent,
    val hashtagMarker: Color = highlightStripe,
    val hashtagMarkerHeightFraction: Float = 0.34f,
    val hashtagWaveAmplitude: Dp = 2.dp,
    val hashtagWaveLength: Dp = 10.dp,
    val spacing: String = "\u2009",
)

data class MarkardTheme(
    val foreground: Color,
    val card: MarkardCardTheme,
    val document: MarkardDocumentTheme,
    val blocks: MarkardBlockTheme,
    val inlines: MarkardInlineTheme,
    val fonts: MarkardFontTheme = MarkardFontTheme(
        heading = MarkardFont.NotoSansCjk,
        body = MarkardFont.NotoSansCjk,
        accent = MarkardFont.NotoSansCjk,
        highlight = MarkardFont.NotoSansCjk,
    ),
) {
    companion object {
        val Default = MarkardTheme(
            foreground = Color(0xFF24211E),
            card = MarkardCardTheme(
                background = Color(0xFFF7F3EA),
                aspectRatio = 3f / 4f,
                padding = 32.dp,
                cornerRadius = 24.dp,
            ),
            document = MarkardDocumentTheme(),
            blocks = MarkardBlockTheme(
                heading1 = TextStyle(fontSize = 34.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold),
                heading2 = TextStyle(fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold),
                body = TextStyle(fontSize = 18.sp, lineHeight = 28.sp),
                quoteIndicator = Color(0xFFB85C38),
                unorderedListMarkerColor = Color(0xFFB85C38),
                orderedListMarkerBackground = Color(0xFFB85C38),
                orderedListMarkerForeground = Color.White,
            ),
            inlines = MarkardInlineTheme(
                strong = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color(0xFF9B4326)),
                emphasis = SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF765D4F)),
                code = SpanStyle(background = Color(0xFFE8DED0)),
                accent = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFB85C38)),
                highlight = SpanStyle(fontWeight = FontWeight.Bold),
                highlightStripe = Color(0xFFE8B98B),
            ),
            fonts = MarkardFontTheme(
                heading = MarkardFont.LxgwWenKai,
                body = MarkardFont.NotoSansCjk,
                accent = MarkardFont.LxgwWenKai,
                highlight = MarkardFont.LxgwWenKai,
            ),
        )

        val Minimal = MarkardTheme(
            foreground = Color(0xFF171717),
            card = MarkardCardTheme(
                background = Color.White,
                aspectRatio = 3f / 4f,
                padding = 28.dp,
                cornerRadius = 8.dp,
            ),
            document = MarkardDocumentTheme(),
            blocks = MarkardBlockTheme(
                heading1 = TextStyle(fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold),
                heading2 = TextStyle(fontSize = 29.sp, lineHeight = 35.sp, fontWeight = FontWeight.Bold),
                body = TextStyle(fontSize = 17.sp, lineHeight = 26.sp),
                quoteIndicator = Color(0xFF4F46E5),
                unorderedListMarkerColor = Color(0xFF4F46E5),
                orderedListMarkerBackground = Color(0xFF4F46E5),
                orderedListMarkerForeground = Color.White,
            ),
            inlines = MarkardInlineTheme(
                strong = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3730A3)),
                emphasis = SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF52525B)),
                code = SpanStyle(background = Color(0xFFF1F1F1)),
                accent = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5)),
                highlight = SpanStyle(fontWeight = FontWeight.Bold),
                highlightStripe = Color(0xFFA5B4FC),
            ),
            fonts = MarkardFontTheme(
                heading = MarkardFont.LxgwWenKai,
                body = MarkardFont.NotoSansCjk,
                accent = MarkardFont.LxgwWenKai,
                highlight = MarkardFont.LxgwWenKai,
            ),
        )

        val Xiaohongshu = MarkardTheme(
            foreground = Color(0xFF30352F),
            card = MarkardCardTheme(
                background = Color(0xFFD8FFD2),
                aspectRatio = 3f / 4f,
                padding = 48.dp,
                decoration = {
                    Text(
                        "“",
                        modifier = Modifier.align(Alignment.TopStart),
                        color = Color(0xFFA9F3A1),
                        fontSize = 104.sp,
                        lineHeight = 88.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .width(34.dp)
                            .height(7.dp)
                            .background(Color(0xFFA9F3A1)),
                    )
                },
            ),
            document = MarkardDocumentTheme(
                alignment = Alignment.CenterStart,
                blockSpacing = 16.dp,
            ),
            blocks = MarkardBlockTheme(
                heading1 = TextStyle(fontSize = 50.sp, lineHeight = 65.sp, fontWeight = FontWeight.SemiBold),
                heading2 = TextStyle(fontSize = 38.sp, lineHeight = 50.sp, fontWeight = FontWeight.SemiBold),
                body = TextStyle(fontSize = 21.sp, lineHeight = 31.sp, fontWeight = FontWeight.Medium),
                quoteIndicator = Color(0xFFFF5B45),
                unorderedListMarkerColor = Color(0xFFFF5B45),
                orderedListMarkerBackground = Color(0xFFFF5B45),
                orderedListMarkerForeground = Color.White,
            ),
            inlines = MarkardInlineTheme(
                strong = SpanStyle(fontWeight = FontWeight.Black, color = Color(0xFF168C5B)),
                emphasis = SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF587852)),
                code = SpanStyle(background = Color(0xFFBDF5B6), color = Color(0xFF30352F)),
                accent = SpanStyle(fontWeight = FontWeight.Black, color = Color(0xFFFF5B45)),
                highlight = SpanStyle(fontWeight = FontWeight.Black),
                highlightStripe = Color(0xFFFFCA63),
                highlightStripeHeightFraction = 0.2f,
                hashtag = SpanStyle(fontWeight = FontWeight.Black, color = Color(0xFF245C35)),
                hashtagMarker = Color(0xFF92EDA0),
                hashtagMarkerHeightFraction = 0.38f,
                hashtagWaveAmplitude = 2.dp,
                hashtagWaveLength = 11.dp,
            ),
            fonts = MarkardFontTheme(
                heading = MarkardFont.LxgwWenKai,
                body = MarkardFont.NotoSansCjk,
                accent = MarkardFont.LxgwWenKai,
                highlight = MarkardFont.LxgwWenKai,
            ),
        )

        val Midnight = MarkardTheme(
            foreground = Color(0xFFF7F1E3),
            card = MarkardCardTheme(
                background = Color(0xFF171A24),
                aspectRatio = 3f / 4f,
                padding = 42.dp,
                decoration = {
                    Box(
                        Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(Color(0xFFFF6B57)),
                    )
                    Text(
                        "NIGHT NOTE",
                        modifier = Modifier.align(Alignment.BottomEnd),
                        color = Color(0xFF68708A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                },
            ),
            document = MarkardDocumentTheme(
                alignment = Alignment.TopStart,
                widthFraction = 0.88f,
                offset = DpOffset(0.dp, 52.dp),
                blockSpacing = 20.dp,
            ),
            blocks = MarkardBlockTheme(
                heading1 = TextStyle(fontSize = 44.sp, lineHeight = 52.sp, fontWeight = FontWeight.Black),
                heading2 = TextStyle(fontSize = 39.sp, lineHeight = 47.sp, fontWeight = FontWeight.Black),
                body = TextStyle(fontSize = 18.sp, lineHeight = 28.sp),
                quoteIndicator = Color(0xFFFF6B57),
                unorderedListMarkerColor = Color(0xFFFF6B57),
                orderedListMarkerBackground = Color(0xFFFF6B57),
                orderedListMarkerForeground = Color(0xFF171A24),
            ),
            inlines = MarkardInlineTheme(
                strong = SpanStyle(fontWeight = FontWeight.Black, color = Color(0xFF78DCE8)),
                emphasis = SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFFA9B1D6)),
                code = SpanStyle(background = Color(0xFF2A2E3D), color = Color(0xFFFFD866)),
                accent = SpanStyle(fontWeight = FontWeight.Black, color = Color(0xFFFF6B57)),
                highlight = SpanStyle(fontWeight = FontWeight.Bold),
                highlightStripe = Color(0xFF8A6DFF),
                highlightStripeHeightFraction = 0.32f,
                hashtag = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF78DCE8)),
                hashtagMarker = Color(0xFF3E5872),
                hashtagMarkerHeightFraction = 0.42f,
                hashtagWaveAmplitude = 1.5.dp,
                hashtagWaveLength = 9.dp,
                spacing = "\u200A",
            ),
            fonts = MarkardFontTheme(
                heading = MarkardFont.NotoSansCjk,
                body = MarkardFont.NotoSansCjk,
                accent = MarkardFont.LxgwWenKai,
                highlight = MarkardFont.LxgwWenKai,
            ),
        )

        /** A diagnostic theme that exercises all bundled font families. */
        val FontShowcase = MarkardTheme(
            foreground = Color(0xFF25211E),
            card = MarkardCardTheme(
                background = Color(0xFFFFF8EC),
                aspectRatio = 3f / 4f,
                padding = 42.dp,
                cornerRadius = 18.dp,
            ),
            document = MarkardDocumentTheme(blockSpacing = 18.dp),
            blocks = MarkardBlockTheme(
                heading1 = TextStyle(fontSize = 42.sp, lineHeight = 52.sp, fontWeight = FontWeight.Bold),
                heading2 = TextStyle(fontSize = 32.sp, lineHeight = 42.sp, fontWeight = FontWeight.SemiBold),
                body = TextStyle(fontSize = 19.sp, lineHeight = 30.sp),
                quoteIndicator = Color(0xFFB56B45),
                unorderedListMarkerColor = Color(0xFFB56B45),
                orderedListMarkerBackground = Color(0xFFB56B45),
                orderedListMarkerForeground = Color.White,
            ),
            inlines = MarkardInlineTheme(
                strong = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF8B452D)),
                emphasis = SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF765D4F)),
                code = SpanStyle(background = Color(0xFFEFE1CE)),
                accent = SpanStyle(fontWeight = FontWeight.SemiBold, color = Color(0xFFB14E35)),
                highlight = SpanStyle(fontWeight = FontWeight.SemiBold),
                highlightStripe = Color(0xFFF0C36A),
            ),
            fonts = MarkardFontTheme(
                heading = MarkardFont.NotoSerifCjk,
                body = MarkardFont.NotoSansCjk,
                accent = MarkardFont.LxgwWenKai,
                highlight = MarkardFont.SarasaGothic,
            ),
        )
    }
}
