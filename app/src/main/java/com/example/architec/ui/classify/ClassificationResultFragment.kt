import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.architec.R
import com.example.architec.ui.classify.ClassifyFragment
import java.io.File
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer


import java.nio.ByteOrder

class ClassificationResultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_classification_result, container, false)

        val imageView: ImageView = view.findViewById(R.id.user_input)

        val selectedImageUri = arguments?.getParcelable<Uri>("selectedImageUri")

//        selectedImageUri?.let { uri ->
//            Glide.with(requireContext())
//                .load(uri)
//                .into(imageView)
//        }

        return view
    }
}
//class ClassificationResultFragment : Fragment() {
//    private lateinit var tflite: Interpreter
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val view = inflater.inflate(R.layout.fragment_classification_result, container, false)
//
//        // Retrieve data from the arguments bundle
//        val args = arguments
//        val photoFilePath = args?.getString("photoFilePath")
//
//        // Load the image into the ImageView using your custom function
//        val imageView: ImageView = view.findViewById(R.id.user_input)
//        loadImageIntoImageView(photoFilePath, imageView)
//
//        val classificationResultTextView: TextView = view.findViewById(R.id.classificationResultTextView)
//        //initializeTFLiteInterpreter()
//        //performTFLiteImageClassification(photoFilePath, classificationResultTextView)
//
//        return view
//    }
//
//    private fun initializeTFLiteInterpreter() {
//        try {
//            // Initialize the TensorFlow Lite interpreter with your model file
//            val modelFile = File("architecture_style_classifierv1.tflite")
//            val options = Interpreter.Options()
//            tflite = Interpreter(modelFile, options)
//        } catch (e: Exception) {
//            // Handle initialization error
//            e.printStackTrace()
//        }
//    }
//
//    private fun performTFLiteImageClassification(photoFilePath: String?, resultTextView: TextView) {
//        try {
//            // Load the image bitmap and preprocess it for TensorFlow Lite
//            val imageBitmap = loadAndRotateImage(photoFilePath)
//            val inputBuffer = preprocessImageForTFLite(imageBitmap)
//
//            // Run inference
//            val outputBuffer = Array(1) { FloatArray(NUM_CLASSES) }
//            tflite.run(inputBuffer, outputBuffer)
//
//            // Post-process the inference result
//            val classificationResult = postprocessTFLiteOutput(outputBuffer[0])
//
//            // Update the result TextView
//            resultTextView.text = classificationResult
//            resultTextView.visibility = View.VISIBLE
//        } catch (e: Exception) {
//            e.printStackTrace()
//            // Handle the exception as needed
//        }
//    }
//
//    // ...
//
//    private fun loadAndRotateImage(photoFilePath: String?): Bitmap {
//        if (!photoFilePath.isNullOrEmpty() && File(photoFilePath).exists()) {
//            try {
//                val options = BitmapFactory.Options()
//                options.inJustDecodeBounds = true
//                BitmapFactory.decodeFile(photoFilePath, options)
//                val imageWidth = options.outWidth
//                val imageHeight = options.outHeight
//
//                val exif = ExifInterface(photoFilePath)
//                val orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED
//                )
//
//                val matrix = Matrix()
//                when (orientation) {
//                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
//                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
//                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
//                }
//
//                options.inJustDecodeBounds = false
//                val bitmap = BitmapFactory.decodeFile(photoFilePath, options)
//                return Bitmap.createBitmap(
//                    bitmap,
//                    0, 0, imageWidth, imageHeight,
//                    matrix, true
//                )
//            } catch (e: Exception) {
//                e.printStackTrace()
//                // Handle the exception as needed
//            }
//        }
//        // Return a default bitmap in case of failure
//        return BitmapFactory.decodeResource(resources, android.R.drawable.ic_menu_gallery)
//    }
//
//    private fun preprocessImageForTFLite(imageBitmap: Bitmap): ByteBuffer {
//        val inputSize = 214 // Specify the input size required by your TensorFlow Lite model
//        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
//        byteBuffer.order(ByteOrder.nativeOrder())
//
//        // Assuming the model accepts raw pixel values, just flatten the pixels and put them into the ByteBuffer
//        val pixels = IntArray(inputSize * inputSize)
//        imageBitmap.getPixels(pixels, 0, imageBitmap.width, 0, 0, imageBitmap.width, imageBitmap.height)
//
//        for (pixel in pixels) {
//            // Extract RGB values from the pixel
//            val red = (pixel shr 16 and 0xFF).toFloat()
//            val green = (pixel shr 8 and 0xFF).toFloat()
//            val blue = (pixel and 0xFF).toFloat()
//
//            // Add the raw pixel values to the ByteBuffer
//            byteBuffer.putFloat(red / 255.0f)
//            byteBuffer.putFloat(green / 255.0f)
//            byteBuffer.putFloat(blue / 255.0f)
//        }
//
//        return byteBuffer
//    }
//
//
//    private fun postprocessTFLiteOutput(outputBuffer: FloatArray): String {
//        // Your post-processing logic for TensorFlow Lite output
//        // Convert the output buffer to a meaningful classification result
//        // Note: You need to replace this part with your actual postprocessing logic
//
//        // Sort all values in the original array
//        val sortedValues = outputBuffer.sorted()
//
//        // Convert the sorted values to a formatted string
//        val result = "Sorted Values: ${sortedValues.joinToString(", ")}"
//
//        return result
//    }
//
//
//
//
//
//    companion object {
//        private const val NUM_CLASSES = 25 // Adjust this based on your model's number of classes
//    }
//
//    private fun loadImageIntoImageView(photoFilePath: String?, imageView: ImageView) {
//        if (!photoFilePath.isNullOrEmpty() && File(photoFilePath).exists()) {
//            try {
//                val options = BitmapFactory.Options()
//                options.inJustDecodeBounds = true
//                BitmapFactory.decodeFile(photoFilePath, options)
//                val imageWidth = options.outWidth
//                val imageHeight = options.outHeight
//
//                val exif = ExifInterface(photoFilePath)
//                val orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED
//                )
//
//                val matrix = Matrix()
//                when (orientation) {
//                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
//                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
//                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
//                }
//
//                options.inJustDecodeBounds = false
//                val bitmap = BitmapFactory.decodeFile(photoFilePath, options)
//                val rotatedBitmap = Bitmap.createBitmap(
//                    bitmap,
//                    0, 0, imageWidth, imageHeight,
//                    matrix, true
//                )
//
//                imageView.setImageBitmap(rotatedBitmap)
//                imageView.visibility = View.VISIBLE
//            } catch (e: Exception) {
//                e.printStackTrace()
//                // Handle the exception as needed
//            }
//        } else {
//            // Log a message or handle the case where the file doesn't exist
//        }
//    }
//
////override fun onCreateView(
////    inflater: LayoutInflater, container: ViewGroup?,
////    savedInstanceState: Bundle?
////): View {
////    // Inflate the layout for this fragment
////    return inflater.inflate(R.layout.fragment_classification_result, container, false)
////}
//}
