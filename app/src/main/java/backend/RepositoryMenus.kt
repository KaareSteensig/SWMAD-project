package backend

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import groupassignment.tourshare.LoggedActivity
import groupassignment.tourshare.MainActivity
import groupassignment.tourshare.gps.TrackRoute

data class MenuItemModel(val id: String, val title: String, val iconVector: ImageVector, val contentDescription: String, val activityName:  Class<LoggedActivity>)

class RepositoryMenus {
    private val menus = listOf(
        MenuItemModel("1", "Map", Icons.Default.Home, "Map", TrackRoute::class.java as Class<LoggedActivity>)
        ,
        MenuItemModel("2", "Routes", Icons.Default.LocationOn, "Routes", MainActivity::class.java as Class<LoggedActivity>)
        ,
        MenuItemModel("3", "Pictures", Icons.Default.List, "Pictures", MainActivity::class.java as Class<LoggedActivity>)
        ,
        MenuItemModel("4", "Logout", Icons.Default.AccountBox, "Logout", MainActivity::class.java as Class<LoggedActivity>)
    )
    fun getMenuList(): List<MenuItemModel> {
        Log.v(this::class.simpleName, "Get FriendList")
        return menus.map { MenuItemModel(it.id, it.title, it.iconVector, it.contentDescription, it.activityName) }
    }
}