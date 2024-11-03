package com.zakgof.table.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OrderTableExample()
        SpanExample()
    }
}