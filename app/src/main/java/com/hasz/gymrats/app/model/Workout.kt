package com.hasz.gymrats.app.model

import org.threeten.bp.LocalDateTime

data class Workout(
  val id: Int,
  val account: Account,
  val challenge_id: Int,
  val title: String,
  val description: String?,
  val photo_url: String?,
  val created_at: LocalDateTime,
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
)