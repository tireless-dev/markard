package dev.tireless.markard.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MarkardTheme(val background: Color, val foreground: Color, val accent: Color, val padding: Dp, val cornerRadius: Dp, val body: TextStyle, val heading: TextStyle, val codeBackground: Color) {
    companion object {
        val Default = MarkardTheme(Color(0xFFF7F3EA), Color(0xFF24211E), Color(0xFFB85C38), 32.dp, 24.dp, TextStyle(fontSize = 18.sp, lineHeight = 28.sp), TextStyle(fontSize = 34.sp, lineHeight = 40.sp), Color(0xFFE8DED0))
        val Minimal = MarkardTheme(Color.White, Color(0xFF171717), Color(0xFF4F46E5), 28.dp, 8.dp, TextStyle(fontSize = 17.sp, lineHeight = 26.sp), TextStyle(fontSize = 32.sp, lineHeight = 38.sp), Color(0xFFF1F1F1))
    }
}
