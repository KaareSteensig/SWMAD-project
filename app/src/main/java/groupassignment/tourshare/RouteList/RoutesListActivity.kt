package groupassignment.tourshare.RouteList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import groupassignment.tourshare.R

class RoutesListActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_listroutes)

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