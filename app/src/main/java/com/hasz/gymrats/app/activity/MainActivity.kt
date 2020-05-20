package com.hasz.gymrats.app.activity

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.hasz.gymrats.app.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_main)

    val navController = findNavController(R.id.nav_host)
    val appBarConfiguration = AppBarConfiguration(navController.graph)

    collapsingToolbarLayout.setupWithNavController(toolbar, navController, appBarConfiguration)
    setSupportActionBar(toolbar)

    supportActionBar?.apply {
      setHomeButtonEnabled(true)
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(android.R.drawable.ic_menu_always_landscape_portrait)
    }

    toolbar.setNavigationOnClickListener {
      drawerLayout.openDrawer(Gravity.START)
    }
  }
}