package groupassignment.tourshare.ImageLists

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
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


class PhotosListActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    //lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listphotos)

        val openMenuButton: ImageButton = findViewById(R.id.Menu_Button2)
        openMenuButton.setOnClickListener {
            Log.i("Main", "You clicked the MENU button")
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)


        // on below line we are initializing
        // our views with their ids.
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // on below line we are initializing our list
       val  PhotoList = ArrayList<Photo>()

        // on below line we are creating a variable
        // for our grid layout manager and specifying
        // column count as 2
        val layoutManager = GridLayoutManager(this, 2)

        recyclerView.layoutManager = layoutManager

        // on below line we are initializing our adapter
        val photoRVAdapter = Adapter(PhotoList, this)

        // on below line we are setting
        // adapter to our recycler view.
        recyclerView.adapter = photoRVAdapter

        // on below line we are adding data to our list
        PhotoList.add(Photo("Bild1", R.drawable.map1))
        PhotoList.add(Photo("Bild2", R.drawable.menu))
        PhotoList.add(Photo("Bild3", R.drawable.play))
        PhotoList.add(Photo("Bild4", R.drawable.stop))
        PhotoList.add(Photo("Bild5", R.drawable.pause))

        // on below line we are notifying adapter
        // that data has been updated.
        photoRVAdapter.notifyDataSetChanged()


        photoRVAdapter.onItemClick = {

            // do something with your item
            Log.d("PhotosListActivity: ", " Yic clicked on ${it.title}")
            val DetailView = Intent(this@PhotosListActivity, DetailActivity::class.java)
            DetailView.putExtra("photo", it)
            startActivity(DetailView)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_map -> {
                Toast.makeText(this, "MAP clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_routes -> {
                Toast.makeText(this, "ROUTES clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RoutesListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_pics -> {
                Toast.makeText(this, "PICS clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, PhotosListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}