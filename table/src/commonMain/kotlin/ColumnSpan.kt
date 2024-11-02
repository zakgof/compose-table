package com.zakgof

import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.unit.Density

internal data class ColumnSpanParentData(
    var columnSpan: Int? = 1,
)

internal class ModifierColumnSpanImpl(
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

