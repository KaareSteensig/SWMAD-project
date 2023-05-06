package groupassignment.tourshare

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.ActionMenuView
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import backend.RepositoryMenus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import groupassignment.tourshare.Camera.CameraActivity
import groupassignment.tourshare.gps.Service
import groupassignment.tourshare.gps.routing.Route
import java.util.*

class MainActivity : ComponentActivity(), OnMapReadyCallback  {
    private val repository = RepositoryMenus()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geoCoder: Geocoder
    private lateinit var locationService: Service

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

        val mapview: MapView = findViewById(R.id.Map_View)
        mapview.getMapAsync(this)


        val playButton: ImageButton = findViewById(R.id.Play_Button)
        playButton.setOnClickListener{

        }


        if((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
            && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationService.setLocationOn()
        }
        else
        {
            val permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissions(permission, 2)
            locationService.setLocationOn()
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
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

}

