package com.hasz.gymrats.app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController

import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentLogInBinding

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

      loginButton.setOnClickListener {

      }
    }.root
  }
}
