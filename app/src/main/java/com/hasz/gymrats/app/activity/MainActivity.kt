package com.hasz.gymrats.app.activity

import agency.tango.android.avatarview.views.AvatarView
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.MainNavigationDirections
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.extension.activeOrUpcoming
import com.hasz.gymrats.app.extension.isActive
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var navController: NavController
  private lateinit var drawer: DrawerLayout
  private val loader = GlideLoader()

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
      val avatarView = header.findViewById<AvatarView>(R.id.avatarView)

      name.text = AuthService.currentAccount!!.full_name
      email.text = AuthService.currentAccount!!.email
      loader.loadImage(avatarView!!, AuthService.currentAccount!!.profile_picture_url ?: "", AuthService.currentAccount!!.name)

      addHeaderView(header)
    }

    appBarConfiguration = AppBarConfiguration(
      setOf(
        R.id.home,
        R.id.nav_no_challenges,
        R.id.nav_upcoming_challenge,
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

  fun updateNav(challenges: List<Challenge>) {
    val navView: NavigationView = findViewById(R.id.nav_view)
    val menu = navView.menu

    menu.clear()

    val challengesMenu = menu.addSubMenu("Challenges")

    challenges.forEachIndexed { index, challenge ->
      challengesMenu.add(Menu.NONE, R.id.nav_challenge_bottom_nav, index, challenge.name)
    }

    menuInflater.inflate(R.menu.activity_main_drawer, menu)
    menu.removeItem(menu.findItem(R.id.nav_home).itemId)
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment)

    hideSoftKeyboard(this)

    return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode == 54321 && requestCode == 999) {
      GymRatsApi.allChallenges { result ->
        result.fold(
          onSuccess = { challenges ->
            drawer.closeDrawer(Gravity.START)

            ChallengeState.allChallenges = challenges

            val activeOrUpcoming = challenges.activeOrUpcoming()
            updateNav(activeOrUpcoming)

            if (activeOrUpcoming.isEmpty()) {
              navController.navigate(MainNavigationDirections.noChallenges())
            } else {
              val challenge = activeOrUpcoming.firstOrNull { it.id == ChallengeState.lastOpenedChallengeId } ?: activeOrUpcoming.first()

              navController.popBackStack()

              if (challenge.isActive()) {
                navController.navigate(MainNavigationDirections.challengeBottomNav(challenge))
              } else {
                navController.navigate(MainNavigationDirections.upcomingChallenge(challenge))
              }
            }
          },
          onFailure = { error ->
            Snackbar.make(drawer, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
          }
        )
      }
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    return when(item.itemId) {
      R.id.nav_join_challenge -> {
        val intent = Intent().apply {
          applicationContext?.let { setClass(it, JoinChallengeActivity::class.java) }
        }

        startActivityForResult(intent, 999)

        false
      }
      R.id.nav_create_challenge -> {
        val intent = Intent().apply {
          applicationContext?.let { setClass(it, CreateChallengeActivity::class.java) }
        }

        startActivityForResult(intent, 999)

        false
      }
      R.id.nav_challenge_bottom_nav -> {
        val order = item.order
        val challenges = ChallengeState.allChallenges.activeOrUpcoming()
        val challenge = challenges.elementAtOrNull(order) ?: challenges.first()

        ChallengeState.lastOpenedChallengeId = challenge.id

        if (challenge.isActive()) {
          navController.navigate(MainNavigationDirections.challengeBottomNav(challenge))
        } else {
          navController.navigate(MainNavigationDirections.upcomingChallenge(challenge))
        }

        drawer.closeDrawer(Gravity.START)

        true
      }
      else -> {
        NavigationUI.onNavDestinationSelected(item, navController)
        drawer.closeDrawer(Gravity.START)

        true
      }
    }
  }

  fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    if (inputMethodManager.isActive()) {
      if (activity.currentFocus != null) {
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
      }
    }
  }
}


