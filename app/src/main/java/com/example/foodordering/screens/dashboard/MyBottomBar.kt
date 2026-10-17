package com.example.foodordering.screens.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodordering.R

@Composable
fun MyBottomBar(
    selectedTab: String = "Home",
    onTabSelected: (String) -> Unit = {}
) {
    val bottomMenuItemsList = prepareBottomMenu()

    NavigationBar(
        containerColor = colorResource(id = R.color.black3),
        tonalElevation = 3.dp
    ) {
        bottomMenuItemsList.forEach { bottomMenuItem ->
            NavigationBarItem(
                selected = (selectedTab == bottomMenuItem.label),
                onClick = { onTabSelected(bottomMenuItem.label) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colorResource(id = R.color.orange).copy(alpha = 0.2f)
                ),
                icon = {
                    Icon(
                        painter = bottomMenuItem.icon,
                        contentDescription = bottomMenuItem.label,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(24.dp),
                        tint = Color.Unspecified
                    )
                }
            )
        }
    }
}

data class BottomMenuItem(
    val label: String,
    val icon: Painter
)

@Composable
fun prepareBottomMenu(): List<BottomMenuItem> {
    return listOf(
        BottomMenuItem(
            label = "Home",
            icon = painterResource(id = R.drawable.btn_1)
        ),
        BottomMenuItem(
            label = "Cart",
            icon = painterResource(id = R.drawable.btn_2)
        ),
        BottomMenuItem(
            label = "Favorite",
            icon = painterResource(id = R.drawable.btn_3)
        ),
        BottomMenuItem(
            label = "Order",
            icon = painterResource(id = R.drawable.btn_4)
        ),
        BottomMenuItem(
            label = "Profile",
            icon = painterResource(id = R.drawable.btn_5)
        )
    )
}

@Preview
@Composable
fun MyBottomBarPreview() {
    MyBottomBar()
}
