package com.zakgof.table.examples

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zakgof.Table

@Composable
fun QuickStartExample() {
    Table (lineWidth = 2.dp, lineColor = Color.Blue) {
        Row {
            Text(text = "Cell 1")
            Text(text = "Cell 2")
        }
        Row {
            Text(text = "Long cell", modifier = Modifier.columnSpan(2))
        }
    }
}