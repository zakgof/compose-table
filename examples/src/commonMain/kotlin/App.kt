package com.zakgof.table.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zakgof.Table

@Composable
fun App() {
    Table (
        modifier = Modifier.fillMaxWidth(),
        lineWidth = 1.dp,
        lineColor = Color.DarkGray
    ) {
        Row {
            TextCell(text = "Day", modifier = Modifier.rowSpan(2))
            TextCell(text = "Seminar", modifier = Modifier.columnSpan(3))
        }
        Row {
            TextCell(text = "Begin")
            TextCell(text = "End")
            TextCell(text = "Topic", modifier = Modifier.fillMaxWidth())
        }
        Row {
            TextCell(text = "Monday", modifier = Modifier.rowSpan(2))
            TextCell(text = "8:00", modifier = Modifier.rowSpan(2))
            TextCell(text = "10:00", modifier = Modifier.rowSpan(2))
            TextCell(text = "Java Virtual Machine")
        }
        Row {
            TextCell(text = "Java Programming Language")
        }
        Row {
            TextCell(text = "Tuesday", modifier = Modifier.rowSpan(2))
            TextCell(text = "9:00", modifier = Modifier.rowSpan(2))
            TextCell(text = "12:00", modifier = Modifier.rowSpan(2))
            TextCell(text = "Introduction to Kotlin")
        }
        Row {
            TextCell(text = "Kotlin Multiplatform")
        }
    }
}

@Composable
fun TextCell(text: String, modifier: Modifier = Modifier) = Text (
    text = text,
    modifier = modifier.background(Color.LightGray).padding(4.dp)
)