package groupassignment.tourshare.ImageLists

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
import groupassignment.tourshare.Camera.CameraActivity
import groupassignment.tourshare.MainActivity
import groupassignment.tourshare.R
import groupassignment.tourshare.RouteList.RoutesListActivity
import groupassignment.tourshare.firebase.Login


// This activity displays all images in a scrollable grid Layout with a RecyclerView

class PhotosListActivity : ComponentActivity() {
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

        // set the adapter to the recycler view.
        recyclerView.adapter = photoRVAdapter

        // on below line we are adding data to our list
        // The List contains data of the DataClass photo (photos.kt)
        // All items in the List will we displayed
        PhotoList.add(Photo("Bild1", "/data/user/0/groupassignment.tourshare/app_taken_photos/7c558566-a630-437d-b542-4d29450aaa76.jpg", 1.1 , 2.2, "nice picture", 1))
        PhotoList.add(Photo("Bild2", "/data/user/0/groupassignment.tourshare/app_taken_photos/7c558566-a630-437d-b542-4d29450aaa76.jpg",.1 , 2.2, "cool picture", 1))
        PhotoList.add(Photo("Bild3", "/data/user/0/groupassignment.tourshare/app_taken_photos/7c558566-a630-437d-b542-4d29450aaa76.jpg", .1 , 2.2, "nice picture", 1))
        PhotoList.add(Photo("Bild4", "/data/user/0/groupassignment.tourshare/app_taken_photos/7c558566-a630-437d-b542-4d29450aaa76.jpg",.1 , 2.2, "cool picture", 1))
        PhotoList.add(Photo("Bild5", "/data/user/0/groupassignment.tourshare/app_taken_photos/7c558566-a630-437d-b542-4d29450aaa76.jpg",.1 , 2.2, "nice picture", 1))

        // notify the adapter that data has been updated.
        photoRVAdapter.notifyDataSetChanged()


        // Handles the click on a image-item:
        // Show the Deta View of the Image
        photoRVAdapter.onItemClick = {
            val DetailView = Intent(this@PhotosListActivity, DetailActivity::class.java)
            DetailView.putExtra("photo", it)
            startActivity(DetailView)
        }

    }
}