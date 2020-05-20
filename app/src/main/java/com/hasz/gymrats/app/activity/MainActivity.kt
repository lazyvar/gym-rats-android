package com.hasz.gymrats.app.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.work.Logger
import com.bumptech.glide.Glide
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.service.AuthService

class MainActivity : AppCompatActivity() {
  private lateinit var appBarConfiguration: AppBarConfiguration

  @SuppressLint("RestrictedApi")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_main)

    val toolbar: Toolbar = findViewById(R.id.toolbar)
    val fab: FloatingActionButton = findViewById(R.id.fab)
    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    val navView: NavigationView = findViewById(R.id.nav_view)
    val navController = findNavController(R.id.nav_host_fragment)

    setSupportActionBar(toolbar)

    fab.setOnClickListener { _ ->
      // TODO: fabulous
    }

    navView.apply {
      val header = LayoutInflater.from(context).inflate(R.layout.nav_header_main, null)
      val name = header.findViewById<TextView>(R.id.name)
      val email = header.findViewById<TextView>(R.id.email)
      val imageView = header.findViewById<ImageView>(R.id.imageView)

      name.text = AuthService.currentAccount.full_name
      email.text = AuthService.currentAccount.email

      AuthService.currentAccount.profile_picture_url?.let {
        Glide.with(this)
          .load(it)
          .circleCrop()
          .into(imageView)
      }

      addHeaderView(header)
    }

    appBarConfiguration = AppBarConfiguration(
      setOf(
        R.id.nav_home,
        R.id.nav_settings,
        R.id.nav_about
      ), drawerLayout
    )

    setupActionBarWithNavController(navController, appBarConfiguration)
    navView.setupWithNavController(navController)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)

    return true
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment)

    return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }
}
