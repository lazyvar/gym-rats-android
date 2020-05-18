package com.hasz.gymrats.app.service

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseResultHandler
import com.github.kittinunf.fuel.gson.responseObject
import com.hasz.gymrats.app.model.Account
import com.hasz.gymrats.app.model.ServiceResponse
import com.hasz.gymrats.app.model.ServiceResponseError

object GymRatsApi {
  private const val baseUrl = "https://gym-rats-api-pre-production.gigalixirapp.com"

  init {
    FuelManager.instance.basePath = baseUrl
  }

  fun login(email: String, password: String, handler: (Result<Account>) -> Unit) {
    Fuel.post("/tokens", listOf("email" to email, "password" to password))
      .validate { true }
      .responseObject(handleObject<Account>(handler))
  }

  fun createAccount(email: String, password: String, fullName: String, handler: (Result<Account>) -> Unit) {
    Fuel.post("/accounts", listOf("email" to email, "password" to password, "full_name" to fullName))
      .validate { true }
      .responseObject(handleObject<Account>(handler))
  }

  private fun <T> handleObject(handler: (Result<T>) -> Unit): ResponseResultHandler<ServiceResponse<T>> {
    return { _, _, result ->
      val result: Result<T> = when(result) {
        is com.github.kittinunf.result.Result.Failure -> Result.failure(result.error)
        is com.github.kittinunf.result.Result.Success -> {
          when (result.value.status) {
            "success" -> Result.success(result.value.data!!)
            else -> Result.failure(ServiceResponseError(message = result.value.error ?: "Something unpredictable happened."))
          }
        }
      }

      Handler(Looper.getMainLooper()).post {
        handler(result)
      }
    }
  }
}