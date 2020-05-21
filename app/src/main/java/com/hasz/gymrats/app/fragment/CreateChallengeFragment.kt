package com.hasz.gymrats.app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentCreateChallengeBinding
import com.hasz.gymrats.app.databinding.FragmentJoinChallengeBinding

/**
 * A simple [Fragment] subclass.
 */
class CreateChallengeFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentCreateChallengeBinding>(
      inflater, R.layout.fragment_create_challenge, container, false
    ).apply {

    }.root
  }
}
