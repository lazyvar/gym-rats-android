package com.hasz.gymrats.app.model

data class ChallengeRow(
  val challengeInfo: ChallengeInfo? = null,
  val headerTitle: String? = null,
  val workout: Workout? = null,
  val noWorkouts: Boolean = false
)