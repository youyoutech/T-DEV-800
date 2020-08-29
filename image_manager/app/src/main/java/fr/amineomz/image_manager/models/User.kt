package fr.amineomz.image_manager.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val email: String,
    val username: String,
    val phone_number: String
): Parcelable {
}