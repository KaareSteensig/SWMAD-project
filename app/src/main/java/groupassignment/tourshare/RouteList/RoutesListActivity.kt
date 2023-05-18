package groupassignment.tourshare.RouteList

import CustomAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import groupassignment.tourshare.ImageLists.DetailActivity
import groupassignment.tourshare.ImageLists.Photo
import groupassignment.tourshare.ImageLists.PhotosListActivity
import groupassignment.tourshare.MainActivity
import groupassignment.tourshare.R
import groupassignment.tourshare.firebase.Login

class RoutesListActivity : ComponentActivity() {
    // Initialize Firebase references
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val uid = currentUser?.uid
    private val routesRefDB = uid?.let {
        FirebaseDatabase.getInstance("https://spotshare12-default-rtdb.europe-west1.firebasedatabase.app")
            .reference.child("users").child(it).child("routes")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_listroutes)

        val openMenuButton: ImageButton = findViewById(R.id.Menu_Button)
        openMenuButton.setOnClickListener {
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
                        val intent = Intent(this, PhotosListActivity::class.java)
                        startActivity(intent)
                        drawer.close()
                    }
                    R.id.nav_routes -> {
                        drawer.close()
                    }
                    R.id.nav_logout -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        drawer.close()
                    }
                }
                true
            }
        }

        // getting the recyclerview by its id
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview1)

        // ArrayList of class Routes
        val RouteList = ArrayList<Routes>()

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(RouteList, this)

        // Setting the Adapter with the recyclerview
        recyclerView.adapter = adapter


        // on below line we are adding data to our list
        // The List contains data of the DataClass photo (photos.kt)
        // All items in the List will we displayed
        val routesListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                RouteList.clear()
                val i = 0

                for (imageSnapshot in snapshot.children) {
                    val url = imageSnapshot.child("downloadURL").value as? String ?: ""
                    val title = imageSnapshot.child("title").value as? String ?: ""
                    val description = imageSnapshot.child("description").value as? String ?: ""
                    // Create a Photo object with the retrieved data
                    val route = Routes(title, url)

                    // Add the photo to the list
                    RouteList.add(route)
                }
                // notify the adapter that data has been updated.
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the database error
                Log.e("RoutesListActivity", "Error retrieving route data: $error")
            }
        }


        // Handles the click on a image-item:
        // Show the Deta View of the Image
        adapter.onItemClick = {
            val DetailView = Intent(this@RoutesListActivity, RouteDetailActivity::class.java)
            DetailView.putExtra("route", it)
            startActivity(DetailView)
        }

        // Attach the ValueEventListener to the database reference
        routesRefDB?.addValueEventListener(routesListener)

    }
}
