package groupassignment.tourshare.RouteList

import android.os.Parcel
import android.os.Parcelable

data class Routes(
    val title: String = "",
    val downloadURL: String = "",
    val description: String = "",

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(downloadURL)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Routes> {
        override fun createFromParcel(parcel: Parcel): Routes {
            return Routes(parcel)
        }

        override fun newArray(size: Int): Array<Routes?> {
            return arrayOfNulls(size)
        }
    }
}
