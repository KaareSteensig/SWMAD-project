package groupassignment.tourshare.Camera

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.ImageCapture
import groupassignment.tourshare.R
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.util.*



class CameraActivity : ComponentActivity() {

    private val Camera_Permission_Code = 1
    private val CameraRequesetCode  = 2
    var frame: ImageView? = null
    var image_uri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        frame = findViewById(R.id.PhotoPreview)

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, CameraRequesetCode)



        val title_text = findViewById<EditText>(R.id.title_text)
        val title = title_text.text
        Log.i("TITLE: " ,"${title}")


        val desc_text = findViewById<EditText>(R.id.descr_text)
        val description = desc_text.text
        Log.i("description: " ,"${description}")



        val safeButton: Button = findViewById(R.id.safe_button)
        safeButton.setOnClickListener{
            //saveToGallery(image)
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
            Toast.makeText(this, description, Toast.LENGTH_SHORT).show()
            finish()

        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Camera_Permission_Code)
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CameraRequesetCode)
            }
            else
            {
                Toast.makeText(this, "No Permission to the Camera!", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == CameraRequesetCode)
            {
                val bitmap = uriToBitmap(image_uri!!)
                frame?.setImageBitmap(bitmap)
            }
        }
        else
            Log.i("main Activity.RESULT_OK", "An error occurred! ")


    }

    // takes URI of the image and returns bitmap
    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}

