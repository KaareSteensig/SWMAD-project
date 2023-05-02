package groupassignment.tourshare.firebase

import com.google.firebase.firestore.GeoPoint

data class Pictures(val id:String, val title:String, val description:String, val position:GeoPoint) {
}