package com.hasz.gymrats.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.LogWorkoutActivity
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.databinding.FragmentChallengeBottomNavBinding
import com.hasz.gymrats.app.model.Challenge
import java.io.File

class ChallengeBottomNavFragment: Fragment() {
  private lateinit var challenge: Challenge
  private var savedView: View? = null

  companion object {
    fun newInstance(challenge: Challenge): ChallengeBottomNavFragment {
      return ChallengeBottomNavFragment().also {
        it.arguments = Bundle().also { b -> b.putParcelable("challenge", challenge) }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (savedView != null) {
      return savedView
    }

    challenge = requireArguments().getParcelable("challenge")!!
    (context as? MainActivity)?.supportActionBar?.title = challenge.name

    savedView = DataBindingUtil.inflate<FragmentChallengeBottomNavBinding>(
      inflater, R.layout.fragment_challenge_bottom_nav, container, false
    ).apply {
      (context as? MainActivity)?.supportFragmentManager.let { manager ->
        if (manager == null) { return@let }

        val fragmentTransaction = manager.beginTransaction()
        val fragment = ChallengeFragment.newInstance(challenge)

        fragmentTransaction.add(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()

        fab.setOnClickListener {
          ImagePicker
            .with(requireContext() as MainActivity)
            .compress(1024)
            .start { resultCode, data ->
              when (resultCode) {
                Activity.RESULT_OK -> {
                  val fileUri = data?.data
                  val intent = Intent().apply {
                    setClass(requireContext(), LogWorkoutActivity::class.java)
                    putExtra("workout_image_uri", fileUri)
                  }

                  startActivity(intent)
                }
                ImagePicker.RESULT_ERROR -> {
                  Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
              }
            }
        }

        bottomAppBar.setNavigationOnClickListener {
          // TODO: push awards
        }
      }
    }.root

    return savedView
  }
}
