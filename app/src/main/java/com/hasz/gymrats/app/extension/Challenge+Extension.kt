package com.hasz.gymrats.app.extension

import com.hasz.gymrats.app.model.Challenge

fun Challenge.isActive(): Boolean {
  return false
}

fun Challenge.completed(): Boolean {
  return true
}

fun List<Challenge>.active(): List<Challenge> {
  return filter { it.isActive() }
}

fun List<Challenge>.completed(): List<Challenge> {
  return filter { it.completed() }
}