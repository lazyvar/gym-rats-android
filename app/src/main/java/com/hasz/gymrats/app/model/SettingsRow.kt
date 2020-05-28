package com.hasz.gymrats.app.model

data class SettingsRow(
  val headerText: String?,
  val action: (() -> Unit)?,
  val leftText: String?,
  val rightText: String?
)