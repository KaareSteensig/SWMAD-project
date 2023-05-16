package groupassignment.tourshare.ImageLists
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import groupassignment.tourshare.R


// a Recycler View adapter class for the images.
    class Adapter(private val photoList: ArrayList<Photo>, private val context: Context) : RecyclerView.Adapter<Adapter.CourseViewHolder>() {

    var onItemClick : ((Photo)->Unit)? = null

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): Adapter.CourseViewHolder {
            // this method is use to inflate the layout file
            // which we have created for our recycler view.
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.photo_list_item,
                parent, false
            )
            return CourseViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: Adapter.CourseViewHolder, position: Int) {
            // set the data to our text view and our image view.
            val ph = photoList[position]
            holder.idImageText.text = photoList.get(position).title
            val bitmap = BitmapFactory.decodeFile(photoList.get(position).uri)
            holder.idImage.setImageBitmap(bitmap)
            // add a setOnClickListener to make the images clickable
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(ph)
            }

        }

        override fun getItemCount(): Int {
            // return the size of our list
            return photoList.size
        }

        class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // initialize the image name text view and our image view.
            val idImageText : TextView = itemView.findViewById(R.id.idImagetext)
            val idImage: ImageView = itemView.findViewById(R.id.idImage)


        }
    }
