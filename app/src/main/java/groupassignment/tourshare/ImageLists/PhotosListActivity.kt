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


class PhotosListActivity : ComponentActivity() {
    // Initialize Firebase references
    private val imagesRefDB = FirebaseDatabase.getInstance("https://spotshare12-default-rtdb.europe-west1.firebasedatabase.app").reference.child("images")
    private val imagesRefStorage = FirebaseStorage.getInstance().reference.child("images")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listphotos)

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

        // on below line we are initializing
        // our views with their ids.
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // on below line we are initializing our list
        val photoList = ArrayList<Photo>()

        // on below line we are creating a variable
        // for our grid layout manager and specifying
        // column count as 2
        val layoutManager = GridLayoutManager(this, 2)

        recyclerView.layoutManager = layoutManager

        // Create a new instance of the adapter using the updated photoList
        val photoRVAdapter = Adapter(photoList, this)

        // Set the adapter to the recycler view
        recyclerView.adapter = photoRVAdapter

        // Set up the ValueEventListener to fetch the image data from the database
        val imagesListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                photoList.clear()

                for (imageSnapshot in snapshot.children) {
                    val url = imageSnapshot.child("imageUrl").value as? String ?: ""
                    val title = imageSnapshot.child("title").value as? String ?: ""
                    val description = imageSnapshot.child("description").value as? String ?: ""

                    // Create a Photo object with the retrieved data
                    val photo = Photo(title, description, url)

                    // Add the photo to the list
                    photoList.add(photo)
                }

                // Notify the adapter that the data has changed
                photoRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the database error
                Log.e("PhotosListActivity", "Error retrieving image data: $error")
            }
        }

        // Attach the ValueEventListener to the database reference
        imagesRefDB.addValueEventListener(imagesListener)
    }
}