package com.example.architec.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.architec.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;

    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 101
        private const val REQUEST_IMAGE_FROM_GALLERY = 102
    }

    private lateinit var view2: CardView
    private lateinit var addPhoto: ImageView
    private lateinit var signUp: MaterialButton
    private lateinit var userName: TextInputEditText
    private lateinit var userEmail: TextInputEditText
    private lateinit var userPassword: TextInputEditText

    private var profilePictureUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        view2 = findViewById(R.id.view2)
        addPhoto = findViewById(R.id.add_photo)
        userName = findViewById(R.id.user_profile_name)
        userEmail = findViewById(R.id.user_profile_email)
        userPassword = findViewById(R.id.user_profile_password)
        view2.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_STORAGE_PERMISSION
                )
            }
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_FROM_GALLERY)

        }

        auth = Firebase.auth
        signUp = findViewById(R.id.sign_up)
        signUp.setOnClickListener {
            registerUser()
        }

        val signIn = findViewById<TextView>(R.id.sign_in)
        signIn.setOnClickListener {
            val i = Intent(this, SignInEmailActivity::class.java)
            startActivity(i)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_IMAGE_FROM_GALLERY)
            } else {
                Toast.makeText(
                    this,
                    "Storage permissions are required to access the gallery.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_FROM_GALLERY) {
            profilePictureUri = data?.data
            profilePictureUri?.let {
                Glide.with(this)
                    .load(profilePictureUri)
                    .into(addPhoto)
            }
        }
    }

//    private fun registerUser() {
//        val name = userName.text.toString()
//        val email = userEmail.text.toString()
//        val pass = userPassword.text.toString()
//        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
//                val i = Intent(this, SignInEmailActivity::class.java)
//                startActivity(i)
//            } else {
//                Toast.makeText(
//                    this,
//                    "Register error : " + task.exception?.message,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }

    private fun registerUser() {
        val name = userName.text.toString()
        val email = userEmail.text.toString()
        val pass = userPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser

                val profileUpdates = userProfileChangeRequest{
                    displayName = name
                    photoUri = profilePictureUri
                }

                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                            val i = Intent(this, SignInEmailActivity::class.java)
                            startActivity(i)
                        } else {
                            Toast.makeText(this, "Failed to update user profile: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Register error: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }




}