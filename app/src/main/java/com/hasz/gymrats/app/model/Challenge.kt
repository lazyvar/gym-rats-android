package com.hasz.gymrats.app.model

import org.threeten.bp.LocalDateTime

data class Challenge(
  val id: Int,
  val name: String,
  val code: String,
  val profile_picture_url: String?,
  val start_date: LocalDateTime,
  val end_date: LocalDateTime,
  val description: String?,
  val score_by: String?
)