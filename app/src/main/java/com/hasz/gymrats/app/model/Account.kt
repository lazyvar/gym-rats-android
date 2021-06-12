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
  val token: String?,
  val workout_notifications_enabled: Boolean,
  val comment_notifications_enabled: Boolean,
  val chat_message_notifications_enabled: Boolean
): Parcelable, IUser {
  override fun getAvatar(): String = profile_picture_url ?: ""
  override fun getName(): String = full_name
  override fun getId(): String = id.toString()
}
