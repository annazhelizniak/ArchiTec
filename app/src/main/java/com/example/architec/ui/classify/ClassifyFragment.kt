package com.example.architec.ui.classify


import android.app.Activity
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.architec.R
import com.example.architec.databinding.FragmentReflowBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ClassifyFragment : Fragment() {

    private var _binding: FragmentReflowBinding? = null
    private lateinit var photoPreviewImageView: ImageView

    private val binding get() = _binding!!
    var photoFilePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val classifyViewModel =
            ViewModelProvider(this).get(ClassifyViewModel::class.java)

        _binding = FragmentReflowBinding.inflate(inflater, container, false)
        val root: View = binding.root
        photoPreviewImageView = root.findViewById(R.id.selected_image)

        val galleryButton: Button = root.findViewById(R.id.gallery_button)
        galleryButton.setOnClickListener {
            openGallery()
        }
        return root
    }

    private val cameraPermissionCode = 101
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private var selectedImageUri: Uri? = null


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri1: Uri? = data?.data
            selectedImageUri1?.let {
                selectedImageUri = selectedImageUri1
                showSelectedImage(it)
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun showSelectedImage(imageUri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            photoPreviewImageView.setImageBitmap(bitmap)
            photoPreviewImageView.visibility = View.VISIBLE
            binding.cameraPreviewView.visibility = View.INVISIBLE
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        imageCapture = ImageCapture.Builder().build()

        if (hasCameraPermission()) {
            openCamera()
        } else {
            requestCameraPermission()
        }

        val cameraButton: Button = view.findViewById(R.id.camera_button)
        cameraButton.setOnClickListener {
            openCameraView()
        }

        val nextButton: Button = view.findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            navigateToNextActivity()
        }
    }

    private fun openCameraView() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                photoPreviewImageView.setImageBitmap(it)
                photoPreviewImageView.visibility = View.VISIBLE
                binding.cameraPreviewView.visibility = View.INVISIBLE
                selectedImageUri = getImageUriFromBitmap(requireContext(), it)
            }
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun takePhoto() {
        val photoFile = getOutputFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    photoFilePath = photoFile.absolutePath
                    selectedImageUri = photoFile.toUri()
                    showPhotoPreview(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }
            }
        )
    }

    private fun showPhotoPreview(photoFile: File) {
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(photoFile.absolutePath, options)
            val imageWidth = options.outWidth
            val imageHeight = options.outHeight

            val exif = ExifInterface(photoFile.absolutePath)
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
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath, options)
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0, 0, imageWidth, imageHeight,
                matrix, true
            )

            photoPreviewImageView.setImageBitmap(rotatedBitmap)
            photoPreviewImageView.visibility = View.VISIBLE

            binding.cameraPreviewView.visibility = View.INVISIBLE
        } catch (e: Exception) {
        }
    }


// In ClassifyFragment.kt

    private fun navigateToNextActivity() {
        val imageUri = selectedImageUri

        if (imageUri != null) {
            val intent = Intent(requireContext(), ClassificationResultActivity::class.java).apply {
                putExtra("selectedImageUri", imageUri)
            }
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
        tempFile.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return tempFile
    }


    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Set up the PreviewView and ImageCapture
            val preview: Preview = Preview.Builder().build()
            val cameraPreviewView: PreviewView = binding.cameraPreviewView
            preview.setSurfaceProvider(cameraPreviewView.surfaceProvider)

            // Unbind any previous use-cases and bind new ones
            cameraProvider.unbindAll()
            try {
                // Bind the camera use cases to the lifecycle of the fragment
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,  // or specify the camera selector as needed
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                // Handle exceptions
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            // Handle the case where the user denied the camera permission
            // You may want to show a message or take alternative actions
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            cameraPermissionCode
        )
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build()

                val cameraPreviewView = binding.cameraPreviewView
                preview.setSurfaceProvider(cameraPreviewView.surfaceProvider)

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview
                )
            } catch (e: Exception) {
                // Log the exception for debugging
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private val outputDirectory: File by lazy {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        } ?: requireContext().filesDir
        File(mediaDir, "photos").apply { mkdirs() }
    }

    private fun getOutputFile(): File {
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        return File(outputDirectory, "IMG_${formatter.format(Date())}.jpg")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}