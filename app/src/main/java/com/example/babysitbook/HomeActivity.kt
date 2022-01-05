package com.example.babysitbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.babysitbook.fragments.FavoriteFragment
import com.example.babysitbook.fragments.HomeFragment
import com.example.babysitbook.fragments.ChatFragment
import com.example.babysitbook.fragments.ProfileFragment
import com.example.babysitbook.fragments.CalendarFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    //private val database = Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")
    //private val myRef = database.getReference("Users/Babysiter")
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = Firebase.auth

        val homeFragment = HomeFragment()
        val chatFragment = ChatFragment()
        val favoriteFragment = FavoriteFragment()
        val calendarFragment = CalendarFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(homeFragment)

        bottom_navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    setCurrentFragment(homeFragment)
                }
                R.id.nav_chat -> {
                    setCurrentFragment(chatFragment)
                }
                R.id.nav_favorite -> {
                    setCurrentFragment(favoriteFragment)
                }
                R.id.nav_calendar -> {
                    setCurrentFragment(calendarFragment)
                }
                R.id.nav_profile -> {
                    setCurrentFragment(profileFragment)
                }
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }


    fun logout(view: View){
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
