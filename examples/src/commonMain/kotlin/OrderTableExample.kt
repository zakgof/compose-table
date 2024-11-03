package com.zakgof.table.examples

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zakgof.Table
import compose_table.examples.generated.resources.Res
import compose_table.examples.generated.resources.approved
import compose_table.examples.generated.resources.cancel
import org.jetbrains.compose.resources.painterResource

private val orders = listOf(
    Order("200385", true, "Ivermectin", "Tablet: 3 mg"),
    Order("200386", true, "Levamisole", "Tablet: 50 mg"),
    Order("200387", false, "Mebendazole", "Tablet (chewable): 100 mg"),
    Order("200388", true, "Niclosamide", "Tablet (chewable): 500 mg"),
    Order("200389", false, "Praziquantel", "Tablet: 150 mg"),
    Order("200360", true, "Pyrantel", "Tablet (chewable): 250 mg")
)

@Composable
fun OrderTableExample() {
    Table(
        modifier = Modifier.padding(4.dp),
        lineWidth = 1.dp,
        lineColor = Color(0xFFC0C0C0)
    ) {
        Row {
            TextHeader(text = "Order Id")
            TextHeader(text = "Status")
            TextHeader(text = "Name")
            TextHeader(text = "Form")
        }
        orders.forEach {
            Row {
                TextCell( it.id)
                StatusCell(it.available)
                TextCell(it.name)
                TextCell( it.form)
            }
        }

    }
}

private class Order (val id: String, val available: Boolean, val name: String, val form: String)

@Composable
private fun StatusCell(status: Boolean) = Image(
    painter = painterResource(if (status) Res.drawable.approved else Res.drawable.cancel),
    contentDescription = null,
    modifier = Modifier.size(18.dp).padding(8.dp)
)

@Composable
private fun TextHeader(text: String) = Text(
    text = text,
    color = Color.White,
    modifier = Modifier.background(Color(0xFF36304A)).padding(8.dp)
)

@Composable
private fun TextCell(text: String) = Text(
    text = text,
    modifier = Modifier.padding(8.dp)
)