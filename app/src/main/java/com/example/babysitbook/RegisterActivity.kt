package com.example.babysitbook

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.babysitbook.databinding.ActivityRegisterBinding
import com.example.babysitbook.model.Babysitter
import com.example.babysitbook.model.Parent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    //FireBase API
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")
    private val babysitterRef = database.getReference("Users/Babysitters")
    private val parentRef = database.getReference("Users/Parents")

    //SPA
    private lateinit var registerFormLayout : LinearLayout
    private lateinit var inflater : LayoutInflater

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var radioBabysitter: RadioButton
    private lateinit var radioParent: RadioButton
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var age : EditText
    private lateinit var kids : EditText
    private lateinit var exp : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        registerFormLayout = binding.RegisterForm
        radioBabysitter = binding.Babysitter
        radioBabysitter.setOnClickListener(View.OnClickListener{ babysitterChecked() })
        radioParent = binding.Parent
        radioParent.setOnClickListener(View.OnClickListener { parentChecked() })
    }

    private fun register() {
        createUser()
    }

    private fun babysitterChecked(){
        Toast.makeText(this, "Babysitter", Toast.LENGTH_SHORT).show()
        placeForm(R.layout.babysitter_form)
    }
    private fun parentChecked(){
        Toast.makeText(this, "Parent", Toast.LENGTH_SHORT).show()
        placeForm(R.layout.parent_form)
    }

    private fun createUser() {
        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)
        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                firstName = findViewById(R.id.Name)
                lastName  = findViewById(R.id.LastName)
                age = findViewById(R.id.Age)

                if(radioParent.isChecked) {
                    kids = findViewById(R.id.Kids)
                    val user = Parent(
                        firstName.text.toString(),
                        lastName.text.toString(),
                        email.text.toString(),
                        Integer.parseInt(age.text.toString()),
                        "Israel",
                        "Jerusalem",
                        Integer.parseInt(kids.text.toString())
                    )

                    val currentUserUid = auth.currentUser?.uid
                    if(currentUserUid != null) {
                        parentRef.child(currentUserUid.toString()).setValue(user)
                    }
                }
                else if(radioBabysitter.isChecked){
                    exp = findViewById(R.id.Experience)
                    val user = Babysitter(
                        firstName.text.toString(),
                        lastName.text.toString(),
                        email.text.toString(),
                        Integer.parseInt(age.text.toString()),
                        "Israel",
                        "Jerusalem",
                        Integer.parseInt(exp.text.toString())
                    )

                    val currentUserUid = auth.currentUser?.uid
                    if(currentUserUid != null) {
                        babysitterRef.child(currentUserUid.toString()).setValue(user)
                    }
                }

                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))

            } else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                email.error = "Email is already exist"
            }
        }
    }

    private fun placeForm(layoutID : Int){
        val form = inflater.inflate(layoutID, null)
        registerFormLayout.removeAllViews()
        registerFormLayout.addView(form)
        findViewById<Button>(R.id.registerButton).setOnClickListener(View.OnClickListener { register() })
    }
}