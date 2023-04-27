package groupassignment.tourshare.menu

import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerHeader(title: String, background: Color) {
    Box(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 25.sp
        )
    }
}

@Preview
@Composable
fun DrawerHeadPreview() {
    DrawerHeader(title = "TestHeader", Color.Blue)
}

@Composable
fun DrawerBody(menuItems:List<MenuItemModel>) {
    val menus = LazyColumn {
        items(1) {
            for (menuItem in menuItems) {
                MenuItem(menuItem = menuItem)
            }
        }
    }
}

@Preview
@Composable
fun DrawerBodyPreview() {
    DrawerBody(
        listOf(
            MenuItemModel(
                "1",
                "Home",
                Icons.Default.Home,
                "Home")
            { println("clicked home") }
            ,
            MenuItemModel(
                "1",
                "Settings",
                Icons.Default.Settings,
                "Settings")
            { println("clicked settings") }
        )
    )
}

@Composable
fun Drawer(title: String, menuItems: List<MenuItemModel>, background: Color) {
    Column {
        DrawerHeader(title = title, background = background)
        DrawerBody(menuItems = menuItems)
    }
}

@Preview
@Composable
fun DrawerPreview() {
    Drawer(
        title = "TestHeader",
        menuItems = listOf(
            MenuItemModel(
                "1",
                "Home",
                Icons.Default.Home,
                "Home")
            { println("clicked home") }
            ,
            MenuItemModel(
                "1",
                "Settings",
                Icons.Default.Settings,
                "Settings")
            { println("clicked settings") }
        ),
        background = Color.Blue
    )
}