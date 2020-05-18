package com.hasz.gymrats.app.application

import android.app.Application
import android.content.Context

class GymRatsApplication: Application() {
  override fun onCreate() {
    super.onCreate()

    context = applicationContext
  }

  companion object {
    var context: Context? = null
  }
}