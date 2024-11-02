package com.zakgof.table.examples

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.zakgof.Table

@Composable
fun QuickStartExample() {
    Table {
        Row {
            Text(text = "1")
            Text(text = "John Doe")
            Text(text = "johndoe@somemail.com")
        }
        Row {
            Text(text = "2")
            Text(text = "Jane Doe")
            Text(text = "janedoe@somemail.com")
        }
    }
}