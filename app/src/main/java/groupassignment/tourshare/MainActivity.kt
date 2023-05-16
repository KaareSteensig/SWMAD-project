package groupassignment.tourshare

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.compose.rememberCameraPositionState
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import groupassignment.tourshare.Camera.CameraActivity
import groupassignment.tourshare.ImageLists.DetailActivity
import groupassignment.tourshare.ImageLists.Photo
import groupassignment.tourshare.ImageLists.PhotosListActivity
import groupassignment.tourshare.RouteList.RoutesListActivity
import groupassignment.tourshare.firebase.Login
import groupassignment.tourshare.gps.Service
import groupassignment.tourshare.gps.TAG_ROUTE
import groupassignment.tourshare.gps.drawRoute
import groupassignment.tourshare.gps.updatePosition
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : ComponentActivity(), OnMapReadyCallback {
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
    private var imagelocation: Location? = null
    private var setMarkerRequestCode : Int = 5

    private var routeNr : Int = 1

   // contains a list of all photos of a route
    var imageList = mutableListOf<Photo>()
    // contains a list of all photomarkers of a route
    private var PhotoMarkers = mutableListOf<Marker>()


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
            Log.i("Main", "You clicked the camera button")
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

                            findViewById<ComposeView>(R.id.my_composable).setContent {
                                val scope = rememberCoroutineScope()
                                LaunchedEffect(locationService.locationOn.value) {
                                    scope.launch {
                                        imagelocation = locationService.getCurrentLocation()
                                        Log.i("MAIN", "Imagelocation: $imagelocation")
                                        val CameraView =
                                            Intent(this@MainActivity, CameraActivity::class.java)
                                        CameraView.putExtra("long", imagelocation!!.longitude)
                                        CameraView.putExtra("lat", imagelocation!!.latitude)
                                        CameraView.putExtra("routeNr", routeNr)
                                        //startActivity(CameraView)
                                        startActivityForResult(CameraView, setMarkerRequestCode)
                                    }
                                }
                            }
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

        val openMenuButton: ImageButton = findViewById(R.id.Menu_Button)
        openMenuButton.setOnClickListener {
            val drawer: DrawerLayout = findViewById(R.id.drawerLayout)
            drawer.open()
            val navView: NavigationView = findViewById(R.id.navView)
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_map -> {
                        //what should happen:
                        //Toast.makeText(this@MainActivity, "Map Item Clicked", Toast.LENGTH_SHORT).show()
                        drawer.close()
                    }
                    R.id.nav_pics -> {
                        //Toast.makeText(this@MainActivity, "Pictures Item Clicked", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, PhotosListActivity::class.java)
                        startActivity(intent)
                        drawer.close()
                    }
                    R.id.nav_routes -> {
                        //Toast.makeText(this@MainActivity, "Routes Item Clicked", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, RoutesListActivity::class.java)
                        startActivity(intent)
                        drawer.close()
                    }
                    R.id.nav_logout -> {
                        //Toast.makeText(this@MainActivity, "Logout Item Clicked", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        drawer.close()
                    }
                }
                true
            }
        }

        mapview = findViewById(R.id.Map_View)

        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
            && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            locationService.setLocationOn()
        } else {
            val permission = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            requestPermissions(permission, 2)
            locationService.setLocationOn()
            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
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

        val playButton: ImageButton = findViewById(R.id.Play_Button)
        val pauseButton: ImageButton = findViewById(R.id.Pause_Button)
        val continueButton: ImageButton = findViewById(R.id.Continue_Button)
        val stopButton: ImageButton = findViewById(R.id.Stop_Button)
        stopButton.visibility = View.GONE
        pauseButton.visibility = View.GONE
        continueButton.visibility = View.GONE
        stopButton.setOnClickListener {
            locationService.stopTracking()
            if (pauseButton.visibility == View.GONE) {
                continueButton.visibility = View.GONE
            } else {
                pauseButton.visibility = View.GONE
            }
            playButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE
        }

        pauseButton.setOnClickListener {
            locationService.pauseTracking()
            pauseButton.visibility = View.GONE
            continueButton.visibility = View.VISIBLE
        }

        continueButton.setOnClickListener {
            locationService.resumeTracking()
            continueButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
        }

        playButton.setOnClickListener {
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
                        locationService.startTracking(youMarker, mMap) {
                            if (it.isNotEmpty()) {
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
                            }
                            drawRoute(mMap, polyLineList, locationName, currentPos)
                            findViewById<ComposeView>(R.id.my_composable).setContent {
                                updatePosition(youMarker, locationService, mMap)

                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        youMarker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .title("You")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.youmarker2))
        )

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), 15f
            )
        )
        mMap = googleMap

        findViewById<ComposeView>(R.id.my_composable).setContent {
            updatePosition(youMarker, locationService, mMap)
        }

        // if the user clicks on a marker:
        mMap.setOnMarkerClickListener { marker ->
            val markerName = marker.title
            //Toast.makeText(this@MainActivity, "Clicked location is $markerName", Toast.LENGTH_SHORT).show()
            // if the marker is assigned to a photo, open the Datail View with that photo
            val photo = imageList.find { ph -> marker.title.equals(ph.title) }
            if (photo != null) {
                val DetailView =
                    Intent(this@MainActivity, DetailActivity::class.java)
                DetailView.putExtra("photo", photo)
                startActivity(DetailView)
            }
            false
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

    // if we retrun from the camera Activity, handle taken photo:
    // add the photo to the List
    // create a marker for the photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            setMarkerRequestCode -> {
                if (resultCode == RESULT_OK) {
                    // the taken photo will be submitted by the returning intent
                    // get the photo
                    var photo = data?.getParcelableExtra<Photo>("photo")
                    if (photo != null) {
                        //add default values
                        if (photo.title == "") {
                            photo.title = "title"
                        }
                        if (photo.lat == null) {
                            photo.lat = 0.0
                        }
                        if (photo.long == null) {
                            photo.long = 0.0
                        }
                        if (photo.description == "") {
                            photo.description  = "description"
                        }
                        if (photo.routeNr == null) {
                            photo.routeNr = routeNr
                        }
                        val uri = photo.uri
                        // add the photo to the list
                        uri?.let {
                            imageList.add(photo)
                        }

                        // create a marker for each element in the imageList and assign it to the map
                        // add this marker to the marker list PhotoMarkers
                        for (i in imageList.indices) {
                            val photomarker = mMap.addMarker(
                                MarkerOptions()
                                    .position(
                                        LatLng(
                                            imageList[i].lat + 0.002,
                                            imageList[i].long + 0.002
                                        )
                                    )
                                    .title(imageList[i].title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_icon))
                            )
                            if (photomarker != null) {
                                PhotoMarkers.add(photomarker)
                            }
                           // Log.i("MAIN", "You set a marker on  ${imageList[i].title}")
                        }
                    }
                }
            }
        }
    }
}



