package com.hasz.gymrats.app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime

@Parcelize
data class Challenge(
  val id: Int,
  val name: String,
  val code: String,
  val profile_picture_url: String?,
  val start_date: LocalDateTime,
  val end_date: LocalDateTime,
  val description: String?,
  val score_by: String?
): Parcelable