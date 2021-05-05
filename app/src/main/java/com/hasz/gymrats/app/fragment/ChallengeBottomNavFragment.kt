package com.hasz.gymrats.app.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.BuildConfig
import com.hasz.gymrats.app.MainNavigationDirections
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.LogWorkoutActivity
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.databinding.FragmentChallengeBottomNavBinding
import com.hasz.gymrats.app.extension.activeOrUpcoming
import com.hasz.gymrats.app.extension.isActive
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.refreshable.Refreshable
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState

class ChallengeBottomNavFragment: Fragment() {
  private lateinit var challengeFragment: ChallengeFragment
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
    if (savedView != null) { return savedView }

    setHasOptionsMenu(true)

    challenge = arguments?.getParcelable("challenge")
      ?: ChallengeState.allChallenges.firstOrNull { it.id == ChallengeState.lastOpenedChallengeId }
      ?: ChallengeState.allChallenges.first()

    (context as? MainActivity)?.supportActionBar?.title = challenge.name

    savedView = DataBindingUtil.inflate<FragmentChallengeBottomNavBinding>(
      inflater, R.layout.fragment_challenge_bottom_nav, container, false
    ).apply {
      (context as? MainActivity)?.supportFragmentManager.let { manager ->
        if (manager == null) { return@let }

        val fragmentTransaction = manager.beginTransaction()
        challengeFragment = ChallengeFragment.newInstance(challenge)

        fragmentTransaction.add(R.id.fragmentContainer, challengeFragment)
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

                  startActivityForResult(intent, 65533)
                }
                ImagePicker.RESULT_ERROR -> {
                  Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
              }
            }
        }

        bottomAppBar.setNavigationOnClickListener {
          findNavController().navigate(
            ChallengeBottomNavFragmentDirections.stats(challenge)
          )
        }

        bottomAppBar.setOnMenuItemClickListener {
          findNavController().navigate(
            ChallengeBottomNavFragmentDirections.chat(challenge)
          )

          true
        }
      }
    }.root

    return savedView
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)

    menu.clear()

    inflater.inflate(R.menu.challenge_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when(item.itemId) {
      R.id.nav_leave -> {
        AlertDialog.Builder(context)
          .setTitle("Leave challenge")
          .setMessage("Are you sure you want to leave ${challenge.name}?")
          .setPositiveButton(android.R.string.yes) { _, _ ->

            GymRatsApi.leave(challenge) { result ->
              result.fold(
                onSuccess = { _ ->
                  GymRatsApi.allChallenges { result ->
                    result.fold(
                      onSuccess = { challenges ->
                        ChallengeState.lastOpenedChallengeId = 0
                        ChallengeState.allChallenges = challenges

                        val activeOrUpcoming = challenges.activeOrUpcoming()
                        val nav = findNavController()
                        (requireContext() as MainActivity).updateNav(activeOrUpcoming)

                        if (activeOrUpcoming.isEmpty()) {
                          nav.navigate(HomeFragmentDirections.noChallenges())
                        } else {
                          val challenge = activeOrUpcoming.firstOrNull { it.id == ChallengeState.lastOpenedChallengeId } ?: activeOrUpcoming.first()

                          if (challenge.isActive()) {
                            nav.navigate(MainNavigationDirections.challengeBottomNav(challenge))
                          } else {
                            nav.navigate(MainNavigationDirections.upcomingChallenge(challenge))
                          }
                        }

                        (context as? MainActivity)?.supportActionBar?.setHomeButtonEnabled(false)
                        (context as? MainActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                      },
                      onFailure = { error ->
                        Snackbar.make(requireView(), error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
                      }
                    )
                  }
                },
                onFailure = { error ->
                  Snackbar.make(requireView(), error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
                }
              )
            }
          }
          .setCancelable(false)
          .setNeutralButton(android.R.string.no, null)
          .setIcon(android.R.drawable.ic_dialog_alert)
          .show()

        true
      }
      R.id.nav_invite -> {
        var link = BuildConfig.INVITE_URL + challenge.code
        val message = "Ready for a challenge? Join me in \"${challenge.name}\" $link"
        val sendIntent: Intent = Intent().apply {
          action = Intent.ACTION_SEND
          putExtra(Intent.EXTRA_TEXT, message)
          type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

        true
      }
      R.id.nav_edit -> {
        findNavController().navigate(
          ChallengeBottomNavFragmentDirections.edit(challenge)
        )

        true
      }
      else -> { super.onOptionsItemSelected(item) }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode == 9114112 && requestCode == 65533) {
      challengeFragment.refresh()
    }
  }
}
