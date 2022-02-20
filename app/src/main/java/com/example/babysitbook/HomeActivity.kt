package com.example.babysitbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.babysitbook.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.babysitbook.fragments.FavoriteFragment
import com.example.babysitbook.fragments.HomeFragment
import com.example.babysitbook.fragments.ChatFragment
import com.example.babysitbook.fragments.ProfileFragment
import com.example.babysitbook.fragments.CalendarFragment
import com.example.babysitbook.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    //private val database = Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")
    //private val myRef = database.getReference("Users/Babysiter")
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater);
        setContentView(binding.root)

        auth = Firebase.auth

        //bottom navigator
        navController = findNavController(R.id.host_fragment)
        bottom_navigation.setupWithNavController(navController)

        //navigation UP button
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.chatFragment,
                                R.id.favoriteFragment, R.id.calendarFragment, R.id.profileFragment), drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        //drawer navigator
        NavigationUI.setupWithNavController(nav_drawer_view, navController)

        nav_drawer_view.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.mi_logout -> logout()
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}