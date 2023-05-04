package groupassignment.tourshare.gps

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import groupassignment.tourshare.gps.routing.Route

@Composable
fun Navigation(navController: NavHostController, locationService: Service) {
    NavHost(navController = navController, startDestination = "Home") {
        composable("Route") {
            Route(locationService)
        }
    }
}
