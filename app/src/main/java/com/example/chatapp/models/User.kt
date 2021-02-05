package com.example.chatapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val uid: String,
    val username: String,
    val profileImageUrl: String,
    val notification: Boolean,
    val color: Int
    ): Parcelable {
    constructor(): this("", "", "", true, -1)
}