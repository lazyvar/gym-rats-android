package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentNoActiveChallengesBinding

class NoActiveChallengesFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentNoActiveChallengesBinding>(
      inflater, R.layout.fragment_no_active_challenges, container, false
    ).apply {

    }.root
  }
}