import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import groupassignment.tourshare.ImageLists.Photo
import groupassignment.tourshare.R
import groupassignment.tourshare.RouteList.Routes

class CustomAdapter(private val mList: List<Routes>, private val context: Context) : RecyclerView.Adapter<CustomAdapter.CourseViewHolder>() {

    var onItemClick : ((Routes)->Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomAdapter.CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.route_list_item,
            parent, false
        )
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAdapter.CourseViewHolder, position: Int) {
        val route = mList[position]
        holder.textView.text = mList.get(position).title
        Glide.with(context)
            .load(route.downloadURL)
            .into(holder.imageview)
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(route)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // initialize the image name text view and our image view.
        val textView : TextView = itemView.findViewById(R.id.textView)
        val imageview: ImageView = itemView.findViewById(R.id.imageview)
    }
}