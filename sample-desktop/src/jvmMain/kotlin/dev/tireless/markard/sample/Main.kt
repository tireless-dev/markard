package dev.tireless.markard.sample

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import dev.tireless.markard.Markard
import dev.tireless.markard.theme.MarkardTheme

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Markard Playground", state = rememberWindowState(width = 1100.dp, height = 720.dp)) {
        MaterialTheme {
            Surface(Modifier.fillMaxSize()) {
                var markdown by remember { mutableStateOf("# Hello, Markard\n\nA **polished** Markdown card.\n\n> Compose-native and shareable.") }
                Row(Modifier.fillMaxSize().padding(24.dp)) {
                    Text(markdown, modifier = Modifier.width(360.dp).padding(16.dp))
                    Markard(markdown, modifier = Modifier.width(520.dp).padding(16.dp), theme = MarkardTheme.Default)
                }
            }
        }
    }
}
