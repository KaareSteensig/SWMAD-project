package groupassignment.tourshare.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class MenuItemModel(
    val id: String,
    val title: String,
    val iconVector: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)

@Composable
fun MenuItem(menuItem: MenuItemModel){
    Row(modifier = Modifier.clickable(onClick = menuItem.onClick )
        .height(40.dp)
        .background(color = Color.Red)
        .border(1.dp,Color.Black)
    ) {

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add a horizontal space between the image and the column
            Text(text = menuItem.title)

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = menuItem.iconVector,
                contentDescription = menuItem.contentDescription
            )
        }
    }
}

@Preview
@Composable
fun MenuItemPreview() {
    //ScaffoldAppTheme(darktheme = true) {
    MenuItem(
        MenuItemModel("1", "Home", Icons.Default.Home, "Home")
        { println("click") }
    )
    //}
}