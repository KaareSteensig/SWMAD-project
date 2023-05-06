package groupassignment.tourshare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.ActionMenuView
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import backend.RepositoryMenus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import groupassignment.tourshare.Camera.CameraActivity
import groupassignment.tourshare.gps.Service
import groupassignment.tourshare.gps.routing.TAG_ROUTE
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : ComponentActivity(), OnMapReadyCallback  {
    private val repository = RepositoryMenus()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geoCoder: Geocoder
    private lateinit var locationService: Service
    private lateinit var mapview: MapView
    private lateinit var location: Location
    private lateinit var polyLineList: MutableState<List<LatLng>>
    private lateinit var locationName: MutableState<String>
    private lateinit var currentPos: MutableState<LatLng>
    private lateinit var mMap: GoogleMap

    private val Camera_Permission_Code = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geoCoder = Geocoder(this, Locale.getDefault())
        locationService = Service(fusedLocationClient, this, geoCoder)
        setContentView(R.layout.activity_main)

        val openCameraButton: ImageButton = findViewById(R.id.Camera_Button)
        openCameraButton.setOnClickListener{
            //val CameraView = Intent(this@MainActivity, CameraActivity::class.java)
            //startActivity(CameraView)
            if((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED)
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                val CameraView = Intent(this@MainActivity, CameraActivity::class.java)
                startActivity(CameraView)
            }
            else
            {
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, Camera_Permission_Code)
                //ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), Camera_Permission_Code)
                //ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 112)
            }


        }

        val menu: ActionMenuView = findViewById(R.id.Menu_View)

        val openMenuButton: ImageButton = findViewById(R.id.Menu_Button)
        openMenuButton.setOnClickListener{
            //menu.showOverflowMenu()
        }

        mapview = findViewById(R.id.Map_View)

        if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
            && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationService.setLocationOn()
        }
        else {
            val permission = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            requestPermissions(permission, 2)
            locationService.setLocationOn()
            if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            }
        }
        mapview.onCreate(savedInstanceState)

        findViewById<ComposeView>(R.id.my_composable).setContent {
            val scope = rememberCoroutineScope()
            LaunchedEffect(locationService.locationOn.value) {
                scope.launch {
                    location = locationService.getCurrentLocation()
                    mapview.getMapAsync(this@MainActivity)
                }
            }
        }

        val stopButton: ImageButton = findViewById(R.id.Stop_Button)
        stopButton.setOnClickListener{
            locationService.stopTracking()
        }

        val playButton: ImageButton = findViewById(R.id.Play_Button)
        playButton.setOnClickListener{
            findViewById<ComposeView>(R.id.my_composable).setContent {
                polyLineList = remember { mutableStateOf(listOf<LatLng>()) }
                val scope = rememberCoroutineScope()
                locationName = remember { mutableStateOf("Unknown Location") }
                currentPos = remember { mutableStateOf(LatLng(1.35, 103.87)) }
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(currentPos.value, 15f)
                }
                LaunchedEffect(locationService.locationOn.value) {
                    scope.launch {
                        locationService.startTracking {
                            polyLineList.value = it.map { l ->
                                LatLng(
                                    l.latitude,
                                    l.longitude
                                )
                            }
                            currentPos.value =
                                LatLng(
                                    polyLineList.value[0].latitude,
                                    polyLineList.value[0].longitude
                                )
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(currentPos.value, 15f)
                            Log.v(TAG_ROUTE, "Length of locations ${it.size.toString()}")
                            drawRoute(mMap)
                        }
                    }
                }
            }
            /*val track = Intent(this@MainActivity, TrackRoute::class.java)
            startActivity(track)*/
        }

    }
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude,location.longitude))
                .title("Your start position")
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),15f))
        mMap = googleMap
    }
    private fun drawRoute(googleMap: GoogleMap) {
        googleMap.addPolyline(
            PolylineOptions()
                .color(0xff0000ff.toInt())
                .pattern(listOf(Dash(2f)))
                .addAll(polyLineList.value)
        )
        googleMap.addMarker(
            MarkerOptions()
                .title("Location: ${locationName.value}")
                .snippet("Marker in ${locationName.value}")
                .position(currentPos.value)
        )
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

    override fun onStart() {
        super.onStart()
        mapview.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapview.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapview.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapview.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapview.onLowMemory()
    }

}

