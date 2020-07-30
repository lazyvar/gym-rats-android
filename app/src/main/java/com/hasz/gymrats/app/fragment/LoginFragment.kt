package com.hasz.gymrats.app.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.databinding.FragmentLogInBinding
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi


class LoginFragment: Fragment() {

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

      resetPasswordButton.setOnClickListener {
        val input = TextInputEditText(requireContext())
        input.hint = "Enter email"

        AlertDialog.Builder(requireContext())
          .setTitle("Reset password")
          .setMessage("You will receive an email with next steps.")
          .setView(input)
          .setCancelable(false)
          .setPositiveButton("Send") { _, _ ->
            GymRatsApi.resetPassword(input.text.toString() ?: "") { result ->
              result.fold(
                onSuccess = { _ ->
                  Snackbar.make(it, "Success! Check your email.", Snackbar.LENGTH_LONG).show()
                },
                onFailure = { error ->
                  Snackbar.make(it, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
                }
              )
            }
          }
          .setNeutralButton(android.R.string.no, null)
          .setIcon(android.R.drawable.ic_dialog_alert)
          .show()
      }

      loginButton.setOnClickListener {
        val email = email.editText?.text?.toString() ?: ""
        val password = password.editText?.text?.toString() ?: ""

        if (email.isEmpty() || password.isEmpty()) {
          return@setOnClickListener
        }

        loginButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        GymRatsApi.login(email, password) { result ->
          loginButton.isEnabled = true
          progressBar.visibility = View.INVISIBLE

          result.fold(
            onSuccess = { account ->
              AuthService.storeAccount(account = account)

              val intent = Intent().apply {
                context?.let { it -> setClass(it, MainActivity::class.java) }
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
              }

              startActivity(intent)
              activity?.finish()
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
