package com.example.architec

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.architec.data.ArchitectureStyle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream


class DetailActivity : AppCompatActivity() {
    private lateinit var architectureStyleId: String
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var originTextView: TextView
    private lateinit var timePeriodTextView: TextView
    private lateinit var imageGalleryLayout: LinearLayout
    // Add more TextViews for other fields as needed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Retrieve architecture style id from extra
        architectureStyleId = intent.getStringExtra("architecture_style_id") ?: ""
        if (architectureStyleId.isEmpty()) {
            // Handle error, id not found
            return
        }

        // Initialize TextViews
        nameTextView = findViewById(R.id.name)
        descriptionTextView = findViewById(R.id.description)
        originTextView = findViewById(R.id.origin)
        timePeriodTextView = findViewById(R.id.time_period)
        imageGalleryLayout = findViewById(R.id.image_gallery_layout)
        fetchArchitectureStyle()

    }

    private fun fetchArchitectureStyle() {
        val db = FirebaseFirestore.getInstance()
        val architectureStyleRef = db.collection("styles").document(architectureStyleId)
        architectureStyleRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val architectureStyle = document.toObject(ArchitectureStyle::class.java)
                    if (architectureStyle != null) {
                        displayArchitectureStyle(architectureStyle)
                    } else {
                        // Handle error, unable to convert document to ArchitectureStyle
                    }
                } else {
                    // Handle error, document not found
                }
            }
            .addOnFailureListener { exception ->
                // Handle error, failed to fetch document
            }
    }

    private fun displayArchitectureStyle(architectureStyle: ArchitectureStyle) {
        // Display ArchitectureStyle data on TextViews
        nameTextView.text = architectureStyle.name
        descriptionTextView.text = architectureStyle.description
         originTextView.text = architectureStyle.origin
         timePeriodTextView.text = architectureStyle.time_period

        // Add features to the LinearLayout
        val featuresLayout = findViewById<LinearLayout>(R.id.features)
        featuresLayout.removeAllViews() // Clear existing views
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size)
        architectureStyle.features?.let { features ->
            for (feature in features) {
                // Create a TextView for each feature
                val featureTextView = TextView(this)
                featureTextView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                featureTextView.text = feature
                val icon = ContextCompat.getDrawable(this, R.drawable.column)
                icon?.setBounds(0, 0, iconSize, iconSize) // Set icon size
                featureTextView.setCompoundDrawables(icon, null, null, null)
                featureTextView.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_dimen) // Set padding between icon and text
                featuresLayout.addView(featureTextView)
            }
        }
        loadImagesFromStorage()
    }



    private fun loadImagesFromStorage() {
        // Get a reference to the Firebase Storage root
        val storage = FirebaseStorage.getInstance()
        // Construct a reference to the folder for the current style's images
        val storageRef = storage.reference.child("$architectureStyleId")

        // List all images in the folder
        storageRef.listAll()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Iterate through each image in the folder
                    for (imageRef in task.result!!.items) {
                        // Load image from Firebase Storage and add it to the image gallery
                        loadImage(imageRef)
                    }
                } else {
                    // Handle failure to retrieve images
                    Log.e("DetailActivity", "Failed to retrieve images from Firebase Storage: ${task.exception}")
                }
            }
    }


    private fun loadImage(imageRef: StorageReference) {
        // Create an ImageView to display the image
        val imageView = ImageView(this)
        // Set layout parameters for the ImageView
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageView.layoutParams = layoutParams

        // Load image from Firebase Storage into the ImageView
        Glide.with(this)
            .load(imageRef)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    // Set the loaded image to ImageView
                    imageView.setImageDrawable(resource)
                    // Add the ImageView to the image gallery layout
                    imageGalleryLayout.addView(imageView)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Placeholder cleanup if needed
                }
            })
    }

}

