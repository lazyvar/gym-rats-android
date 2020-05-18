package com.hasz.gymrats.app.model

data class ServiceResponse <T> (
  val status: String,
  val data: T?,
  val error: String?
)
