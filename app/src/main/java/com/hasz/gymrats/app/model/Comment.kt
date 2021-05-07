package com.hasz.gymrats.app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

@Parcelize
data class Comment(
  val id: Int,
  val account: Account,
  val workout_id: Int,
  val content: String,
  val created_at: Instant
): Parcelable

fun Comment.createdAt(): LocalDateTime =
  ZonedDateTime.ofInstant(this.created_at, ZoneId.systemDefault()).toLocalDateTime()
