package com.hasz.gymrats.app.service

import android.os.Handler
import android.os.Looper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseResultHandler
import com.github.kittinunf.fuel.gson.responseObject
import com.hasz.gymrats.app.model.Account
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.ServiceResponse
import com.hasz.gymrats.app.model.ServiceResponseError
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object GymRatsApi {
  private const val baseUrl = "https://gym-rats-api-pre-production.gigalixirapp.com"

  init {
    FuelManager.instance.basePath = baseUrl
    setBaseHeaders()
  }

  fun login(email: String, password: String, handler: (Result<Account>) -> Unit) {
    Fuel.post("/tokens", listOf("email" to email, "password" to password))
      .validate { true }
      .responseObject(handleObject(handler))
  }

  fun createAccount(email: String, password: String, fullName: String, handler: (Result<Account>) -> Unit) {
    Fuel.post("/accounts", listOf("email" to email, "password" to password, "full_name" to fullName))
      .validate { true }
      .responseObject(handleObject(handler))
  }

  fun joinChallenge(code: String, handler: (Result<Challenge>) -> Unit) {
    Fuel.post("/memberships", listOf("code" to code))
      .validate { true }
      .responseObject(handleObject(handler))
  }

  fun allChallenges(handler: (Result<List<Challenge>>) -> Unit) {
    Fuel.get("/challenges")
      .validate { true }
      .responseObject(handleObject(handler))
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
      .responseObject(handleObject(handler))
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