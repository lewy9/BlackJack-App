package com.example.cse438.blackjack

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignUpActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        signInButton.setOnClickListener {
            App.mAuth = FirebaseAuth.getInstance()
            var email = signInEmail.text.toString()
            var pass = signInPass.text.toString()
            if(email != "" && pass != "")
            {
                App.mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                        task ->
                    if (task.isSuccessful)
                    {
                        App.user = App.mAuth.currentUser
                        startActivity(Intent(this, GameActivity::class.java))
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Can't not sign in", Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}