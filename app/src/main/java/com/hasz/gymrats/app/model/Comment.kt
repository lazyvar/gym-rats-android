package com.hasz.gymrats.app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Instant

@Parcelize
data class Comment(
  val id: Int,
  val account: Account,
  val workout_id: Int,
  val content: String,
  val created_at: Instant
): Parcelable
