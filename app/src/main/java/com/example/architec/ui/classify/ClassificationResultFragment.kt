//import android.R
//import android.content.Context
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.camera.core.ImageProcessor
//import androidx.fragment.app.Fragment
//import com.bumptech.glide.Glide
//import com.example.architec.R
//import com.google.android.gms.tasks.Task
//import com.google.firebase.firestore.util.FileUtil
//import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
//import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
//import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
//import org.tensorflow.lite.Interpreter
//import java.io.File
//import java.io.IOException
//import java.nio.MappedByteBuffer
//
//
//class ClassificationResultFragment : Fragment() {
//
//    var context: Context? = null
//    var result = ""
//    //var modelOutput: TensorBuffer? = null
//    var interpreter: Interpreter? = null
//    var modelFile: File? = null
//    var options = Interpreter.Options()
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(com.example.architec.R.layout.fragment_classification_result, container, false)
//
//        val imageView: ImageView = view.findViewById(com.example.architec.R.id.user_input)
//
//        val selectedImageUri = arguments?.getParcelable<Uri>("selectedImageUri")
//
//        selectedImageUri?.let { uri ->
//            Glide.with(requireContext())
//                .load(uri)
//                .into(imageView)
//        }
//
//        return view
//    }
//
//    fun predict(view: View?) {
//        context = this
//        val remoteModel: FirebaseCustomRemoteModel = FirebaseCustomRemoteModel.Builder("architecture-style-classifier").build()
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
//                        interpreter = Interpreter(modelFile, options)
//                    }
//            }
//        if (modelFile != null) {
//            interpreter = Interpreter(modelFile, options)
//            makePrediction()
//        } else {
//            Log.i("Info", "Trying Local Model")
//            try {
//                val tfliteModel: MappedByteBuffer =
//                    FileUtil.loadMappedFile(context, "mobilenet_v1_1.0_224_quant.tflite")
//                val options = Interpreter.Options()
//                interpreter = Interpreter(tfliteModel, options)
//                makePrediction()
//            } catch (e: IOException) {
//                Log.e("tflite Support", "Error reading model", e)
//            }
//        }
//    }
//
//    fun makePrediction() {
//        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.hen)
//        val imageProcessor: ImageProcessor = Builder()
//            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
//            .build()
//        var tImage = TensorImage(DataType.UINT8)
//        tImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
//        modelOutput = TensorBuffer.createFixedSize(intArrayOf(1, 1001), DataType.UINT8)
//        interpreter!!.run(tImage.getBuffer(), modelOutput.getBuffer())
//        val MOBILE_NET_LABELS = "labels_mobilenet_quant_v1_224.txt"
//        var mobilenetlabels: List<String?>? = null
//        try {
//            mobilenetlabels = FileUtil.loadLabels(context, MOBILE_NET_LABELS)
//        } catch (e: IOException) {
//            Log.e("tfliteSupport", "Error reading label file", e)
//        }
//        val probabilityProcessor: TensorProcessor = Builder().add(NormalizeOp(0, 255)).build()
//        if (mobilenetlabels != null) {
//            // Map of labels and their corresponding probability
//            val labels = TensorLabel(mobilenetlabels, probabilityProcessor.process(modelOutput))
//            // Create a map to access the result based on label
//            val resultsMap: Map<String, Float> = labels.getMapWithFloatValue()
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
//}
//
//
////class ClassificationResultFragment : Fragment() {
////    private lateinit var tflite: Interpreter
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
////        val view = inflater.inflate(R.layout.fragment_classification_result, container, false)
////
////        // Retrieve data from the arguments bundle
////        val args = arguments
////        val photoFilePath = args?.getString("photoFilePath")
////
////        // Load the image into the ImageView using your custom function
////        val imageView: ImageView = view.findViewById(R.id.user_input)
////        loadImageIntoImageView(photoFilePath, imageView)
////
////        val classificationResultTextView: TextView = view.findViewById(R.id.classificationResultTextView)
////        //initializeTFLiteInterpreter()
////        //performTFLiteImageClassification(photoFilePath, classificationResultTextView)
////
////        return view
////    }
////
////    private fun initializeTFLiteInterpreter() {
////        try {
////            // Initialize the TensorFlow Lite interpreter with your model file
////            val modelFile = File("architecture_style_classifierv1.tflite")
////            val options = Interpreter.Options()
////            tflite = Interpreter(modelFile, options)
////        } catch (e: Exception) {
////            // Handle initialization error
////            e.printStackTrace()
////        }
////    }
////
////    private fun performTFLiteImageClassification(photoFilePath: String?, resultTextView: TextView) {
////        try {
////            // Load the image bitmap and preprocess it for TensorFlow Lite
////            val imageBitmap = loadAndRotateImage(photoFilePath)
////            val inputBuffer = preprocessImageForTFLite(imageBitmap)
////
////            // Run inference
////            val outputBuffer = Array(1) { FloatArray(NUM_CLASSES) }
////            tflite.run(inputBuffer, outputBuffer)
////
////            // Post-process the inference result
////            val classificationResult = postprocessTFLiteOutput(outputBuffer[0])
////
////            // Update the result TextView
////            resultTextView.text = classificationResult
////            resultTextView.visibility = View.VISIBLE
////        } catch (e: Exception) {
////            e.printStackTrace()
////            // Handle the exception as needed
////        }
////    }
////
////    // ...
////
////    private fun loadAndRotateImage(photoFilePath: String?): Bitmap {
////        if (!photoFilePath.isNullOrEmpty() && File(photoFilePath).exists()) {
////            try {
////                val options = BitmapFactory.Options()
////                options.inJustDecodeBounds = true
////                BitmapFactory.decodeFile(photoFilePath, options)
////                val imageWidth = options.outWidth
////                val imageHeight = options.outHeight
////
////                val exif = ExifInterface(photoFilePath)
////                val orientation = exif.getAttributeInt(
////                    ExifInterface.TAG_ORIENTATION,
////                    ExifInterface.ORIENTATION_UNDEFINED
////                )
////
////                val matrix = Matrix()
////                when (orientation) {
////                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
////                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
////                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
////                }
////
////                options.inJustDecodeBounds = false
////                val bitmap = BitmapFactory.decodeFile(photoFilePath, options)
////                return Bitmap.createBitmap(
////                    bitmap,
////                    0, 0, imageWidth, imageHeight,
////                    matrix, true
////                )
////            } catch (e: Exception) {
////                e.printStackTrace()
////                // Handle the exception as needed
////            }
////        }
////        // Return a default bitmap in case of failure
////        return BitmapFactory.decodeResource(resources, android.R.drawable.ic_menu_gallery)
////    }
////
////    private fun preprocessImageForTFLite(imageBitmap: Bitmap): ByteBuffer {
////        val inputSize = 214 // Specify the input size required by your TensorFlow Lite model
////        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
////        byteBuffer.order(ByteOrder.nativeOrder())
////
////        // Assuming the model accepts raw pixel values, just flatten the pixels and put them into the ByteBuffer
////        val pixels = IntArray(inputSize * inputSize)
////        imageBitmap.getPixels(pixels, 0, imageBitmap.width, 0, 0, imageBitmap.width, imageBitmap.height)
////
////        for (pixel in pixels) {
////            // Extract RGB values from the pixel
////            val red = (pixel shr 16 and 0xFF).toFloat()
////            val green = (pixel shr 8 and 0xFF).toFloat()
////            val blue = (pixel and 0xFF).toFloat()
////
////            // Add the raw pixel values to the ByteBuffer
////            byteBuffer.putFloat(red / 255.0f)
////            byteBuffer.putFloat(green / 255.0f)
////            byteBuffer.putFloat(blue / 255.0f)
////        }
////
////        return byteBuffer
////    }
////
////
////    private fun postprocessTFLiteOutput(outputBuffer: FloatArray): String {
////        // Your post-processing logic for TensorFlow Lite output
////        // Convert the output buffer to a meaningful classification result
////        // Note: You need to replace this part with your actual postprocessing logic
////
////        // Sort all values in the original array
////        val sortedValues = outputBuffer.sorted()
////
////        // Convert the sorted values to a formatted string
////        val result = "Sorted Values: ${sortedValues.joinToString(", ")}"
////
////        return result
////    }
////
////
////
////
////
////    companion object {
////        private const val NUM_CLASSES = 25 // Adjust this based on your model's number of classes
////    }
////
////    private fun loadImageIntoImageView(photoFilePath: String?, imageView: ImageView) {
////        if (!photoFilePath.isNullOrEmpty() && File(photoFilePath).exists()) {
////            try {
////                val options = BitmapFactory.Options()
////                options.inJustDecodeBounds = true
////                BitmapFactory.decodeFile(photoFilePath, options)
////                val imageWidth = options.outWidth
////                val imageHeight = options.outHeight
////
////                val exif = ExifInterface(photoFilePath)
////                val orientation = exif.getAttributeInt(
////                    ExifInterface.TAG_ORIENTATION,
////                    ExifInterface.ORIENTATION_UNDEFINED
////                )
////
////                val matrix = Matrix()
////                when (orientation) {
////                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
////                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
////                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
////                }
////
////                options.inJustDecodeBounds = false
////                val bitmap = BitmapFactory.decodeFile(photoFilePath, options)
////                val rotatedBitmap = Bitmap.createBitmap(
////                    bitmap,
////                    0, 0, imageWidth, imageHeight,
////                    matrix, true
////                )
////
////                imageView.setImageBitmap(rotatedBitmap)
////                imageView.visibility = View.VISIBLE
////            } catch (e: Exception) {
////                e.printStackTrace()
////                // Handle the exception as needed
////            }
////        } else {
////            // Log a message or handle the case where the file doesn't exist
////        }
////    }
////
//////override fun onCreateView(
//////    inflater: LayoutInflater, container: ViewGroup?,
//////    savedInstanceState: Bundle?
//////): View {
//////    // Inflate the layout for this fragment
//////    return inflater.inflate(R.layout.fragment_classification_result, container, false)
//////}
////}
