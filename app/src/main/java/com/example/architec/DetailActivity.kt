package com.example.architec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.architec.data.ArchitectureStyle
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {
    private lateinit var architectureStyleId: String
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var originTextView: TextView
    private lateinit var timePeriodTextView: TextView
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
//        originTextView = findViewById(R.id.origin_text_view)
//        timePeriodTextView = findViewById(R.id.time_period_text_view)
        // Initialize other TextViews for other fields as needed

        // Fetch ArchitectureStyle from Firestore
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
//        originTextView.text = architectureStyle.origin
//        timePeriodTextView.text = architectureStyle.time_period
        // Update other TextViews for other fields as needed
    }
}