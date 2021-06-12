package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentNotificationSettingsBinding
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi

class NotificationSettingsFragment: Fragment() {
  private lateinit var binding: FragmentNotificationSettingsBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentNotificationSettingsBinding>(
      inflater, R.layout.fragment_notification_settings, container, false
    ).apply {
      binding = this

      val account = AuthService.currentAccount!!

      workoutSwitch.isChecked = account.workout_notifications_enabled
      commentSwitch.isChecked = account.comment_notifications_enabled
      chatMessageSwitch.isChecked = account.chat_message_notifications_enabled

      workoutSwitch.setOnCheckedChangeListener { _, _ ->
        update()
      }

      commentSwitch.setOnCheckedChangeListener { _, _ ->
        update()
      }

      chatMessageSwitch.setOnCheckedChangeListener { _, _ ->
        update()
      }
    }.root
  }

  private fun update() {
    GymRatsApi.updateNotificationSettings(workouts = binding.workoutSwitch.isChecked, comments = binding.commentSwitch.isChecked, chatMessages = binding.chatMessageSwitch.isChecked) { result ->
      result.fold(
        onSuccess = { account ->
          AuthService.storeAccount(account)
        },
        onFailure = { error ->
          Snackbar.make(
            requireView(),
            error.message ?: "Something unpredictable happened.",
            Snackbar.LENGTH_LONG
          ).show()
        }
      )
    }
  }
}