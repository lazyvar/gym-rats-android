package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentAboutBinding>(
      inflater, R.layout.fragment_about, container, false
    ).apply {
      textView.movementMethod = LinkMovementMethod.getInstance()
    }.root
  }
}
