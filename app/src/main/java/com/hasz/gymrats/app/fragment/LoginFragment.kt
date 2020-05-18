package com.hasz.gymrats.app.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.transition.Visibility
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.work.Logger
import com.google.android.material.snackbar.Snackbar

import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentLogInBinding
import com.hasz.gymrats.app.service.GymRatsApi

class LoginFragment: Fragment() {

  @SuppressLint("RestrictedApi")
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentLogInBinding>(
      inflater, R.layout.fragment_log_in, container, false
    ).apply {
      toolbar.setNavigationOnClickListener {
        findNavController().popBackStack()
      }

      loginButton.setOnClickListener {
        val email = email.editText?.text?.toString() ?: ""
        val password = password.editText?.text?.toString() ?: ""

        if (email.isEmpty() || password.isEmpty()) {
          return@setOnClickListener
        }

        loginButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        Logger.LogcatLogger.get().info("Mack - in button", Thread.currentThread().name)

        GymRatsApi.login(email, password) { result ->
          loginButton.isEnabled = true
          progressBar.visibility = View.INVISIBLE

          result.fold(
            onSuccess = { account ->

            },
            onFailure = { error ->
              Snackbar.make(it, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
            }
          )
        }
      }
    }.root
  }
}
