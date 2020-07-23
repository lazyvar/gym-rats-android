package com.hasz.gymrats.app.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.MainNavigationDirections
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.adapter.UpcomingChallengeAdapter
import com.hasz.gymrats.app.databinding.FragmentChallengeBinding
import com.hasz.gymrats.app.databinding.FragmentUpcomingChallengeBinding
import com.hasz.gymrats.app.extension.activeOrUpcoming
import com.hasz.gymrats.app.extension.completed
import com.hasz.gymrats.app.extension.isActive
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState

class UpcomingChallengeFragment: Fragment() {
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager
  private lateinit var challenge: Challenge
  private lateinit var binding: FragmentUpcomingChallengeBinding

  private var savedView: View? = null

  companion object {
    fun newInstance(challenge: Challenge): UpcomingChallengeFragment {
      return UpcomingChallengeFragment().also {
        it.arguments = Bundle().also { b -> b.putParcelable("challenge", challenge) }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    challenge = requireArguments().getParcelable("challenge")!!
    (context as? MainActivity)?.supportActionBar?.title = challenge.name
    setHasOptionsMenu(true)

    if (savedView != null) { return savedView }

    viewManager = LinearLayoutManager(context)
    viewAdapter = UpcomingChallengeAdapter(challenge, arrayListOf())

    savedView = DataBindingUtil.inflate<FragmentUpcomingChallengeBinding>(
      inflater, R.layout.fragment_upcoming_challenge, container, false
    ).apply {
      recyclerView.adapter = viewAdapter
      recyclerView.layoutManager = viewManager

      recyclerView.visibility = View.GONE
      progressBar.visibility = View.VISIBLE

      GymRatsApi.getMembers(challenge) { result ->
        recyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        result.fold(
          onSuccess = { members ->
            viewAdapter = UpcomingChallengeAdapter(challenge, members)
            recyclerView.adapter = viewAdapter
          },
          onFailure = { error ->
            Snackbar.make(requireView(), error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
          }
        )
      }
    }.root

    return savedView
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)

    menu.clear()

    inflater.inflate(R.menu.upcoming_challenge_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
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
        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.data = Uri.parse("sms:")
        sendIntent.putExtra("sms_body", "Let's workout together! Download GymRats and join \"${challenge.name}\" using code ${challenge.code}.  https://www.gymrats.app")
        startActivity(sendIntent)

        true
      }
      R.id.nav_edit -> {
        findNavController().navigate(
          UpcomingChallengeFragmentDirections.edit(challenge)
        )

        true
      }
      R.id.nav_chat -> {
        findNavController().navigate(
          UpcomingChallengeFragmentDirections.chat(challenge)
        )

        true
      }
      else -> { super.onOptionsItemSelected(item) }
    }
  }
}