package com.hasz.gymrats.app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.hasz.gymrats.app.R

class WelcomeFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_welcome, container, false)
    val loginButton = view.findViewById<Button>(R.id.log_in)

    loginButton.setOnClickListener {
      print("WOOOOO")

      val direction = WelcomeFragmentDirections
        .welcomeToLogin()

      view.findNavController().navigate(direction)
    }

    return view
  }
}
