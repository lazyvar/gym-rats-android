package com.hasz.gymrats.app.extension

import com.hasz.gymrats.app.model.Challenge

fun Challenge.isActive(): Boolean {
  return false
}

fun List<Challenge>.active(): List<Challenge> {
  return filter { it.isActive() }
}