package com.example.architec.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.architec.R

class CreateAccountActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE_FROM_GALLERY = 102
    }

    private lateinit var view2: CardView
    private lateinit var addPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        view2 = findViewById(R.id.view2)
        addPhoto = findViewById(R.id.add_photo)

        view2.setOnClickListener {
            // Launch the image picker
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_FROM_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_FROM_GALLERY) {
            // Image selected from gallery
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                // Load the selected image into the add_photo ImageView using Glide
                Glide.with(this)
                    .load(selectedImageUri)
                    .into(addPhoto)
            }
        }
    }
}