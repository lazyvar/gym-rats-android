package com.hasz.gymrats.app.extension

import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.Workout
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

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

fun Challenge.daysLeft(): String {
  val today = LocalDateTime.now()
  val diff = ChronoUnit.DAYS.between(today, end_date).toInt()

  return if (diff == 0) {
    "Last day"
  } else if (diff > 0) {
    "$diff\nDays left"
  } else {
    "Completed\n${end_date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}"
  }
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

fun Challenge.buckets(workouts: List<Workout>): List<Pair<LocalDate, List<Workout>>> {
  val dateComparator = kotlin.Comparator<LocalDate> { a, b -> b.compareTo(a) }
  val hash = TreeMap<LocalDate, ArrayList<Workout>>(dateComparator)

  for (workout in workouts) {
    val day = workout.created_at.toLocalDate()
    val workoutList = hash[day] ?: arrayListOf()
    workoutList.add(workout)

    hash[day] = workoutList
  }

  return hash.map { it.key to it.value }
}

fun List<Workout>.on(date: LocalDateTime): List<Workout> = filter { local2YouTeeSee(it.created_at.atZone(ZoneId.systemDefault()), date) }

private fun local2YouTeeSee(local: ZonedDateTime, youToo: LocalDateTime): Boolean {
  return local.dayOfYear == youToo.dayOfYear && local.year == youToo.year
}
