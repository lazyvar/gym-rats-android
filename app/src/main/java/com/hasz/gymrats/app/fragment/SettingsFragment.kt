package com.hasz.gymrats.app.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.LogWorkoutActivity
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.activity.WelcomeActivity
import com.hasz.gymrats.app.adapter.SettingsAdapter
import com.hasz.gymrats.app.databinding.FragmentSettingsBinding
import com.hasz.gymrats.app.model.SettingsRow
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GService
import com.hasz.gymrats.app.service.GymRatsApi

class SettingsFragment: Fragment() {
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager
  private lateinit var binding: FragmentSettingsBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    (context as? MainActivity)?.supportActionBar?.title = "Settings"

    return DataBindingUtil.inflate<FragmentSettingsBinding>(
      inflater, R.layout.fragment_settings, container, false
    ).apply {
      binding = this
      refresh()
    }.root
  }

  private fun refresh() {
    binding.apply {
      val account = AuthService.currentAccount!!

      viewManager = LinearLayoutManager(context)
      viewAdapter = SettingsAdapter(
        arrayListOf(
          SettingsRow(headerText = "PROFILE", leftText = null, rightText = null, action = { }),
          SettingsRow(headerText = null, leftText = "Email", rightText = account.email, action = {
            findNavController().navigate(
              SettingsFragmentDirections.changeEmail()
            )
          }),
          SettingsRow(
            headerText = null,
            leftText = "Profile picture",
            rightText = null,
            isProfilePicture = true,
            action = {
              ImagePicker
                .with(requireContext() as MainActivity)
                .compress(1024)
                .start { resultCode, data ->
                  when (resultCode) {
                    Activity.RESULT_OK -> {
                      val fileUri = data?.data

                      Handler(Looper.getMainLooper()).post {
                        progressBar.visibility = View.VISIBLE
                      }

                      GService.uploadImage(fileUri!!) { result ->
                        result.fold(
                          onSuccess = { url ->
                            GymRatsApi.updateAccount(profilePictureUrl = url) { result ->
                              result.fold(
                                onSuccess = { account ->
                                  Handler(Looper.getMainLooper()).post {
                                    progressBar.visibility = View.INVISIBLE
                                  }

                                  AuthService.storeAccount(account = account)
                                  refresh()
                                },
                                onFailure = { error ->
                                  Handler(Looper.getMainLooper()).post {
                                    progressBar.visibility = View.INVISIBLE
                                  }

                                  Snackbar.make(
                                    recyclerView,
                                    error.message ?: "Something unpredictable happened.",
                                    Snackbar.LENGTH_LONG
                                  ).show()
                                }
                              )
                            }
                          },
                          onFailure = { error ->
                            Handler(Looper.getMainLooper()).post {
                              progressBar.visibility = View.INVISIBLE
                            }

                            Snackbar.make(
                              recyclerView,
                              error.message ?: "Something unpredictable happened.",
                              Snackbar.LENGTH_LONG
                            ).show()
                          }
                        )
                      }
                    }
                    ImagePicker.RESULT_ERROR -> {
                      Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    }
                  }
                }
            }),
          SettingsRow(
            headerText = null,
            leftText = "Name",
            rightText = account.full_name,
            action = {
              findNavController().navigate(
                SettingsFragmentDirections.changeName()
              )
            }),
          SettingsRow(headerText = null, leftText = "Password", rightText = "••••••", action = {
            findNavController().navigate(
              SettingsFragmentDirections.changePassword()
            )
          }),
          SettingsRow(headerText = "APP INFO", leftText = null, rightText = null, action = { }),
          SettingsRow(
            headerText = null,
            leftText = "Google Play page",
            rightText = null,
            action = {
              val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.hasz.gymrats.app"))

              startActivity(browserIntent)
            }),
          SettingsRow(headerText = null, leftText = "Terms of Service", rightText = null, action = {
            val browserIntent =
              Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gymrats.app/terms/"))

            startActivity(browserIntent)
          }),
          SettingsRow(headerText = null, leftText = "Privacy Policy", rightText = null, action = {
            val browserIntent =
              Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gymrats.app/privacy/"))

            startActivity(browserIntent)
          }),
          SettingsRow(headerText = null, leftText = "Support", rightText = null, action = {
            val emailIntent = Intent(
              Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "help@gymrats.app", null
              )
            )

            startActivity(Intent.createChooser(emailIntent, "Send email..."))
          }),
          SettingsRow(headerText = "ACCOUNT", leftText = null, rightText = null, action = { }),
          SettingsRow(headerText = null, leftText = "Sign out", rightText = null, action = {
            AuthService.logout()

            val intent = Intent().apply {
              context?.let { it -> setClass(it, WelcomeActivity::class.java) }
              flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            startActivity(intent)
            activity?.finish()
          })
        )
      )

      recyclerView.adapter = viewAdapter
      recyclerView.layoutManager = viewManager
    }
  }
}
