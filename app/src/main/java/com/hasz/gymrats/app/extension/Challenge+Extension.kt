package com.hasz.gymrats.app.extension

import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.Workout
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

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

fun Challenge.days(): List<LocalDateTime> {
  val days = arrayListOf<LocalDateTime>()
  var day = start_date

  while (day != end_date) {
    days.add(day)
    day = day.plusDays(1)
  }

  days.add(day)

  return days
}

fun Challenge.buckets(workouts: List<Workout>): List<Pair<LocalDateTime, List<Workout>>> {
  return days().reversed().map { date ->
    val workoutsOnDate = workouts.on(date)

    if (workoutsOnDate.isEmpty()) {
      return@map null
    } else {
      return@map date to workoutsOnDate
    }
  }.filterNotNull()
}

fun List<Workout>.on(date: LocalDateTime): List<Workout> = filter { local2YouTeeSee(it.created_at.atZone(ZoneId.systemDefault()), date) }

private fun local2YouTeeSee(local: ZonedDateTime, youToo: LocalDateTime): Boolean {
  return local.dayOfYear == youToo.dayOfYear && local.year == youToo.year
}
