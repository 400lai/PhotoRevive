package com.laiiiii.photorevive.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laiiiii.photorevive.R

@Composable
fun NavigationBottomBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Edit, contentDescription = null) },
            label = { Text("修图") },
            selected = selectedItem == R.id.nav_edit,
            onClick = { onItemSelected(R.id.nav_edit) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Face, contentDescription = null) },
            label = { Text("灵感") },
            selected = selectedItem == R.id.nav_inspiration,
            onClick = { onItemSelected(R.id.nav_inspiration) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("我的") },
            selected = selectedItem == R.id.nav_mine,
            onClick = { onItemSelected(R.id.nav_mine) }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun NavigationBottomBarPreview() {
    NavigationBottomBar(
        selectedItem = R.id.nav_edit,
        onItemSelected = {}
    )
}

