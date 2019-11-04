package com.team15app.team15

import android.os.Parcel
import android.os.Parcelable

class Club : Parcelable {

    var name: String? = null

    constructor(name: String) {
        this.name = name
    }

    private constructor(`in`: Parcel) {
        name = `in`.readString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Club) return false

        val artist = o as Club?

        return if (name != null) name == artist?.name else artist?.name == null
    }

    override fun hashCode(): Int {
        var result = if (name != null) name!!.hashCode() else 0
        result *= 31
        return result
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Club> = object : Parcelable.Creator<Club> {
            override fun createFromParcel(`in`: Parcel): Club {
                return Club(`in`)
            }

            override fun newArray(size: Int): Array<Club?> {
                return arrayOfNulls(size)
            }
        }
    }
}
