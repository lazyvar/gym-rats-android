package com.hasz.gymrats.app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentWelcomeBinding

class WelcomeFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentWelcomeBinding>(
      inflater, R.layout.fragment_welcome, container, false
    ).apply {
      loginButton.setOnClickListener {
        findNavController().navigate(
          WelcomeFragmentDirections.login()
        )
      }

      getStartedButton.setOnClickListener {
        findNavController().navigate(
          WelcomeFragmentDirections.signUp()
        )
      }
    }.root
  }
}
