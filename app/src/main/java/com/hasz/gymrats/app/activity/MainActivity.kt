package com.hasz.gymrats.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.service.AuthService

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var navController: NavController
  private lateinit var drawer: DrawerLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_main)

    val toolbar: Toolbar = findViewById(R.id.toolbar)
    val navView: NavigationView = findViewById(R.id.nav_view)

    drawer = findViewById(R.id.drawer_layout)
    navController = findNavController(R.id.nav_host_fragment)

    setSupportActionBar(toolbar)

    navView.apply {
      val header = LayoutInflater.from(context).inflate(R.layout.nav_header_main, null)
      val name = header.findViewById<TextView>(R.id.name)
      val email = header.findViewById<TextView>(R.id.email)
      val imageView = header.findViewById<ImageView>(R.id.imageView)

      name.text = AuthService.currentAccount!!.full_name
      email.text = AuthService.currentAccount!!.email

      AuthService.currentAccount!!.profile_picture_url?.let {
        Glide.with(this)
          .load(it)
          .circleCrop()
          .into(imageView)
      }

      addHeaderView(header)
    }

    appBarConfiguration = AppBarConfiguration(
      setOf(
        R.id.home,
        R.id.nav_challenge_bottom_nav,
        R.id.nav_completed_challenges,
        R.id.nav_settings,
        R.id.nav_about
      ), drawer
    )

    setupActionBarWithNavController(navController, appBarConfiguration)
    navView.setupWithNavController(navController)
    navView.setNavigationItemSelectedListener(this)
  }

//  override fun onCreateOptionsMenu(menu: Menu): Boolean {
//    menuInflater.inflate(R.menu.main, menu)
//
//    return true
//  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment)

    return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    return when(item.itemId) {
      R.id.nav_join_challenge -> {
        val intent = Intent().apply {
          applicationContext?.let { setClass(it, JoinChallengeActivity::class.java) }
        }

        startActivity(intent)

        false
      }
      R.id.nav_create_challenge -> {
        val intent = Intent().apply {
          applicationContext?.let { setClass(it, CreateChallengeActivity::class.java) }
        }

        startActivity(intent)

        false
      }
      else -> {
        NavigationUI.onNavDestinationSelected(item, navController)
        drawer.closeDrawer(Gravity.START)

        true
      }
    }
  }
}


