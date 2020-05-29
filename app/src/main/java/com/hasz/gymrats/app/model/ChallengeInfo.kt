package com.hasz.gymrats.app.model

data class ChallengeInfo(
  val member_count: Int,
  val workout_count: Int,
  val leader: Account,
  val leader_score: String,
  val current_account_score: String
)