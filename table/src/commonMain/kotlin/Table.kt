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
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

@Composable
fun Table(
    modifier: Modifier = Modifier,
    lineWidth: Dp = 1.dp,
    lineColor: Color = Color.DarkGray,
    content: TableScope.() -> Unit
) {
    var horizontalLines by remember { mutableStateOf<Map<Float, Map<Float, Float>>>(mapOf()) }
    var verticalLines by remember { mutableStateOf<Map<Float, Map<Float, Float>>>(mapOf()) }

    SubcomposeLayout(modifier = modifier.drawBehind {

        val lineWidthFloatPix: Float = LocalDensity.run { lineWidth.toPx() }
        horizontalLines.forEach { entry ->
            entry.value.forEach {
                drawLine(
                    color = lineColor,
                    strokeWidth = lineWidthFloatPix,
                    start = Offset(it.key, entry.key),
                    end = Offset(it.value, entry.key)
                )
            }
        }
        verticalLines.forEach { entry ->
            entry.value.forEach {
                drawLine(
                    color = lineColor,
                    strokeWidth = lineWidth.toPx(),
                    start = Offset(entry.key, it.key),
                    end = Offset(entry.key, it.value)
                )
            }
        }

    }) { constraints ->

        val lineWidthFloatPix: Float = LocalDensity.run { lineWidth.toPx() }
        val lineWidthPix = ceil(lineWidthFloatPix).toInt()

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

        val mins = measureCells(cells, lineWidthPix) { it.min }
        val maxs = measureCells(cells, lineWidthPix) { it.max }

        val actualTotalWidth = mins.first.sum()
        val desiredTotalWidth = maxs.first.sum()

        val columnCount = chessBoard.maxOf { it.first } + 1
        val rowCount = chessBoard.maxOf { it.second } + 1

        val effectiveAvailableWidth = constraints.maxWidth - (columnCount + 1) * lineWidthPix

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

        val effectiveAvailableHeight = constraints.maxHeight - (rowCount + 1) * lineWidthPix

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
                        (cell.columnEnd - cell.columnStart) * lineWidthPix
                val height = (cell.rowStart..cell.rowEnd).sumOf { finalHeights[it] } +
                        (cell.rowEnd - cell.rowStart) * lineWidthPix
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

        val accumWidths = finalWidths.accumulated(lineWidthPix)
        val accumHeights = finalHeights.accumulated(lineWidthPix)

        horizontalLines = calculateLines(accumWidths, accumHeights, noxline, lineWidthFloatPix)
        verticalLines = calculateLines(accumHeights, accumWidths, noyline, lineWidthFloatPix)

        layout(
            width = accumWidths.last(),
            height = accumHeights.last()
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
    noxline: Set<Pair<Int, Int>>,
    lineWidthFloatPix: Float
): Map<Float, Map<Float, Float>> {
    return accumHeights.mapIndexed { yy, y ->
        val m = mutableMapOf<Float, Float>()
        var startIndex = 0
        while (startIndex < accumWidths.size) {
            while (noxline.contains(startIndex to yy) && startIndex < accumWidths.size - 1) {
                startIndex++
            }
            var endIndex = startIndex
            while (!noxline.contains(endIndex to yy) && endIndex < accumWidths.size - 1) {
                endIndex++
            }
            val so = if (startIndex == 0) -lineWidthFloatPix else -lineWidthFloatPix * 0.5f
            val eo = if (endIndex == accumWidths.size - 1) 0f else -lineWidthFloatPix * 0.5f
            m[accumWidths[startIndex].toFloat() + so] = accumWidths[endIndex].toFloat() + eo
            startIndex = endIndex + 1
        }
        y.toFloat()-lineWidthFloatPix * 0.5f to m
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
