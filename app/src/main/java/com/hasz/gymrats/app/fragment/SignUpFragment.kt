package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentSignUpBinding
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi

class SignUpFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentSignUpBinding>(
      inflater, R.layout.fragment_sign_up, container, false
    ).apply {
      toolbar.setNavigationOnClickListener {
        findNavController().popBackStack()
      }

      footerText.movementMethod = LinkMovementMethod.getInstance()

      createAccountButton.setOnClickListener {
        val email = email.editText?.text?.toString() ?: ""
        val name = name.editText?.text?.toString() ?: ""
        val password = password.editText?.text?.toString() ?: ""
        val confirmPassword = confirmPassword.editText?.text?.toString() ?: ""

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()) {
          return@setOnClickListener
        }

        if (password != confirmPassword) {
          Snackbar.make(it, "Those passwords do not match.", Snackbar.LENGTH_LONG).show()

          return@setOnClickListener
        }

        createAccountButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        GymRatsApi.createAccount(email = email, password = password, fullName = name) { result ->
          createAccountButton.isEnabled = true
          progressBar.visibility = View.INVISIBLE

          result.fold(
            onSuccess = { account ->
              AuthService.storeAccount(account = account)
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
