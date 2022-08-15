package uz.fozilbekimomov.ml_card_scan.models

import android.os.Parcel
import android.os.Parcelable

class CardResult(val cardNumber: String?, val cardDate: String?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cardNumber)
        parcel.writeString(cardDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardResult> {
        override fun createFromParcel(parcel: Parcel): CardResult {
            return CardResult(parcel)
        }

        override fun newArray(size: Int): Array<CardResult?> {
            return arrayOfNulls(size)
        }
    }
}