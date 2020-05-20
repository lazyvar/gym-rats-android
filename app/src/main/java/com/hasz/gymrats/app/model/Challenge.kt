package com.hasz.gymrats.app.model

import java.util.Date

data class Challenge(
  val id: Int,
  val name: String,
  val code: String,
  val profile_picture_url: String?,
  val start_date: Date,
  val end_date: Date,
  val description: String?,
  val score_by: String?
)