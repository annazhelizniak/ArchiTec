package com.example.architec.ui.classify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.architec.R
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager

import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.nio.MappedByteBuffer
//
//
//class ClassificationResultActivity : AppCompatActivity() {
//    var context: Context? = null
//    var result = ""
//    var modelOutput: TensorBuffer? = null
//    var interpreter: Interpreter? = null
//    var modelFile: File? = null
//    var options = Interpreter.Options()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(com.example.architec.R.layout.activity_classification_result)
//
//        val imageView: ImageView = findViewById(com.example.architec.R.id.user_input)
//        val selectedImageUri = intent.getParcelableExtra<Uri>("selectedImageUri")
//
//        selectedImageUri?.let { uri ->
//            Glide.with(this)
//                .load(uri)
//                .into(imageView)
//        }
//
//        predict(imageView)
//    }
//
//    fun predict(view: View?) {
//        context = this
//        val remoteModel = FirebaseCustomRemoteModel.Builder("architecture-style-classifier").build()
//        val conditions = FirebaseModelDownloadConditions.Builder()
//            .requireWifi()
//            .build()
//        FirebaseModelManager.getInstance().download(remoteModel, conditions)
//            .addOnSuccessListener { v: Void? ->
//                Log.i("Info", "Switching to downloaded model")
//                FirebaseModelManager.getInstance().getLatestModelFile(remoteModel)
//                    .addOnCompleteListener { task: Task<File> ->
//                        modelFile = task.result
//                        assert(modelFile != null)
//                        modelFile?.let { file ->
//                            interpreter = Interpreter(file, options)
//                            makePrediction()
//                        }
//
//                    }
//            }
//        if (modelFile != null) {
//            interpreter = Interpreter(modelFile!!, options)
//
//            makePrediction()
//        } else {
//            Log.i("Info", "Trying Local Model")
//            context?.let { ctx ->
//                try {
//                    val tfliteModel: MappedByteBuffer =
//                        FileUtil.loadMappedFile(ctx, "mobilenet_v1_1.0_224_quant.tflite")
//                    val options = Interpreter.Options()
//                    interpreter = Interpreter(tfliteModel, options)
//                    makePrediction()
//                } catch (e: IOException) {
//                    Log.e("tflite Support", "Error reading model", e)
//                }
//            }
//
//        }
//    }
//
//    fun makePrediction() {
//        var textView: TextView =
//            findViewById(com.example.architec.R.id.classificationResultTextView)
//        val imageView: ImageView = findViewById(R.id.user_input)
//        val bitmap = imageViewToBitmap(imageView)
//        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
//            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
//            .build()
//        var tImage = TensorImage(DataType.UINT8)
//        tImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
//        modelOutput = TensorBuffer.createFixedSize(intArrayOf(1, 1001), DataType.UINT8)
//        interpreter!!.run(tImage.buffer, modelOutput!!.buffer)
//        val MOBILE_NET_LABELS = "labels.txt"
//        var mobilenetlabels: List<String?>? = null
//        try {
//            mobilenetlabels = FileUtil.loadLabels(context!!, MOBILE_NET_LABELS)
//        } catch (e: IOException) {
//            Log.e("tfliteSupport", "Error reading label file", e)
//        }
//        val probabilityProcessor = TensorProcessor.Builder().add(NormalizeOp(0f, 255f)).build()
//        if (mobilenetlabels != null) {
//            val labels = TensorLabel(mobilenetlabels, probabilityProcessor.process(modelOutput))
//            val resultsMap = labels.mapWithFloatValue
//            for (key in resultsMap.keys) {
//                val value = resultsMap[key]
//                if (value!! >= 0.50) {
//                    val roundOff = String.format("%.2f", value)
//                    result = "$key $roundOff"
//                }
//                Log.i("Info", "$key $value")
//            }
//            Log.i("Info", "The label is $result")
//            textView.append(result)
//            modelOutput = TensorBuffer.createFixedSize(intArrayOf(1, 1001), DataType.UINT8)
//        }
//    }
//
//    private fun imageViewToBitmap(imageView: ImageView): Bitmap {
//        val drawable = imageView.drawable
//            ?: throw IllegalArgumentException("ImageView does not have a drawable")
//        if (drawable is BitmapDrawable) {
//            return drawable.bitmap
//        } else {
//            val bitmap = Bitmap.createBitmap(
//                drawable.intrinsicWidth,
//                drawable.intrinsicHeight,
//                Bitmap.Config.ARGB_8888
//            )
//            val canvas = Canvas(bitmap)
//            drawable.setBounds(0, 0, canvas.width, canvas.height)
//            drawable.draw(canvas)
//            return bitmap
//        }
//    }
//}

import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ClassificationResultActivity : AppCompatActivity() {

    private var interpreter: Interpreter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification_result)

        val imageView: ImageView = findViewById(R.id.user_input)
        val textView: TextView = findViewById(R.id.classificationResultTextView)
        val selectedImageUri = intent.getParcelableExtra<Uri>("selectedImageUri")

        selectedImageUri?.let { uri ->
            Glide.with(this)
                .asBitmap()
                .load(uri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageView.setImageBitmap(resource)
                        classifyImage(resource, textView)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    private fun classifyImage(bitmap: Bitmap, textView: TextView) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel("architecture-style-classifier", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnSuccessListener { model: CustomModel? ->
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                    runModel(bitmap, textView)
                } else {
                    textView.text = "Model file is null"
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                textView.text = "Model download failed: ${e.message}"
            }
    }

    private fun runModel(inputImage: Bitmap, textView: TextView) {
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
                "$label: $probability"
            }
            textView.text = results.joinToString("\n")
        } catch (e: IOException) {
            textView.text = "Error reading labels: ${e.message}"
        }
    }
}