package com.example.architec.ui.classify

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.architec.R
import java.io.File

class ClassificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_classification_result)

        val photoFilePath = intent.getStringExtra("photoFilePath")
        val photoFile = File(photoFilePath)

        // Now you can use the photo file for further processing, such as classification
        // Implement your TensorFlow Lite model logic here

        // Example: Display the photo in an ImageView
        val imageView: ImageView = findViewById(R.id.imageView)
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        imageView.setImageBitmap(bitmap)
    }
}
