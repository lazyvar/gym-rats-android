package com.hasz.gymrats.app.fragment

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
import com.hasz.gymrats.app.databinding.FragmentHomeBinding
import com.hasz.gymrats.app.service.GymRatsApi

class HomeFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentHomeBinding>(
      inflater, R.layout.fragment_home, container, false
    ).apply {
      GymRatsApi.allChallenges { result ->
        result.fold(
          onSuccess = { challenges ->
            progressBar.visibility = View.GONE

            findNavController().navigate(HomeFragmentDirections.challenge(challenges.first()))
            (context as? MainActivity)?.reloadNavGraph()
//              val activeChallenges = challenges.active()
//              if (activeChallenges.isEmpty()) {
//                val main = (context as MainActivity)
//                val tx = main.supportFragmentManager.beginTransaction()
//
//                tx.replace(R.id.fragment_home, NoActiveChallengesFragment())
//                tx.commit()
//              } else {
//                // TODO
//              }
          },
          onFailure = { error ->
            Snackbar.make(root, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
          }
        )
      }
    }.root
  }
}
