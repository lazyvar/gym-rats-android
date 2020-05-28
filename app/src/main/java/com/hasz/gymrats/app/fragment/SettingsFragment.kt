package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.adapter.CompletedChallengesAdapter
import com.hasz.gymrats.app.adapter.SettingsAdapter
import com.hasz.gymrats.app.databinding.FragmentCompletedChallengesBinding
import com.hasz.gymrats.app.databinding.FragmentSettingsBinding
import com.hasz.gymrats.app.extension.completed
import com.hasz.gymrats.app.model.SettingsRow
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi
import kotlinx.android.synthetic.main.item_table_view_cell.*

class SettingsFragment: Fragment() {
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val account = AuthService.currentAccount!!

    viewManager = LinearLayoutManager(context)
    viewAdapter = SettingsAdapter(arrayListOf(
      SettingsRow(headerText = "PROFILE", leftText = null, rightText = null, action = { }),
      SettingsRow(headerText = null, leftText = "Email", rightText = account.email, action = { }),
      SettingsRow(headerText = null, leftText = "Profile picture", rightText = null, action = { }),
      SettingsRow(headerText = null, leftText = "Name", rightText = account.full_name, action = { }),
      SettingsRow(headerText = null, leftText = "Password", rightText = "••••••", action = { }),
      SettingsRow(headerText = "APP INFO", leftText = null, rightText = null, action = { }),
      SettingsRow(headerText = null, leftText = "Google Play page", rightText = null, action = { }),
      SettingsRow(headerText = null, leftText = "Terms of Service", rightText = null, action = { }),
      SettingsRow(headerText = null, leftText = "Privacy Policy", rightText = null, action = { }),
      SettingsRow(headerText = null, leftText = "Support", rightText = null, action = { }),
      SettingsRow(headerText = "ACCOUNT", leftText = null, rightText = null, action = { }),
      SettingsRow(headerText = null, leftText = "Sign out", rightText = null, action = { })
    ))

    return DataBindingUtil.inflate<FragmentSettingsBinding>(
      inflater, R.layout.fragment_settings, container, false
    ).apply {
      recyclerView.adapter = viewAdapter
      recyclerView.layoutManager = viewManager
    }.root
  }
}
