package com.example.babysitbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)

    }

    override fun onStart() {
        super.onStart()
        val currentUser : FirebaseUser? = auth.currentUser
            if(currentUser != null){
                startActivity(Intent(this, HomeActivity::class.java))
            }
    }

    fun register(view: View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun login(view: View){

        if(email.text.isEmpty()){
            email.error = "error"
            return;
        }

        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "User logged in successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "User logged in failed " + task.exception, Toast.LENGTH_LONG).show()
            }
        }
    }
}