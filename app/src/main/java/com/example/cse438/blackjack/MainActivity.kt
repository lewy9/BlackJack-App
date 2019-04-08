package com.example.cse438.blackjack

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //sigh up button
        buttonSignUp.setOnClickListener{
            val name = nickname.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            if(name == "" || email == "" || password == "")
            {
                Toast.makeText(this, "All three blanks needed", Toast.LENGTH_SHORT).show()
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                editTextEmail.requestFocus()
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }
            else {
                App.mAuth = FirebaseAuth.getInstance()
                var l = App.mAuth?.createUserWithEmailAndPassword(email, password)
                l?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val db = FirebaseFirestore.getInstance()
                        val userData = HashMap<String, Any>()
                        userData["email"] = email
                        userData["nickname"] = name
                        userData["wins"] = 0L
                        userData["loses"] = 0L
                        App.user = App.mAuth.currentUser
                        db.collection("users").document(App.user!!.uid).set(userData).addOnSuccessListener {
                            Toast.makeText(this, "Sign up successfully", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this, GameActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "User Exists!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Can't sigh up", Toast.LENGTH_SHORT).show()
                        }
                    }
                }?.addOnFailureListener(this) {
                    Toast.makeText(this, "Can't add user", Toast.LENGTH_SHORT).show()
                }
            }
        }


        //login button
        textViewLogin.setOnClickListener {
            var intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
}


