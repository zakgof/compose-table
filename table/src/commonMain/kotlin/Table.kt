package com.zakgof

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Table(
    modifier: Modifier = Modifier,
    lineWidth: Dp = 1.dp,
    lineColor: Color = Color.DarkGray,
    content: TableScope.() -> Unit
) {
    var horizontalLines by remember { mutableStateOf<Map<Int, Map<Int, Int>>>(mapOf()) }
    var verticalLines by remember { mutableStateOf<Map<Int, Map<Int, Int>>>(mapOf()) }
    SubcomposeLayout(modifier = modifier.drawBehind {
        horizontalLines.forEach { entry ->
            entry.value.forEach {
                drawLine(
                    color = lineColor,
                    strokeWidth = lineWidth.toPx(),
                    start = Offset(
                        it.key.toFloat() - 0.5f * lineWidth.toPx(),
                        entry.key.toFloat() - 0.5f * lineWidth.toPx()
                    ),
                    end = Offset(
                        it.value.toFloat() - 0.5f * lineWidth.toPx(),
                        entry.key.toFloat() - 0.5f * lineWidth.toPx()
                    )
                )
            }
        }
        verticalLines.forEach { entry ->
            entry.value.forEach {
                drawLine(
                    color = lineColor,
                    strokeWidth = lineWidth.toPx(),
                    start = Offset(
                        entry.key.toFloat() - 0.5f * lineWidth.toPx(),
                        it.key.toFloat() - 0.5f * lineWidth.toPx()
                    ),
                    end = Offset(
                        entry.key.toFloat() - 0.5f * lineWidth.toPx(),
                        it.value.toFloat() - 0.5f * lineWidth.toPx()
                    )
                )
            }
        }

    }) { constraints ->

        val rows = mutableListOf<@Composable TableRowScope.() -> Unit>()
        content(object : TableScope {
            override fun Row(rowContent: @Composable TableRowScope.() -> Unit) {
                rows += rowContent
            }
        })
        val cells = mutableListOf<Cell>()
        val chessBoard = mutableSetOf<Pair<Int, Int>>()
        val noxline = mutableSetOf<Pair<Int, Int>>()
        val noyline = mutableSetOf<Pair<Int, Int>>()
        var x = 0
        var y = 0
        rows.forEachIndexed { rowIndex, row ->
            val firstScan = subcompose(rowIndex to 0) { TableRowScopeImpl.row() }.map {
                it.measure(Constraints())
            }
            subcompose(rowIndex to 1) { TableRowScopeImpl.row() }.forEachIndexed { index, it ->
                val min = firstScan[index]
                val max = it.measure(
                    Constraints(
                        maxWidth = constraints.maxWidth,
                        maxHeight = constraints.maxHeight
                    )
                )
                val columnSpan = (it.parentData as? ColumnSpanParentData)?.columnSpan ?: 1
                val rowSpan = (it.parentData as? RowSpanParentData)?.rowSpan ?: 1

                while (chessBoard.contains(x to y)) {
                    x++
                }
                cells.add(Cell(min, max, x, x + columnSpan - 1, y, y + rowSpan - 1))
                for (xx in x until x + columnSpan) {
                    for (yy in y until y + rowSpan) {
                        chessBoard.add(xx to yy)
                    }
                }
                for (xx in x until x + columnSpan) {
                    for (yy in y + 1 until y + rowSpan) {
                        noxline.add(xx to yy)
                    }
                }
                for (xx in x + 1 until x + columnSpan) {
                    for (yy in y until y + rowSpan) {
                        noyline.add(yy to xx)
                    }
                }
                x += columnSpan
            }
            y++
            x = 0
        }

        val mins = measureCells(cells, lineWidth.value.toInt()) { it.min }
        val maxs = measureCells(cells, lineWidth.value.toInt()) { it.max }

        val actualTotalWidth = mins.first.sum()
        val desiredTotalWidth = maxs.first.sum()

        val effectiveAvailableWidth = constraints.maxWidth - (mins.first.size + 1) * lineWidth.value.toInt()

        val finalWidths =
            if (actualTotalWidth > effectiveAvailableWidth) {
                mins.first.map { it * effectiveAvailableWidth / actualTotalWidth }
            } else if (desiredTotalWidth > effectiveAvailableWidth) {
                val deficit = desiredTotalWidth - effectiveAvailableWidth
                val space = desiredTotalWidth - actualTotalWidth
                maxs.first.mapIndexed { index, it ->
                    it - (it - mins.first[index]) * deficit / space
                }
            } else maxs.first

        val actualTotalHeight = mins.second.sum()
        val desiredTotalHeight = maxs.second.sum()

        val effectiveAvailableHeight = constraints.maxHeight - (mins.second.size + 1) * lineWidth.value.toInt()

        val finalHeights =
            if (actualTotalHeight > effectiveAvailableHeight) {
                mins.second.map { it * effectiveAvailableHeight / actualTotalHeight }
            } else if (desiredTotalHeight > effectiveAvailableHeight) {
                val deficit = desiredTotalHeight - effectiveAvailableHeight
                val space = desiredTotalHeight - actualTotalHeight
                maxs.second.mapIndexed { index, it ->
                    it - (it - mins.second[index]) * deficit / space
                }
            } else maxs.second

        var i = 0
        val triples = rows.flatMapIndexed { rowIndex, row ->
            subcompose(rowIndex to 2) { TableRowScopeImpl.row() }.map { measurable ->
                val cell = cells[i++]
                val width = (cell.columnStart..cell.columnEnd).sumOf { finalWidths[it] } +
                        (cell.columnEnd - cell.columnStart) * lineWidth.value.toInt()
                val height = (cell.rowStart..cell.rowEnd).sumOf { finalHeights[it] } +
                        (cell.rowEnd - cell.rowStart) * lineWidth.value.toInt()
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = width,
                        minHeight = height,
                        maxWidth = width,
                        maxHeight = height
                    )
                )
                Triple(placeable, cell.columnStart, cell.rowStart)
            }
        }

        val accumWidths = finalWidths.accumulated(lineWidth.value.toInt()) // TODO: all floatz
        val accumHeights = finalHeights.accumulated(lineWidth.value.toInt()) // TODO: all floatz

        horizontalLines = calculateLines(accumWidths, accumHeights, noxline)
        verticalLines = calculateLines(accumHeights, accumWidths, noyline)

        layout(
            width = accumWidths.last() + lineWidth.value.toInt(),
            height = accumHeights.last() + lineWidth.value.toInt()
        ) {
            triples.map {
                it.first.placeRelative(accumWidths[it.second], accumHeights[it.third])
            }
        }
    }
}

fun calculateLines(
    accumWidths: List<Int>,
    accumHeights: List<Int>,
    noxline: Set<Pair<Int, Int>>
): Map<Int, Map<Int, Int>> {
    return accumHeights.mapIndexed { yy, y ->
        val m = mutableMapOf<Int, Int>()
        var startIndex = 0
        while (startIndex < accumWidths.size) {
            while (noxline.contains(startIndex to yy) && startIndex < accumWidths.size - 1) {
                startIndex++
            }
            var endIndex = startIndex
            while (!noxline.contains(endIndex to yy) && endIndex < accumWidths.size - 1) {
                endIndex++
            }
            m[accumWidths[startIndex]] = accumWidths[endIndex]
            startIndex = endIndex + 1
        }
        y to m
    }.associate { it }
}

private fun measureCells(
    cells: List<Cell>,
    lineWidth: Int,
    getter: (Cell) -> Placeable,
): Pair<List<Int>, List<Int>> {
    val columnWidths = mutableMapOf<Int, Int>()
    val rowHeights = mutableMapOf<Int, Int>()
    cells.forEach { cell ->
        val placeable = getter(cell)
        val currentWidth = (cell.columnStart..cell.columnEnd).sumOf {
            columnWidths[it] ?: 0
        } + lineWidth * (cell.columnEnd - cell.columnStart)
        val currentHeight = (cell.rowStart..cell.rowEnd).sumOf {
            rowHeights[it] ?: 0
        } + lineWidth * (cell.rowEnd - cell.rowStart)
        if (placeable.width > currentWidth) {
            val surplus = (placeable.width - currentWidth) / (cell.columnEnd - cell.columnStart + 1)
            (cell.columnStart..cell.columnEnd).forEach {
                columnWidths[it] = (columnWidths[it] ?: 0) + surplus
            }
        }
        if (placeable.height > currentHeight) {
            val surplus = (placeable.height - currentHeight) / (cell.rowEnd - cell.rowStart + 1)
            (cell.rowStart..cell.rowEnd).forEach {
                rowHeights[it] = (rowHeights[it] ?: 0) + surplus
            }
        }
    }
    val widthsList = (0 until columnWidths.size).map { columnWidths[it]!! }
    val heightsList = (0 until rowHeights.size).map { rowHeights[it]!! }
    return widthsList to heightsList
}

private fun List<Int>.accumulated(width: Int) =
    fold(listOf(width)) { acc, el -> acc + (acc.last() + el + width) }

private class Cell(
    val min: Placeable, val max: Placeable,
    val columnStart: Int, val columnEnd: Int,
    val rowStart: Int, val rowEnd: Int
)

interface TableScope {
    fun Row(rowContent: @Composable TableRowScope.() -> Unit)
}

interface TableRowScope {
    fun Modifier.columnSpan(columns: Int): Modifier
    fun Modifier.rowSpan(rows: Int): Modifier
}

private object TableRowScopeImpl : TableRowScope {

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

private data class ColumnSpanParentData(
    var columnSpan: Int? = 1,
)

private data class RowSpanParentData(
    var rowSpan: Int? = 1,
)

private class ModifierColumnSpanImpl(
    val value: Int,
    inspectorInfo: InspectorInfo.() -> Unit
) : ParentDataModifier, InspectorValueInfo(inspectorInfo) {

    override fun Density.modifyParentData(parentData: Any?) =
        ((parentData as? ColumnSpanParentData) ?: ColumnSpanParentData()).also {
            it.columnSpan = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModifierColumnSpanImpl) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString(): String = "ModifierColumnSpanImpl(value=$value)"
}

private class ModifierRowSpanImpl(
    val value: Int,
    inspectorInfo: InspectorInfo.() -> Unit
) : ParentDataModifier, InspectorValueInfo(inspectorInfo) {

    override fun Density.modifyParentData(parentData: Any?) =
        ((parentData as? RowSpanParentData) ?: RowSpanParentData()).also {
            it.rowSpan = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModifierRowSpanImpl) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 17 * result + value.hashCode()
        return result
    }

    override fun toString(): String = "ModifierRowSpanImpl(value=$value)"
}