package groupassignment.tourshare.gps

import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun updatePosition(youMarker: Marker?,  locationService: Service, mMap: GoogleMap) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(locationService.locationOn.value) {
        scope.launch {
            while (true)
            {
                delay(10 * 1000L)
                val location: Location = locationService.getCurrentLocation()
                youMarker?.position = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),15f))
                Log.v(this.javaClass.name, "Updated user location")
            }
        }
    }

}

fun drawRoute(mMap: GoogleMap, polyLineList: MutableState<List<LatLng>>, locationName: MutableState<String>, currentPos: MutableState<LatLng>) {
    mMap.addPolyline(
        PolylineOptions()
            .color(0xff0000ff.toInt())
            .pattern(listOf(Dash(2f)))
            .addAll(polyLineList.value)
    )
    mMap.addMarker(
        MarkerOptions()
            .title("start of route")
            .snippet("Marker in ${locationName.value}")
            .position(currentPos.value)
    )
}