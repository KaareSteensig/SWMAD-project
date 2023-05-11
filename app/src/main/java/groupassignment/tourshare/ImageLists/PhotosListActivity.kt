package groupassignment.tourshare.ImageLists

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import groupassignment.tourshare.R



class PhotosListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listphotos)

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

    }

}