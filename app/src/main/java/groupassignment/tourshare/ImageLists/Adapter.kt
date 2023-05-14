package groupassignment.tourshare.ImageLists
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import groupassignment.tourshare.R

// on below line we are creating
// a course rv adapter class.
    class Adapter(private val photoList: ArrayList<Photo>, private val context: Context) : RecyclerView.Adapter<Adapter.CourseViewHolder>() {

    var onItemClick : ((Photo)->Unit)? = null

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): Adapter.CourseViewHolder {
            // this method is use to inflate the layout file
            // which we have created for our recycler view.
            // on below line we are inflating our layout file.
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.photo_list_item,
                parent, false
            )
            // at last we are returning our view holder
            // class with our item View File.
            return CourseViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: Adapter.CourseViewHolder, position: Int) {
            // on below line we are setting data to our text view and our image view.
            val ph = photoList[position]
            holder.courseNameTV.text = photoList.get(position).title
            holder.courseIV.setImageResource(photoList.get(position).image)
            holder.itemView.setOnClickListener {
                Log.d("onBindViewHolder: ", " CLICKED")
                onItemClick?.invoke(ph)
            }

        }

        override fun getItemCount(): Int {
            // on below line we are
            // returning our size of our list
            return photoList.size
        }

        class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // on below line we are initializing our course name text view and our image view.
            val courseNameTV: TextView = itemView.findViewById(R.id.idTVCourse)
            val courseIV: ImageView = itemView.findViewById(R.id.idIVCourse)
        }
    }
