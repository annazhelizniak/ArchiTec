package com.example.architec.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.architec.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignInEmailActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private lateinit var userEmail: TextInputEditText
    private lateinit var userPassword: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_email)

        auth = Firebase.auth

        userEmail = findViewById(R.id.user_profile_email)
        userPassword = findViewById(R.id.user_profile_password)
        val signIn = findViewById<MaterialButton>(R.id.sign_in)
        signIn.setOnClickListener {
            loginUser()
        }
        val signUp = findViewById<TextView>(R.id.sign_up)
        signUp.setOnClickListener {
            val i = Intent(this, CreateAccountActivity::class.java)
            startActivity(i)
        }
    }

    private fun loginUser() {
        val email = userEmail.text.toString()
        val pass = userPassword.text.toString()
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
            else{
                Toast.makeText(this,"Sign in error " + task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
//    public override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//    }
}