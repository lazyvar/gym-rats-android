package com.hasz.gymrats.app.model

data class Account(
  val id: Int,
  val email: String,
  val fullName: String,
  val profilePictureUrl: String?,
  val token: String?
)