package com.hasz.gymrats.app.service

import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseResultHandler
import com.github.kittinunf.fuel.gson.responseObject
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.hasz.gymrats.app.BuildConfig
import com.hasz.gymrats.app.model.*
import com.hasz.gymrats.app.typeadapter.InstantConverter
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.hasz.gymrats.app.typeadapter.LocalDateTimeConverter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

object GymRatsApi {
  private val baseUrl = BuildConfig.API
  val gsonGuy: Gson

  init {
    FuelManager.instance.basePath = baseUrl
    gsonGuy = GsonBuilder()
      .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter())
      .registerTypeAdapter(Instant::class.java, InstantConverter())
      .create()

    setBaseHeaders()
  }

  fun login(email: String, password: String, handler: (Result<Account>) -> Unit) {
    Fuel.post("/tokens", listOf("email" to email, "password" to password))
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun createAccount(email: String, password: String, fullName: String, handler: (Result<Account>) -> Unit) {
    Fuel.post("/accounts", listOf("email" to email, "password" to password, "full_name" to fullName))
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun getChallenge(code: String, handler: (Result<List<Challenge>>) -> Unit) {
    Fuel.get("/challenges", listOf("code" to code))
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun postComment(content: String, workout: Workout, handler: (Result<Comment>) -> Unit) {
    Fuel.post("/workouts/${workout.id}/comments", listOf("content" to content))
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun getComments(workout: Workout, handler: (Result<List<Comment>>) -> Unit) {
    Fuel.get("/workouts/${workout.id}/comments")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun joinChallenge(code: String, handler: (Result<Challenge>) -> Unit) {
    Fuel.post("/memberships", listOf("code" to code))
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun allChallenges(handler: (Result<List<Challenge>>) -> Unit) {
    Fuel.get("/challenges")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun editChallenge(id: Int, startDate: LocalDateTime, endDate: LocalDateTime, name: String, description: String?, handler: (Result<Challenge>) -> Unit) {
    val start = startDate.toLocalDate().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    val end = endDate.toLocalDate().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    var body = listOf("start_date" to start, "end_date" to end, "name" to name)

    description?.let {
      body = ArrayList(body).apply { add("description" to it) }
    }

    Fuel.put("/challenges/${id}", body)
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun getMembers(challenge: Challenge, handler: (Result<List<Account>>) -> Unit) {
    Fuel.get("challenges/${challenge.id}/members")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun createChallenge(startDate: Date, endDate: Date, name: String, description: String?, scoreBy: String, handler: (Result<Challenge>) -> Unit) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    val start = dateFormatter.format(atStartOfDay(startDate))
    val end = dateFormatter.format(atStartOfDay(endDate))
    var body = listOf("start_date" to start, "end_date" to end, "score_by" to scoreBy, "time_zone" to "AND", "name" to name)

    description?.let {
      body = ArrayList(body).apply { add("description" to it) }
    }

    Fuel.post("/challenges", body)
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun chatMessages(challenge: Challenge, page: Int, handler: (Result<List<ChatMessage>>) -> Unit) {
    Fuel.get("challenges/${challenge.id}/messages?page=${page}")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun postWorkout(
    uri: Uri,
    title: String,
    description: String?,
    duration: Int?,
    distance: String?,
    calories: Int?,
    steps: Int?,
    points: Int?,
    challenges: List<Int>,
    handler: (Result<Workout>) -> Unit
  ) {
    GService.uploadImage(uri) { result ->
      result.fold(
        onSuccess = { url ->
          val body = arrayListOf("title" to title, "challenges" to challenges, "photo_url" to url)

          description?.let {
            body.add("description" to it)
          }

          duration?.let {
            body.add("duration" to it)
          }

          distance?.let {
            body.add("distance" to it)
          }

          calories?.let {
            body.add("calories" to it)
          }

          steps?.let {
            body.add("steps" to it)
          }

          points?.let {
            body.add("points" to it)
          }

          Fuel.post("/workouts", body)
            .validate { true }
            .responseObject(gsonGuy, handleObject(handler))
        },
        onFailure = { error ->
          handler(Result.failure(error))
        }
      )
    }
  }

  fun leave(challenge: Challenge, handler: (Result<Challenge>) -> Unit) {
    Fuel.delete("memberships/${challenge.id}")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun deleteWorkout(workout: Workout, handler: (Result<Workout>) -> Unit) {
    Fuel.delete("workouts/${workout.id}")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun rankings(challenge: Challenge, handler: (Result<List<Ranking>>) -> Unit) {
    Fuel.get("challenges/${challenge.id}/rankings")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun updateAccount(profilePictureUrl: String? = null, email: String? = null, name: String? = null, password: String? = null, currentPassword: String? = null, handler: (Result<Account>) -> Unit) {
    val body = ArrayList<Pair<String, String>>()

    profilePictureUrl?.let { body.add("profile_picture_url" to it) }
    email?.let { body.add("email" to it) }
    name?.let { body.add("full_name" to it) }
    password?.let { body.add("password" to it) }
    currentPassword?.let { body.add("current_password" to it) }

    Fuel.put("/accounts/self", body)
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun getWorkouts(account: Account, challenge: Challenge, handler: (Result<List<Workout>>) -> Unit) {
    Fuel.get("challenges/${challenge.id}/members/${account.id}/workouts")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun getAllWorkouts(challenge: Challenge, handler: (Result<List<Workout>>) -> Unit) {
    Fuel.get("/challenges/${challenge.id}/workouts")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun resetPassword(email: String, handler: (Result<LinkedTreeMap<Any, Any>>) -> Unit) {
    Fuel.post("/passwords", listOf("email" to email))
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun getChallengeInfo(challenge: Challenge, handler: (Result<ChallengeInfo>) -> Unit) {
    Fuel.get("/challenges/${challenge.id}/info")
      .validate { true }
      .responseObject(gsonGuy, handleObject(handler))
  }

  fun setBaseHeaders() {
    FuelManager.instance.baseHeaders = headers()
  }

  private fun atStartOfDay(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    calendar.timeZone = TimeZone.getTimeZone("UTC")

    return calendar.time
  }

  private fun headers(): Map<String, String> {
    return mapOf("Authorization" to (AuthService.currentAccount?.token ?: ""))
  }

  private fun <T> handleObject(handler: (Result<T>) -> Unit): ResponseResultHandler<ServiceResponse<T>> {
    return { _, _, result ->
      Handler(Looper.getMainLooper()).post {
        handler(when(result) {
          is com.github.kittinunf.result.Result.Failure -> Result.failure(result.error)
          is com.github.kittinunf.result.Result.Success -> {
            when (result.value.status) {
              "success" -> Result.success(result.value.data!!)
              else -> Result.failure(ServiceResponseError(message = result.value.error ?: "Something unpredictable happened."))
            }
          }
        })
      }
    }
  }
}