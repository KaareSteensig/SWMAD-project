package backend

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import groupassignment.tourshare.Camera.CameraActivity
import groupassignment.tourshare.LoggedActivity
import groupassignment.tourshare.MainActivity
import groupassignment.tourshare.firebase.Login

data class MenuItemModel(val id: String, val title: String, val iconVector: ImageVector, val contentDescription: String, val activityName:  Class<ComponentActivity>)

class RepositoryMenus {
    private val menus = listOf(
        MenuItemModel("1", "Map", Icons.Default.Home, "Map", MainActivity::class.java as Class<ComponentActivity>)
        ,
        MenuItemModel("2", "Routes", Icons.Default.LocationOn, "Routes", MainActivity::class.java as Class<ComponentActivity>)
        ,
        MenuItemModel("3", "Pictures", Icons.Default.List, "Pictures", CameraActivity::class.java as Class<ComponentActivity>)
        ,
        MenuItemModel("4", "Logout", Icons.Default.AccountBox, "Logout", Login::class.java as Class<ComponentActivity>)
    )
    fun getMenuList(): List<MenuItemModel> {
        Log.v(this::class.simpleName, "Get FriendList")
        return menus.map { MenuItemModel(it.id, it.title, it.iconVector, it.contentDescription, it.activityName) }
    }
}