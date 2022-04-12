package com.example.babysitbook

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.babysitbook.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        //bottom navigator
        navController = (supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment).navController
        binding.bottomNavigation.setupWithNavController(navController)

        //navigation UP button
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.chatFragment,
                                R.id.favoriteFragment, R.id.calendarFragment, R.id.profileFragment), binding.drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        //drawer navigator
        NavigationUI.setupWithNavController(binding.navDrawerView, navController)

        binding.navDrawerView.setNavigationItemSelectedListener {
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
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.top_bar_menu,menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        (menu.findItem(R.id.nav_search).actionView as SearchView).apply{
            setSearchableInfo(searchManager.getSearchableInfo(ComponentName("com.example.babysitbook","com.example.babysitbook.SearchableActivity")))
            isSubmitButtonEnabled = true
            isIconifiedByDefault =true
        }
        return true
    }


}