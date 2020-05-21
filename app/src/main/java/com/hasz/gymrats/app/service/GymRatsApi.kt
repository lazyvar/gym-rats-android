package com.hasz.gymrats.app.service

import android.annotation.SuppressLint
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

  fun setBaseHeaders() {
    FuelManager.instance.baseHeaders = headers()
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