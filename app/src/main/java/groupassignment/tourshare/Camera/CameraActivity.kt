package groupassignment.tourshare.Camera

import android.app.Activity
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
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.google.android.gms.maps.model.LatLng
import groupassignment.tourshare.ImageLists.Photo
import groupassignment.tourshare.MainActivity
import groupassignment.tourshare.R
import java.io.*
import java.util.*


// Activity with  three functions:
// 1. opens the camera of the phone
// 2. displays the taken image and provides ui-elements, so the user can type in the title and description of the image
// 3. Saves the image in the storage of the phone and returns to main activity with the photo as photo-DataClass as Extra

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

        // get the infos about the position from MainActivity
        val long: Double = intent.getDoubleExtra("long", 4.1)
        val lat: Double = intent.getDoubleExtra("lat", 4.21)
        val routeNr: Int = intent.getIntExtra("routeNr", 1)


        // Open the Camera of the Phone
        // Init firebase
        imagesRefDB = FirebaseDatabase.getInstance("https://spotshare12-default-rtdb.europe-west1.firebasedatabase.app").reference.child("images")
        imagesRefStorage = FirebaseStorage.getInstance().getReference("images")

        // Open Camera of the Phone
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, camera_requestCode)

        // handle the ui-elements
        val safeButton: Button = findViewById(R.id.safe_button)
        safeButton.setOnClickListener {
            val title_text = findViewById<EditText>(R.id.title_text)
            val title = title_text.text.toString()

            val desc_text = findViewById<EditText>(R.id.descr_text)
            val description = desc_text.text.toString()

            // saves the image to the storage
            photo?.let {
                imagePath = saveImageToInternalStorage(photo!!)
                Log.i("SAVING", "Image saved to $imagePath")
                uploadImageToStorage(imagePath, title, description)
                // Reload image withthis URI
                //Log.i("SAVING", "Image saved to ${imagePath}")


            }

            // Create a new photo and submit it back to the main activity
            val backintent = Intent()
            val photo = Photo(title, imagePath, long, lat, description, routeNr)
            backintent.putExtra("photo", photo)
            setResult(RESULT_OK, backintent)
            finish()

        }
    }

    // Display the taken photo in the imageView
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

    // Saves the taken image to the storage by opening a new OutputStream
    // The path is random, because the title may include characters which are not allowed in a path
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