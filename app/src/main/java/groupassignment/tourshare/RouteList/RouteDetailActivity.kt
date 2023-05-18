package groupassignment.tourshare.RouteList

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import groupassignment.tourshare.R

class RouteDetailActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_detail)

        // Get the current route by extracting the extra from the intent
        val route = intent.getParcelableExtra<Routes>("route")

        // Find the UI elements
        val titleTextView: TextView = findViewById(R.id.imagetitle)
        val imageImageView: ImageView = findViewById(R.id.photo)

        // Set the values for the UI elements
        titleTextView.text = route?.title

        // Load the route image using Glide library
        if (route != null) {
            Glide.with(this)
                .load(route.downloadURL)
                .into(imageImageView)
        }

        // Handle the back button click
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}
