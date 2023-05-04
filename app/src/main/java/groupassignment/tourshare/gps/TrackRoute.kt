package groupassignment.tourshare.gps

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import backend.RepositoryMenus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import groupassignment.tourshare.R
import groupassignment.tourshare.menu.MenuItemModel
import groupassignment.tourshare.menu.MenuScaffold
import kotlinx.coroutines.launch
import java.util.*

class TrackRoute: ComponentActivity() {
    private val repository = RepositoryMenus()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geoCoder: Geocoder
    private lateinit var locationService: Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geoCoder = Geocoder(this, Locale.getDefault())
        locationService = Service(fusedLocationClient, this, geoCoder)
        setContent {
            val scope = rememberCoroutineScope()
            val scaffoldState = rememberScaffoldState()
            val navController = rememberNavController()
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                MenuScaffold(
                    "Tracking",
                    repository.getMenuList()
                        .map {
                            MenuItemModel(
                                id = it.id,
                                title = it.title,
                                iconVector = it.iconVector,
                                contentDescription = it.contentDescription
                            ) {
                                val intent =
                                    Intent(this@TrackRoute, it.activityName)
                                startActivity(intent)
                            }
                        },
                    scaffoldState,
                    Color.Red,
                ){
                    Navigation(navController, locationService)
                }
                BottomAppBar() {
                    Row() {
                        Button(onClick = {navController.navigate("Route")}) {
                            Icons.Default.PlayArrow
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.v("TrackRoute", permissions.toString())
        when (requestCode) {
            Service.REQUEST_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TrackRoute", "Permission was granted")
                    locationService.setLocationOn()
                } else {
                    Log.v("TrackRoute", "Permission was denied")
                    locationService.setLocationOff()
                }
                return
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}