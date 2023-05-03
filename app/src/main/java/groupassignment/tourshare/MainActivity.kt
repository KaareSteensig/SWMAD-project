package groupassignment.tourshare

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import groupassignment.tourshare.Camera.CameraActivity

class MainActivity : ComponentActivity()  {

    private val Camera_Permission_Code = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openCameraButton: ImageButton = findViewById(R.id.Camera_Button)
        openCameraButton.setOnClickListener{
            //val CameraView = Intent(this@MainActivity, CameraActivity::class.java)
            //startActivity(CameraView)
            if((ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED)
                && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                val CameraView = Intent(this@MainActivity, CameraActivity::class.java)
                startActivity(CameraView)
            }
            else
            {
                val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, Camera_Permission_Code)
                //ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), Camera_Permission_Code)
                //ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 112)
            }


        }


    }

}

