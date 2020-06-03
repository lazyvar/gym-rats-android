package com.hasz.gymrats.app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
  val id: Int,
  val email: String,
  val full_name: String,
  val profile_picture_url: String?,
  val token: String?
): Parcelable