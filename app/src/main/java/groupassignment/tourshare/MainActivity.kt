package groupassignment.tourshare

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import groupassignment.tourshare.Camera.CameraActivity
import groupassignment.tourshare.gps.Service
import groupassignment.tourshare.gps.routing.TAG_ROUTE
import kotlinx.coroutines.delay
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
    private var youMarker: Marker? = null

    private val Camera_Permission_Code = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geoCoder = Geocoder(this, Locale.getDefault())
        locationService = Service(fusedLocationClient, this, geoCoder)
        setContentView(R.layout.activity_main)

        // If the Camera Button is clicked:
        val openCameraButton: ImageButton = findViewById(R.id.Camera_Button)
        openCameraButton.setOnClickListener {
            // Check if the app has the permission to storage and camera
            // use Dexter plugin to simplify the process
            Log.i("MAin", "You clicked the camera button")
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
            ).withListener(object : MultiplePermissionsListener {
                // What to do when we have all permissions:
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    p0?.let {
                        if (p0!!.areAllPermissionsGranted()) {
                            /*Toast.makeText(this@MainActivity,"You have permissions now",Toast.LENGTH_SHORT                           ).show()
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, camera_requestCode)*/
                            val CameraView = Intent(this@MainActivity, CameraActivity::class.java)
                            startActivity(CameraView)
                        }
                    }
                }
                // What to do when we have not all permissions:
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    // create intent which goes to the settings of the application
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage("You have to give permissions!")
                        .setPositiveButton("go to settings") { _, _ ->
                            try {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                e.printStackTrace()
                            }
                        }
                        .setNegativeButton("cancel")
                        { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                }
            }).onSameThread().check()
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
                    while (true)
                    {
                        delay(10 * 1000L)
                        location = locationService.getCurrentLocation()
                        youMarker?.position = LatLng(location.latitude,location.longitude)
                        Log.v(this.javaClass.name, "Updated user location")
                    }
                }
            }
        }

        val playButton: ImageButton = findViewById(R.id.Play_Button)
        val pauseButton: ImageButton = findViewById(R.id.Pause_Button)
        val continueButton: ImageButton = findViewById(R.id.Continue_Button)
        val stopButton: ImageButton = findViewById(R.id.Stop_Button)
        stopButton.visibility = View.GONE
        pauseButton.visibility = View.GONE
        continueButton.visibility = View.GONE
        stopButton.setOnClickListener{
            locationService.stopTracking()
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE
            continueButton.visibility = View.GONE
        }

        pauseButton.setOnClickListener{
            locationService.pauseTracking()
            pauseButton.visibility = View.GONE
            continueButton.visibility = View.VISIBLE
        }

        continueButton.setOnClickListener{
            locationService.resumeTracking()
            continueButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
        }

        playButton.setOnClickListener{
            stopButton.visibility = View.VISIBLE
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
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
        youMarker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude,location.longitude))
                .title("You")
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

