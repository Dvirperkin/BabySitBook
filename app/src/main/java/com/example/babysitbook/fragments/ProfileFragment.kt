package com.example.babysitbook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.babysitbook.Babysitter
import com.example.babysitbook.Parent
import com.example.babysitbook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(){
    enum class userKinds{
        NONE, BABYSITTER, PARENT
    }
    //FireBase API
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")

    private lateinit var classInflater : LayoutInflater
    private lateinit var babysitterUser : Babysitter
    private lateinit var parentUser : Parent
    private var userKind : userKinds = userKinds.NONE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        classInflater = inflater


        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileView = view.findViewById<LinearLayout>(R.id.Profile)

        profileView?.removeAllViews()

        //Need to be change to onDataChange().
        if (userKind == userKinds.NONE) {
            for(kind in mapOf(Pair("Babysitters", R.layout.babysitter_profile),
                            Pair("Parents", R.layout.parent_profile))){
                database.getReference("Users/${kind.key}/${auth.currentUser?.uid}").get().addOnSuccessListener {
                    if (it.exists()) {
                        profileView?.addView(classInflater.inflate(kind.value, null))
                        userKind = if (kind.key == "Babysitters") {
                            babysitterUser = it.getValue(Babysitter::class.java)!!
                            setDetails(view, babysitterUser)
                            userKinds.BABYSITTER
                        } else {
                            parentUser = it.getValue(Parent::class.java)!!
                            userKinds.PARENT
                        }
                    }
                }.addOnFailureListener {
                    val error = it.message
                    print(error)
                }
                if(userKind != userKinds.NONE)
                    break
            }
        } else {
            if (userKind == userKinds.BABYSITTER) {
                profileView?.addView(classInflater.inflate(R.layout.babysitter_profile, null))
                setDetails(view, babysitterUser)
            } else {
                profileView?.addView(classInflater.inflate(R.layout.parent_profile, null))
            }
        }
    }

    private fun setDetails(view : View, user: Babysitter){

        val name : String = "${user.firstName} ${user.lastName}"
        view.findViewById<TextView>(R.id.Name).text = name
        view.findViewById<TextView>(R.id.City).text = user.city
        view.findViewById<TextView>(R.id.Age).text = user.age.toString()
        view.findViewById<TextView>(R.id.Experience).text = user.experience.toString()
    }
}