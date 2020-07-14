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
import com.hasz.gymrats.app.model.*
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.hasz.gymrats.app.typeadapter.LocalDateTimeConverter
import org.threeten.bp.LocalDateTime

object GymRatsApi {
  private const val baseUrl = "https://gym-rats-api-pre-production.gigalixirapp.com"
  private val gsonGuy: Gson

  init {
    FuelManager.instance.basePath = baseUrl
    gsonGuy = GsonBuilder()
      .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConverter())
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

  fun updateAccount(email: String? = null, name: String? = null, password: String? = null, currentPassword: String? = null, handler: (Result<Account>) -> Unit) {
    val body = ArrayList<Pair<String, String>>()

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