package com.hasz.gymrats.app.activity

import android.R
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hasz.gymrats.app.service.AuthService
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

  private val branchReferralInitListener =
    BranchReferralInitListener { linkProperties, error ->
      print(linkProperties)
      print(error)
    }
}
