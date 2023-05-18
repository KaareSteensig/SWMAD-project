package groupassignment.tourshare.ImageLists

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import groupassignment.tourshare.Camera.CameraActivity
import groupassignment.tourshare.MainActivity
import groupassignment.tourshare.R
import groupassignment.tourshare.RouteList.RoutesListActivity
import groupassignment.tourshare.firebase.Login


// This activity displays all images in a scrollable grid Layout with a RecyclerView

class PhotosListActivity : ComponentActivity() {
    // Initialize Firebase references
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val uid = currentUser?.uid
    private val imagesRefDB = uid?.let {
        FirebaseDatabase.getInstance("https://spotshare12-default-rtdb.europe-west1.firebasedatabase.app")
        .reference.child("users").child(it).child("images")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listphotos)

        // handle Menu Button
        val openMenuButton: ImageButton = findViewById(R.id.Menu_Button2)
        openMenuButton.setOnClickListener{
            val drawer: DrawerLayout = findViewById(R.id.drawerLayout)
            drawer.open()
            val navView: NavigationView = findViewById(R.id.navView)
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_map -> {
                        //what should happen:
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        drawer.close()
                    }
                    R.id.nav_pics -> {
                        drawer.close()
                    }
                    R.id.nav_routes -> {
                        val intent = Intent(this, RoutesListActivity::class.java)
                        startActivity(intent)
                        drawer.close()
                    }
                    R.id.nav_logout -> {
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        drawer.close()
                    }
                }
                true
            }
        }


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // initializing the list
        val PhotoList = ArrayList<Photo>()

        // creating a variable for our grid layout manager and specifying the column count as 2
        val layoutManager = GridLayoutManager(this, 2)

        recyclerView.layoutManager = layoutManager

        // initialize our adapter
        val photoRVAdapter = Adapter(PhotoList, this)
        // Create a new instance of the adapter using the updated photoList

        // set the adapter to the recycler view.
        recyclerView.adapter = photoRVAdapter

        // on below line we are adding data to our list
        // The List contains data of the DataClass photo (photos.kt)
        // All items in the List will we displayed
        val imagesListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                PhotoList.clear()
                val i = 0

                for (imageSnapshot in snapshot.children) {
                    val url = imageSnapshot.child("imageUrl").value as? String ?: ""
                    val title = imageSnapshot.child("title").value as? String ?: ""
                    val description = imageSnapshot.child("description").value as? String ?: ""
                    val long = imageSnapshot.child("longitude").value as? Double?: 0.0
                    val lat = imageSnapshot.child("latitude").value as? Double ?: 0.0

                    val routeNr = i + 1

                    // Create a Photo object with the retrieved data
                    val photo = Photo(title, url, long,  lat, description, routeNr)

                    // Add the photo to the list
                    PhotoList.add(photo)
                }
                // notify the adapter that data has been updated.
                photoRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the database error
                Log.e("PhotosListActivity", "Error retrieving image data: $error")
            }
        }



        // Handles the click on a image-item:
        // Show the Deta View of the Image
        photoRVAdapter.onItemClick = {
            val DetailView = Intent(this@PhotosListActivity, DetailActivity::class.java)
            DetailView.putExtra("photo", it)
            startActivity(DetailView)
        }

        // Attach the ValueEventListener to the database reference
        imagesRefDB?.addValueEventListener(imagesListener)

    }
}