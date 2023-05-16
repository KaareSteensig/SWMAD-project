package groupassignment.tourshare.ImageLists

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import groupassignment.tourshare.ImageLists.ui.theme.TourShareTheme
import groupassignment.tourshare.R

class DetailActivity : ComponentActivity() {

    // Displays a selected image with its details
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_detail)

        // get the current photo by submitted extra of intent
        val photo = intent.getParcelableExtra<Photo>("photo")
        if(photo != null)
        {
            // find the ui-items
            val titleTextView:TextView = findViewById(R.id.imagetitle)
            val photoImageView:ImageView = findViewById(R.id.photo)
            val descr :TextView = findViewById(R.id.textView7)
            val loc :TextView = findViewById(R.id.textView9)

            // set the values for the ui-items
            titleTextView.text = photo.title
            descr.text = photo.description
            loc.text = "${photo.lat} | ${photo.long}"
            val path: String = photo.uri
            val bitmap = BitmapFactory.decodeFile(path)
            photoImageView.setImageBitmap(bitmap)

        }

        // handle backButton: return to last activity
        val backButton :ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

    }
}

