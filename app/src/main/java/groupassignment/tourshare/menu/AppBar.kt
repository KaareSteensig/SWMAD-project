package groupassignment.tourshare.menu

<<<<<<< HEAD
@Composable

=======
import android.util.Log
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppBar(title: String, icon: ImageVector = Icons.Default.Menu, onMenu: () -> Unit) {
    //var scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = onMenu) {
                Icon(imageVector = icon, contentDescription = "Menu")
            }
        },
        backgroundColor = Color.Red,
        contentColor = Color.White,
        elevation = 8.dp
    )

}

@Preview
@Composable
fun AppBarPreview() {
    AppBar(title = "test appbar") {
        Log.v("AppBarPreview", "Icon clicked")
    }
}
>>>>>>> 926fb4e561afbbece4982918819b4c8974a5a723
