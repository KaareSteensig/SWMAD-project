package groupassignment.tourshare.ImageLists

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable


// Data class to handle the photos
// implements the interface Parcelable, so it can be passed by Intent.putextra
data class Photo(var title : String, val uri: String, var long: Double, var lat: Double, var description: String, var routeNr: Int): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble()!!,
        parcel.readDouble()!!,
        parcel.readString()!!,
        parcel.readInt()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(uri)
        parcel.writeDouble(long)
        parcel.writeDouble(lat)
        parcel.writeString(description)
        parcel.writeInt(routeNr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}
