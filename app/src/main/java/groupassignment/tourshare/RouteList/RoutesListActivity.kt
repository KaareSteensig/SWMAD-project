package groupassignment.tourshare.RouteList

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import groupassignment.tourshare.ImageLists.PhotosListActivity
import groupassignment.tourshare.MainActivity
import groupassignment.tourshare.R
import groupassignment.tourshare.firebase.Login

class RoutesListActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_listroutes)

        val openMenuButton: ImageButton = findViewById(R.id.Menu_Button)
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
                        val intent = Intent(this, PhotosListActivity::class.java)
                        startActivity(intent)
                        drawer.close()
                    }
                    R.id.nav_routes -> {
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

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview1)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<Routes>()

        // This loop will create 20 Views containing
        // the image with the count of view
        val Route1 = Routes("Route1", "walk by harbour", R.drawable.map1)
        val Route2 = Routes("Route2", "walk through forest", R.drawable.map1)
        data.add(Route1)
        data.add(Route2)
        /*for (i in 1..2) {
            data.add(Route1, Route2)
            //data.add(Routes(R.drawable.baseline_stop_circle_24, "Item " + i))
        }*/


        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
    }

}