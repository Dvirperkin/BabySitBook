package com.example.babysitbook.activities

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.babysitbook.R
import com.example.babysitbook.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = Firebase.auth

        //todo: check if the user is a babysitter, and if it is- change the nav drawer!!!!

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //bottom navigator
        navController = (supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment).navController
        binding.bottomNavigation.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)

        //drawer navigator
        NavigationUI.setupWithNavController(binding.navDrawerView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    fun logout(item: MenuItem) {
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
            setSearchableInfo(searchManager.getSearchableInfo(ComponentName("com.example.babysitbook","com.example.babysitbook.activities.SearchableActivity")))
            isSubmitButtonEnabled = true
            isIconifiedByDefault = true

        }
        return true
    }

    override fun onStart() {
        super.onStart()
        val currentUser : FirebaseUser? = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
    }
}