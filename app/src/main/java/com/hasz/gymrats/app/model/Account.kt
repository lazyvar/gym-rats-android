package com.hasz.gymrats.app.model

import android.os.Parcelable
import com.stfalcon.chatkit.commons.models.IUser
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
  val id: Int,
  val email: String,
  val full_name: String,
  val profile_picture_url: String?,
  val token: String?
): Parcelable, IUser {
  override fun getAvatar(): String = profile_picture_url ?: ""
  override fun getName(): String = full_name
  override fun getId(): String = id.toString()
}
