package com.hasz.gymrats.app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.databinding.FragmentChangeNameBinding
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi

class ChangeNameFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentChangeNameBinding>(
      inflater, R.layout.fragment_change_name, container, false
    ).apply {
      name.editText?.setText(AuthService.currentAccount?.full_name ?: "")

      changeNameButton.setOnClickListener {
        val name = name.editText?.text.toString()

        if (name.isEmpty()) {
          return@setOnClickListener
        }

        changeNameButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        GymRatsApi.updateAccount(name = name) { result ->
          changeNameButton.isEnabled = true
          progressBar.visibility = View.INVISIBLE

          result.fold(
            onSuccess = { account ->
              AuthService.storeAccount(account = account)
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