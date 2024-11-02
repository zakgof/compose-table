package com.zakgof.table.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.zakgof.Table

@Composable
fun App() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        QuickStartExample()
        SpanExample()
        Table (lineWidth = 30.dp) { Row { Text("HELLO") } }
    }
}