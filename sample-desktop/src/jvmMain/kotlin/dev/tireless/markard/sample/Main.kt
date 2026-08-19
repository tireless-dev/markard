package dev.tireless.markard.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SkiaGraphicsContext
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.tireless.markard.Markard
import dev.tireless.markard.theme.MarkardTheme
import java.awt.FileDialog
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlinx.coroutines.launch

private val PlaygroundBackground = Color(0xFFF6F6F6)
private val PanelBorder = Color(0xFFE8E8E8)
private val XiaohongshuRed = Color(0xFFFF2442)

private data class ThemeOption(val label: String, val theme: MarkardTheme)

private val themeOptions = listOf(
    ThemeOption("小红书", MarkardTheme.Xiaohongshu),
    ThemeOption("Midnight", MarkardTheme.Midnight),
    ThemeOption("Default", MarkardTheme.Default),
    ThemeOption("Minimal", MarkardTheme.Minimal),
    ThemeOption("字体测试", MarkardTheme.FontShowcase),
)

private val sampleMarkdown = """## 提升效率的 3 个小习惯｜亲测有效

1. 早上先完成^^最重要^^的一件事
2. 把大目标拆成 ==30 分钟==的小任务
3. 工作时**关闭**不必要的消息提醒

不需要一下子改变很多

每天进步一点点

坚持下来就会看到变化

#效率提升 #自我管理 #成长记录

> 我是雷一猴"""

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Markard Studio",
        state = rememberWindowState(width = 1180.dp, height = 780.dp),
    ) {
        MaterialTheme {
            var markdown by remember { mutableStateOf(sampleMarkdown) }
            var selectedTheme by remember { mutableStateOf(themeOptions.first()) }

            Surface(Modifier.fillMaxSize(), color = PlaygroundBackground) {
                Row(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Column(Modifier.weight(0.9f).fillMaxHeight()) {
                        ThemeButtonGroup(
                            selected = selectedTheme,
                            onSelected = { selectedTheme = it },
                        )
                        Spacer(Modifier.height(12.dp))
                        EditorPanel(
                            markdown = markdown,
                            onMarkdownChange = { markdown = it },
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        )
                    }
                    PreviewPanel(
                        markdown = markdown,
                        theme = selectedTheme.theme,
                        modifier = Modifier.weight(1.1f).fillMaxHeight(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeButtonGroup(
    selected: ThemeOption,
    onSelected: (ThemeOption) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
    ) {
        themeOptions.forEachIndexed { index, option ->
            if (index > 0) Box(Modifier.fillMaxHeight().width(1.dp).background(PanelBorder))
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (option == selected) Color(0xFF242424) else Color.Transparent)
                    .clickable { onSelected(option) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    option.label,
                    color = if (option == selected) Color.White else Color(0xFF666666),
                    fontSize = 12.sp,
                    fontWeight = if (option == selected) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun EditorPanel(
    markdown: String,
    onMarkdownChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) = BasicTextField(
    value = markdown,
    onValueChange = onMarkdownChange,
    modifier = modifier
        .background(Color.White, RoundedCornerShape(18.dp))
        .border(1.dp, PanelBorder, RoundedCornerShape(18.dp))
        .padding(24.dp),
    textStyle = TextStyle(color = Color(0xFF333333), fontSize = 17.sp, lineHeight = 28.sp),
    cursorBrush = SolidColor(XiaohongshuRed),
    decorationBox = { innerTextField ->
        Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) { innerTextField() }
    },
)

@OptIn(InternalComposeUiApi::class)
@Composable
private fun PreviewPanel(
    markdown: String,
    theme: MarkardTheme,
    modifier: Modifier = Modifier,
) {
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val graphicsContext = remember { SkiaGraphicsContext() }
    val graphicsLayer = remember { graphicsContext.createGraphicsLayer() }

    DisposableEffect(Unit) {
        onDispose {
            graphicsContext.releaseGraphicsLayer(graphicsLayer)
            graphicsContext.dispose()
        }
    }

    Column(modifier) {
        Row(
            Modifier.fillMaxWidth().height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            message?.let {
                Text(it, color = Color(0xFF777777), fontSize = 12.sp)
                Spacer(Modifier.width(12.dp))
            }
            PreviewActionButton("复制图片") {
                scope.launch {
                    runCatching {
                        copyImageToClipboard(graphicsLayer.toImageBitmap().toBufferedImage())
                    }.onSuccess {
                        message = "已复制"
                    }.onFailure {
                        message = "复制失败"
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            PreviewActionButton("导出 PNG") {
                scope.launch {
                    runCatching {
                        exportImage(graphicsLayer.toImageBitmap().toBufferedImage())
                    }.onSuccess { exported ->
                        if (exported) message = "已导出"
                    }
                }
            }
        }
        BoxWithConstraints(
            Modifier.weight(1f).fillMaxWidth()
                .background(Color(0xFFE3E4E6), RoundedCornerShape(18.dp)).padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            val aspectRatio = theme.card.aspectRatio ?: (3f / 4f)
            val cardWidth = minOf(maxWidth, maxHeight * aspectRatio)
            Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), contentAlignment = Alignment.TopCenter) {
                Markard(
                    markdown,
                    modifier = Modifier.width(cardWidth).drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    },
                    theme = theme,
                )
            }
        }
    }
}

@Composable
private fun PreviewActionButton(label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(Color.White)
            .border(1.dp, PanelBorder, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color(0xFF555555), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

private fun ImageBitmap.toBufferedImage(): BufferedImage = BufferedImage(
    width,
    height,
    BufferedImage.TYPE_INT_ARGB,
).also { image ->
    val pixels = IntArray(width * height)
    readPixels(pixels)
    image.setRGB(0, 0, width, height, pixels, 0, width)
}

private fun copyImageToClipboard(image: BufferedImage) {
    val transferable = object : Transferable {
        override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
        override fun isDataFlavorSupported(flavor: DataFlavor) = flavor == DataFlavor.imageFlavor
        override fun getTransferData(flavor: DataFlavor): Any {
            if (!isDataFlavorSupported(flavor)) throw java.awt.datatransfer.UnsupportedFlavorException(flavor)
            return image
        }
    }
    Toolkit.getDefaultToolkit().systemClipboard.setContents(transferable, null)
}

private fun exportImage(image: BufferedImage): Boolean = runCatching {
    val owner = java.awt.Window.getWindows().firstOrNull { it.isActive && it.isShowing }
    val dialog = FileDialog(owner as? java.awt.Frame, "导出预览图片", FileDialog.SAVE).apply {
        file = "markard-card.png"
        isVisible = true
    }
    val selectedFile = dialog.file?.let { if (it.endsWith(".png", ignoreCase = true)) it else "$it.png" }
        ?: return false
    val selectedDirectory = dialog.directory ?: return false
    ImageIO.write(image, "png", java.io.File(selectedDirectory, selectedFile))
}.getOrDefault(false)
