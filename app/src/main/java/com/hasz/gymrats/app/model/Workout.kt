package com.hasz.gymrats.app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.*

@Parcelize
data class Workout(
  val id: Int,
  val account: Account,
  val challenge_id: Int,
  val title: String,
  val description: String?,
  val photo_url: String?,
  val occurred_at: Instant,
  val google_place_id: String?,
  val duration: Int?,
  val distance: String?,
  val steps: Int?,
  val calories: Int?,
  val points: Int?,
  val apple_device_name: String?,
  val apple_source_name: String?,
  val apple_workout_uuid: String?,
  val activity_type: String?
): Parcelable

fun Workout.occurredAt(): LocalDateTime =
  ZonedDateTime.ofInstant(this.occurred_at, ZoneId.systemDefault()).toLocalDateTime()
