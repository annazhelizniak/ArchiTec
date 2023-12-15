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

class ClassificationResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_classification_result, container, false)

        // Retrieve data from the arguments bundle
        val args = arguments
        val photoFilePath = args?.getString("photoFilePath")

        // Load the image into the ImageView using your custom function
        val imageView: ImageView = view.findViewById(R.id.user_input)
        loadImageIntoImageView(photoFilePath, imageView)


        return view
    }
    private fun loadImageIntoImageView(photoFilePath: String?, imageView: ImageView) {
        if (!photoFilePath.isNullOrEmpty() && File(photoFilePath).exists()) {
            try {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(photoFilePath, options)
                val imageWidth = options.outWidth
                val imageHeight = options.outHeight

                val exif = ExifInterface(photoFilePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
                }

                options.inJustDecodeBounds = false
                val bitmap = BitmapFactory.decodeFile(photoFilePath, options)
                val rotatedBitmap = Bitmap.createBitmap(
                    bitmap,
                    0, 0, imageWidth, imageHeight,
                    matrix, true
                )

                imageView.setImageBitmap(rotatedBitmap)
                imageView.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception as needed
            }
        } else {
            // Log a message or handle the case where the file doesn't exist
        }
    }

//override fun onCreateView(
//    inflater: LayoutInflater, container: ViewGroup?,
//    savedInstanceState: Bundle?
//): View {
//    // Inflate the layout for this fragment
//    return inflater.inflate(R.layout.fragment_classification_result, container, false)
//}
}
