package groupassignment.tourshare.ImageLists

import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_detail)

        val photo = intent.getParcelableExtra<Photo>("photo")
        if(photo != null)
        {
            val titleTextView:TextView = findViewById(R.id.imagetitle)
            val photoImageView:ImageView = findViewById(R.id.photo)

            titleTextView.text = photo.title
            photoImageView.setImageResource(photo.image)

        }

        val backButton :ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }



    }
}

