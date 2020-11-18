package com.hasz.gymrats.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.service.JoinCodeService
import io.branch.referral.Branch
import io.branch.referral.Branch.BranchReferralInitListener

class GymRatsRootActivity: AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val firstActivity = AuthService.retrieveAccount()?.let {
      MainActivity()
    } ?: run {
      WelcomeActivity()
    }

    val intent = Intent().apply {
      setClass(applicationContext, firstActivity::class.java)
    }

    startActivity(intent)
  }

  override fun onStart() {
    super.onStart()

    Branch.sessionBuilder(this)
      .withCallback(branchReferralInitListener)
      .withData(if (intent != null) intent.data else null).init()
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    setIntent(intent)

    Branch.sessionBuilder(this)
      .withCallback(branchReferralInitListener).reInit()
  }

  fun showChallengePreview(code: String) {
    GymRatsApi.getChallenge(code = code) { result ->
      result.fold(
        onSuccess = { challenges ->
          if (challenges.isEmpty()) {
            return@getChallenge
          }

          val intent = Intent().apply {
            setClass(applicationContext, ChallengePreviewActivity::class.java)
            putExtra("challenge", challenges.first())
          }

          startActivityForResult(intent, 999)
        },
        onFailure = { error ->
          // ...
        }
      )
    }
  }

  private val branchReferralInitListener = BranchReferralInitListener { linkProperties, error ->
    if (linkProperties == null) {
      return@BranchReferralInitListener
    }

    if (!linkProperties.has("code")) {
      return@BranchReferralInitListener
    }

    val code = linkProperties.getString("code")

    if (AuthService.retrieveAccount() == null) {
      JoinCodeService.store(code)
    } else {
      showChallengePreview(code)
    }
  }
}
