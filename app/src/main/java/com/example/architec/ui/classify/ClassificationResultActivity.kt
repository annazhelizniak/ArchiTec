package com.example.architec.ui.classify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.architec.R
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ClassificationResultActivity : AppCompatActivity() {

    private var interpreter: Interpreter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification_result)

        val imageView: ImageView = findViewById(R.id.user_input)
        val resultTable: TableLayout = findViewById(R.id.resultTable)
        val selectedImageUri = intent.getParcelableExtra<Uri>("selectedImageUri")

        selectedImageUri?.let { uri ->
            Glide.with(this)
                .asBitmap()
                .load(uri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageView.setImageBitmap(resource)
                        classifyImage(resource, resultTable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    private fun classifyImage(bitmap: Bitmap, resultTable: TableLayout) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel("architecture-style-classifier", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnSuccessListener { model: CustomModel? ->
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                    runModel(bitmap, resultTable)
                } else {
                    val errorRow = TableRow(this)
                    val errorMessage = TextView(this)
                    errorMessage.text = "Model file is null"
                    errorRow.addView(errorMessage)
                    resultTable.addView(errorRow)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                val errorRow = TableRow(this)
                val errorMessage = TextView(this)
                errorMessage.text = "Model download failed: ${e.message}"
                errorRow.addView(errorMessage)
                resultTable.addView(errorRow)
            }
    }

    private fun runModel(inputImage: Bitmap, resultTable: TableLayout) {
        val bitmap = Bitmap.createScaledBitmap(inputImage, 224, 224, true)
        val input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder())

        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val px = bitmap.getPixel(x, y)
                input.putFloat((Color.red(px) - 127) / 255f)
                input.putFloat((Color.green(px) - 127) / 255f)
                input.putFloat((Color.blue(px) - 127) / 255f)
            }
        }

        val bufferSize = 1000 * Float.SIZE_BYTES
        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
        interpreter?.run(input, modelOutput)
        modelOutput.rewind()
        val probabilities = modelOutput.asFloatBuffer()

        try {
            val reader = BufferedReader(InputStreamReader(assets.open("labels.txt")))
            val labels = reader.readLines()
            reader.close()

            val results = (0 until probabilities.capacity()).map { index ->
                val label = labels.getOrNull(index) ?: "Unknown"
                val probability = probabilities.get(index)
                label to probability
            }.sortedByDescending { it.second }
                .take(3)


            results.forEach { (label, probability) ->
                resultTable.addView(createTableRow(label, probability))
            }


        } catch (e: IOException) {
            e.printStackTrace()
            val errorRow = TableRow(this)
            val errorMessage = TextView(this)
            errorMessage.text = "Error reading labels: ${e.message}"
            errorRow.addView(errorMessage)
            resultTable.addView(errorRow)
        }
    }

    private fun createTableRow(label: String, probability: Float): TableRow {
        val tableRow = TableRow(this).apply {
            setBackgroundResource(R.drawable.table_cell_border)
        }

        val labelTextView = TextView(this).apply {
            text = label
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.drawable.table_cell_border)
        }

        val probabilityTextView = TextView(this).apply {
            text = String.format("%.2f", probability)
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.drawable.table_cell_border)
        }

        tableRow.addView(labelTextView)
        tableRow.addView(probabilityTextView)

        return tableRow
    }
}
