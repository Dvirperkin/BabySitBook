package com.example.babysitbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private val database = Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")
    private val babysitterRef = database.getReference("Users/Babysiters")
    private val parentRef = database.getReference("Users/Parents")
    private lateinit var auth: FirebaseAuth
    private  lateinit var babysitter: RadioButton
    private  lateinit var parent: RadioButton
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        babysitter = findViewById(R.id.Babysiter)
        parent = findViewById(R.id.Parent)
        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)
        firstName = findViewById(R.id.FirstName)
        lastName  = findViewById(R.id.LastName)
    }

    fun register(view: View) {
        createUser()
    }

    private fun createUser() {
        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                if(parent.isChecked) {
                    val user = Parent(firstName.text.toString(), lastName.text.toString(), email.text.toString(),26, "Israel", "Jerusalem")
                    val currentUserUid = auth.currentUser?.uid
                    if(currentUserUid != null) {
                        parentRef.child(currentUserUid.toString()).setValue(user)
                    }
                }
                else if(babysitter.isChecked){
                    val user = Babysitter(firstName.text.toString(), lastName.text.toString(), email.text.toString(), 26, "Israel", "Jerusalem")
                    val currentUserUid = auth.currentUser?.uid
                    if(currentUserUid != null) {
                        babysitterRef.child(currentUserUid.toString()).setValue(user)
                    }
                }

                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                Toast.makeText(this, "User registered failed" + task.exception, Toast.LENGTH_LONG).show()
                email.error = "Email is already exist"
            }
        }
    }
}


/**/