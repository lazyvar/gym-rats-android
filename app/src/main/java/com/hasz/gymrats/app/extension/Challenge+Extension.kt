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
  val now = LocalDate.now()
  val start = start_date.toLocalDate()
  val end = end_date.toLocalDate()

  return now == start || now == end || (now.isAfter(start) && now.isBefore(end))
}

fun Challenge.isUpcoming(): Boolean {
  val now = LocalDate.now()
  val start = start_date.toLocalDate()

  return now.isBefore(start)
}

fun Challenge.completed(): Boolean {
  val now = LocalDate.now()
  val end = end_date.toLocalDate()

  return now.isAfter(end)
}

fun List<Challenge>.active(): List<Challenge> {
  return filter { it.isActive() }
}

fun List<Challenge>.activeOrUpcoming(): List<Challenge> {
  return filter { it.isActive() || it.isUpcoming() }
}

fun List<Challenge>.completed(): List<Challenge> {
  return filter { it.completed() }
}

fun List<Workout>.onDay(date: LocalDate): List<Workout> = filter { it.occurred_at.atZone(ZoneId.systemDefault()).toLocalDate() == date }

fun Challenge.daysLeft(): String {
  val today = LocalDate.now()
  val end = end_date.toLocalDate()
  val diff = ChronoUnit.DAYS.between(today, end).toInt()

  return if (diff == 0) {
    "Last day"
  } else if (diff == 1) {
    "1 Day left"
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
    val day = ZonedDateTime.ofInstant(workout.occurred_at, ZoneId.systemDefault()).toLocalDate()
    val workoutList = hash[day] ?: arrayListOf()
    workoutList.add(workout)

    hash[day] = workoutList
  }

  return hash.map { it.key to it.value }
}

fun List<Workout>.on(date: LocalDateTime): List<Workout> = filter { local2YouTeeSee(it.occurred_at.atZone(ZoneId.systemDefault()), date) }

fun local2YouTeeSee(local: ZonedDateTime, youToo: LocalDateTime): Boolean {
  return local.dayOfYear == youToo.dayOfYear && local.year == youToo.year
}
