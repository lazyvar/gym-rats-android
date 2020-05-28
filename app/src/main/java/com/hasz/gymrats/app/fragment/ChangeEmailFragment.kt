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
import com.hasz.gymrats.app.databinding.FragmentChangeEmailBinding
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi

class ChangeEmailFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentChangeEmailBinding>(
      inflater, R.layout.fragment_change_email, container, false
    ).apply {
      email.editText?.setText(AuthService.currentAccount?.email ?: "")
      progressBar.visibility = View.INVISIBLE

      changeEmailButton.setOnClickListener {
        val email = email.editText?.text.toString()

        if (email.isEmpty()) {
          return@setOnClickListener
        }

        changeEmailButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        GymRatsApi.updateAccount(email = email) { result ->
          changeEmailButton.isEnabled = true
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
              Snackbar.make(
                it,
                error.message ?: "Something unpredictable happened.",
                Snackbar.LENGTH_LONG
              ).show()
            }
          )
        }
      }
    }.root
  }
}