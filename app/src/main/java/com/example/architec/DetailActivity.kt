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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        architectureStyleId = intent.getStringExtra("architecture_style_id") ?: ""
        if (architectureStyleId.isEmpty()) {
            return
        }


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

                    }
                } else {

                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun displayArchitectureStyle(architectureStyle: ArchitectureStyle) {
        nameTextView.text = architectureStyle.name
        descriptionTextView.text = architectureStyle.description
         originTextView.text = architectureStyle.origin
         timePeriodTextView.text = architectureStyle.time_period


        val featuresLayout = findViewById<LinearLayout>(R.id.features)
        featuresLayout.removeAllViews()
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size)
        architectureStyle.features?.let { features ->
            for (feature in features) {
                val featureTextView = TextView(this)
                featureTextView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                featureTextView.text = feature
                val icon = ContextCompat.getDrawable(this, R.drawable.column)
                icon?.setBounds(0, 0, iconSize, iconSize)
                featureTextView.setCompoundDrawables(icon, null, null, null)
                featureTextView.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_dimen)
                featuresLayout.addView(featureTextView)
            }
        }
        loadImagesFromStorage()
    }



    private fun loadImagesFromStorage() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("$architectureStyleId")

        storageRef.listAll()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (imageRef in task.result!!.items) {

                        loadImage(imageRef)
                    }
                } else {
                    Log.e("DetailActivity", "Failed to retrieve images from Firebase Storage: ${task.exception}")
                }
            }
    }


    private fun loadImage(imageRef: StorageReference) {
        val imageView = ImageView(this)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageView.layoutParams = layoutParams

        Glide.with(this)
            .load(imageRef)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    imageView.setImageDrawable(resource)

                    imageGalleryLayout.addView(imageView)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

}

