package groupassignment.tourshare.menu

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@Composable
fun MenuScaffold(title: String, menuItems:List<MenuItemModel>, scaffoldState: ScaffoldState, background: Color, content: @Composable (PaddingValues) -> Unit) {
    //val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(title = title) {
                scope.launch {
                    Log.v("Scaffold", "open scaffold")
                    scaffoldState.drawerState.open()
                }
            }
        }, content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                // .background(Color(0xff8d6e63))
                ,
                verticalArrangement = Arrangement.Top
            ) {
                content(it)
            }
        },
        drawerContent = {
            Drawer(title = "Menus", menuItems = menuItems, background = background)
        },
        drawerBackgroundColor = background
    )
}

/*@Preview
@Composable
fun MenuScaffoldPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    MenuScaffold(
        title = "Menu",
        listOf(
            MenuItemModel(
                "1",
                "Home",
                Icons.Default.Home,
                "Home")
            {
                scope.launch {
                    println("clicked home")
                    scaffoldState.drawerState.close()
                }
            }
            ,
            MenuItemModel(
                "1",
                "Settings",
                Icons.Default.Settings,
                "Settings")
            {
                scope.launch {
                    println("clicked settings")
                    scaffoldState.drawerState.close()
                }
            }
        ),
        scaffoldState,
        Color.Blue,

    )
}*/