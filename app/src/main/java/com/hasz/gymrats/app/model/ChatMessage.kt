package com.hasz.gymrats.app.model

import android.os.Parcelable
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.commons.models.MessageContentType
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Instant
import java.util.*

@Parcelize
data class ChatMessage(
  val id: Int,
  val challenge_id: Int,
  val content: String,
  val created_at: Instant,
  val account: Account,
  val message_type: String?
): Parcelable, IMessage, MessageContentType.Image {
  override fun getId(): String = id.toString()
  override fun getText(): String? = content
  override fun getUser(): IUser? = account
  override fun getCreatedAt(): Date? = Date(created_at.toEpochMilli())
  override fun getImageUrl(): String? = if (message_type == "image") { content } else { null }
}
