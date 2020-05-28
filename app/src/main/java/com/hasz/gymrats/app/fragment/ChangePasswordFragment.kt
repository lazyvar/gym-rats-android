package com.hasz.gymrats.app.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentChangePasswordBinding
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi

class ChangePasswordFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentChangePasswordBinding>(
      inflater, R.layout.fragment_change_password, container, false
    ).apply {
      progressBar.visibility = View.INVISIBLE

      changePasswordButton.setOnClickListener {
        val currentPassword = currentPassword.editText?.text.toString()
        val newPassword = newPassword.editText?.text.toString()
        val confirmNewPassword = confirmNewPassword.editText?.text.toString()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
          return@setOnClickListener
        }

        if (newPassword.count() < 6) {
          Snackbar.make(it, "New password not long enough.", Snackbar.LENGTH_LONG).show()

          return@setOnClickListener
        }

        if (confirmNewPassword != newPassword) {
          Snackbar.make(it, "Password's do not match.", Snackbar.LENGTH_LONG).show()

          return@setOnClickListener
        }

        changePasswordButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        GymRatsApi.updateAccount(password = newPassword, currentPassword = currentPassword) { result ->
          changePasswordButton.isEnabled = true
          progressBar.visibility = View.INVISIBLE

          result.fold(
            onSuccess = { account ->
              AuthService.storeAccount(account = account)
              val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
              val currentFocusedView = requireActivity().currentFocus

              if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(
                  currentFocusedView.windowToken,
                  InputMethodManager.HIDE_NOT_ALWAYS
                )
              }

              findNavController().popBackStack()
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