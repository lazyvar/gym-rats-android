package com.hasz.gymrats.app.application

import android.app.Application
import android.content.Context
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.hasz.gymrats.app.service.GService
import com.jakewharton.threetenabp.AndroidThreeTen
import io.branch.referral.Branch

class GymRatsApplication: Application() {
  override fun onCreate() {
    super.onCreate()

    Branch.enableLogging();
    Branch.getAutoInstance(this);
    FirebaseApp.initializeApp(this)
    AndroidThreeTen.init(this)
    context = applicationContext
  }

  companion object {
    var context: Context? = null
  }
}