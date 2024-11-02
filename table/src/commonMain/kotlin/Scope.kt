package com.zakgof

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.debugInspectorInfo


interface TableScope {
    fun Row(rowContent: @Composable TableRowScope.() -> Unit)
}

interface TableRowScope {
    fun Modifier.columnSpan(columns: Int): Modifier
    fun Modifier.rowSpan(rows: Int): Modifier
}

internal object TableRowScopeImpl : TableRowScope {

    override fun Modifier.columnSpan(columns: Int): Modifier {
        require(columns > 0) { "invalid columnSpan $columns; must be greater than zero" }
        return this.then(
            ModifierColumnSpanImpl(
                value = columns,
                inspectorInfo = debugInspectorInfo {
                    name = "columnSpan"
                    this.value = columns
                    properties["value"] = columns
                }
            )
        )
    }

    override fun Modifier.rowSpan(rows: Int): Modifier {
        require(rows > 0) { "invalid rowSpan $rows; must be greater than zero" }
        return this.then(
            ModifierRowSpanImpl(
                value = rows,
                inspectorInfo = debugInspectorInfo {
                    name = "rowSpan"
                    this.value = rows
                    properties["value"] = rows
                }
            )
        )
    }
}