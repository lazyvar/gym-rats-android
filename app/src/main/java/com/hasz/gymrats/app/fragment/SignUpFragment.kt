package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentSignUpBinding

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


    }.root
  }
}
