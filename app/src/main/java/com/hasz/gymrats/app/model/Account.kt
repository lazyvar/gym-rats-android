package com.hasz.gymrats.app.model

data class Account(
  val id: Int,
  val email: String,
  val full_name: String,
  val profile_picture_url: String?,
  val token: String?
)