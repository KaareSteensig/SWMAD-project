package groupassignment.tourshare.gps.routing

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import groupassignment.tourshare.gps.Service
import kotlinx.coroutines.launch

const val TAG_ROUTE = "ROUTE"

@Composable
fun Route(locationService: Service) {
    val polyLineList = remember { mutableStateOf(listOf<LatLng>()) }
    val scope = rememberCoroutineScope()
    val locationName = remember { mutableStateOf("Unknown Location") }
    val currentPos = remember { mutableStateOf(LatLng(1.35, 103.87)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPos.value, 15f)
    }
    LaunchedEffect(locationService.locationOn.value) {
        if (!locationService.locationOn.value) {
            locationService.requestPermission()
        } else {
            val location = locationService.getCurrentLocation()
            currentPos.value = LatLng(location.latitude, location.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(currentPos.value, 15f)
            locationName.value = locationService.getLocationName(location)
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(onClick = {
            scope.launch {
                locationService.startTracking {
                    polyLineList.value = it.map { l ->
                        LatLng(
                            l.latitude,
                            l.longitude
                        )
                    }
                    currentPos.value =
                        LatLng(polyLineList.value[0].latitude, polyLineList.value[0].longitude)
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(currentPos.value, 15f)
                    Log.v(TAG_ROUTE, "Length of locations ${it.size.toString()}")
                }
            }
        }) {
            Text("Start Tracking")
        }
        Button(onClick = {
            locationService.stopTracking()
        }) {
            Text("Pause Tracking")
        }
        Button(onClick = {
            locationService.stopTracking()
        }) {
            Text("Stop Tracking")
        }
        Text("LocationService: ${polyLineList.value}")
        Text("Position: ${currentPos.value}")
        Text("Position: ${cameraPositionState.position}")

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {

            Polyline(
                color = Color.Magenta,
                pattern = listOf(Dash(2f)),
                points = polyLineList.value
            )
            Marker(
                state = MarkerState(position = currentPos.value),
                title = "Location: ${locationName.value}",
                snippet = "Marker in ${locationName.value}"
            )
        }

    }
}