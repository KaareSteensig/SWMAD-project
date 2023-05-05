package groupassignment.tourshare.Camera

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import groupassignment.tourshare.R
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

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
                Log.i("SAVING", "Image saved to ${imagePath}")
                // Reload image withthis URI
            }
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


}


