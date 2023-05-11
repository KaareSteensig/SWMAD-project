package groupassignment.tourshare.gps

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val TAG_ROUTE = "ROUTE"
class Service(

    private val client: FusedLocationProviderClient,
    private val activity: Activity,
    private val geoCoder: Geocoder

) {
    val locationOn = mutableStateOf(false)
    private val track = mutableListOf<Location>()
    private var doTrack = false
    private var doPause = false

    companion object {
        const val REQUEST_ID = 9
    }

    fun stopTracking() {
        doTrack = false
    }

    fun pauseTracking() {
        doPause = true
    }

    fun resumeTracking() {
        doPause = false
    }

    fun setLocationOn() {
        locationOn.value = true
    }

    fun setLocationOff() {
        locationOn.value = false
    }

    suspend fun startTracking(youMarker: Marker?, routeCallBack: (locations: List<Location>) -> Unit) {
        doTrack = true
        while (doTrack) {
            if (doPause)
            {
                delay(1000L)
                val location: Location = getCurrentLocation()
                youMarker?.position = LatLng(location.latitude,location.longitude)
                Log.v(this.javaClass.name, "Updated user location")
            }
            else {
                delay(1000L)
                track.add(getCurrentLocation())
                Log.v(this.javaClass.name, "tracking location")
                val location: Location = getCurrentLocation()
                youMarker?.position = LatLng(location.latitude,location.longitude)
                Log.v(this.javaClass.name, "Updated user location")
            }
        }
        Log.v(this.javaClass.name, "Done tracking")
        routeCallBack(track)
    }

    suspend fun getCurrentLocation(): Location {
        return suspendCoroutine { continuation ->
            try {
                client.getCurrentLocation(
                    LocationRequest.QUALITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener {
                    continuation.resume(it)
                }.addOnFailureListener {
                    Log.v(this.javaClass.name, "Location request failure")
                }
            } catch (e: SecurityException) {
                Log.v(this.javaClass.name, "Location request not allowed")
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        if (!checkPermission()) {
            locationOn.value = false
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ID
            )
        } else {
            locationOn.value = true
        }
    }

    // newest async getFromLocation needs version sdk 33
    suspend fun getLocationName(location: Location): String {
        return withContext(Dispatchers.IO) {
            val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 10)
            return@withContext if (addresses != null && addresses.isNotEmpty()) {
                "${addresses[0].countryName}, ${addresses[0].locality}"
            } else {
                "UnKnown Location"
            }
        }
    }
}