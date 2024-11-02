package com.zakgof.table.examples

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Table Demo"
    ) {
        MaterialTheme(colors = darkColors()) {
            App()
        }
    }
}