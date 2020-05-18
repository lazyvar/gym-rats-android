package com.hasz.gymrats.app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

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

    }.root
  }
}
