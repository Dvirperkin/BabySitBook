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
import androidx.navigation.ui.setupWithNavController
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
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = Firebase.auth

        navController= findNavController(R.id.host_fragment)
        bottom_navigation.setupWithNavController(navController)

        appBarConfiguration= AppBarConfiguration(navController.graph)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.OPEN, R.string.CLOSE)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        nav_drawer_view.setNavigationItemSelectedListener {
//            drawerLayout.closeDrawer(GravityCompat.START)

//            when(it.itemId) {
//                R.id.mi_settings -> {
//                    setCurrentFragment(settingsFragment, "Settings")
//                }
//
//                R.id.mi_logout -> Toast.makeText(applicationContext,
//                    "Clicked Logout", Toast.LENGTH_SHORT).show()
//            }
//            true
//        }
    }

    onSupport

//    private fun setCurrentFragment(fragment: Fragment, title: String) =
//        supportFragmentManager.beginTransaction().apply {
//            //replace(R.id.fl_wrapper, fragment)
//            commit()
//
//            supportActionBar?.title= title
//        }


    fun logout(view: View){
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if(toggle.onOptionsItemSelected(item)){
//            return true
//        }
//
//
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.top_bar_menu, menu)
//        val search = menu?.findItem(R.id.nav_search)
//        val searchView= search?.actionView as SearchView
//        searchView.queryHint="search something!"
//        return super.onCreateOptionsMenu(menu)
//    }
}
