package groupassignment.tourshare.Camera

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import groupassignment.tourshare.R
import groupassignment.tourshare.gps.Service
import kotlinx.coroutines.launch
import java.io.*
import java.util.*



class CameraActivity : ComponentActivity() {

    companion object
    {
        private const val camera_requestCode = 1
        private const val image_directory = "taken_photos"
    }

    private var imagePath: String = ""
    private var photo : Bitmap? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geoCoder: Geocoder
    private lateinit var locationService: Service

    //private val uid: String? = intent.getStringExtra("uid")
    //private val imagesRefDB = uid?.let { FirebaseDatabase.getInstance().getReference("images") }
    //private val imagesRefStorage = uid?.let { FirebaseStorage.getInstance().getReference("images") }
    private lateinit var imagesRefDB: DatabaseReference
    private lateinit var imagesRefStorage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Init firebase
        imagesRefDB = FirebaseDatabase.getInstance("https://spotshare12-default-rtdb.europe-west1.firebasedatabase.app").reference.child("images")
        imagesRefStorage = FirebaseStorage.getInstance().getReference("images")

        // Open Camera of the Phone
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, camera_requestCode)

        // handle the Buttons
        val safeButton: Button = findViewById(R.id.safe_button)
        safeButton.setOnClickListener {
            val title_text = findViewById<EditText>(R.id.title_text)
            val title = title_text.text.toString()
            Log.i("TITLE: ", title)

            val desc_text = findViewById<EditText>(R.id.descr_text)
            val description = desc_text.text.toString()
            Log.i("description: ", description)
            photo?.let {
                imagePath = saveImageToInternalStorage(photo!!)
                Log.i("SAVING", "Image saved to $imagePath")
                uploadImageToStorage(imagePath, title, description)
                // Reload image withthis URI
            }
            // Get the current Location:
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == camera_requestCode) {
                data?.extras?.let {
                    photo = data.extras!!.get("data") as Bitmap
                    val PhotoPreview: ImageView = findViewById(R.id.PhotoPreview)

                    Glide.with(this)
                        .load(photo)
                        .centerCrop()
                        .into(PhotoPreview)
                }

            } else
                Log.i("Cmaera Activity.onActivityResult", "An error occurred! ")
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String
    {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(image_directory, MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException)
        {
            e.printStackTrace()
        }
        // return directory and name of file
        return file.absolutePath
    }

    private fun uploadImageToStorage(imagePath: String, title: String, description: String) {
        val file = Uri.fromFile(File(imagePath))
        val imageRef = imagesRefStorage.child(file.lastPathSegment!!)

        imageRef.putFile(file).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                val key = imagesRefDB.push().key!!
                val image = groupassignment.tourshare.firebase.Image(title, description, imagePath)

                imagesRefDB.child(key).setValue(image)
                    .addOnCompleteListener{
                    }.addOnFailureListener{err ->
                        Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                    }
                Log.i("Camera Upload", "Image uploaded successfully.")
            }
        }.addOnFailureListener { exception ->
            Log.e("CameraActivity Upload", "Error uploading image: ", exception)
        }
    }

}