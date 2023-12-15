package com.example.architec.ui.classify

import ClassificationResultFragment
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.architec.R
import com.example.architec.databinding.FragmentReflowBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ClassifyFragment : Fragment() {

    private var _binding: FragmentReflowBinding? = null
    private lateinit var photoPreviewImageView: ImageView

    // This property is only valid between onCreateView and
    // onDestroyView.
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

//        val textView: TextView = binding.textReflow
//        classifyViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private val cameraPermissionCode = 101
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Initialize imageCapture
        imageCapture = ImageCapture.Builder().build()

        if (hasCameraPermission()) {
            openCamera()
        } else {
            requestCameraPermission()
        }

        val cameraButton: Button = view.findViewById(R.id.camera_button)
        cameraButton.setOnClickListener {
            takePhoto()
        }

        val nextButton: Button = view.findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            navigateToNextFragment()
        }
    }

    private fun takePhoto() {
        val photoFile = getOutputFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Photo saved successfully
                    // You can update UI or provide feedback to the user if needed
                    photoFilePath = photoFile.absolutePath
                    showPhotoPreview(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    // Photo capture failed
                    exception.printStackTrace()
                    // Handle the error as needed
                }
            }
        )
    }
    private fun showPhotoPreview(photoFile: File) {
        try {
            // Decode the image file to get its dimensions and orientation
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(photoFile.absolutePath, options)
            val imageWidth = options.outWidth
            val imageHeight = options.outHeight

            // Get the orientation from the image file
            val exif = ExifInterface(photoFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            // Rotate the bitmap based on the orientation
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
            }

            // Decode the image file again, applying the rotation matrix
            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath, options)
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0, 0, imageWidth, imageHeight,
                matrix, true
            )

            // Update the ImageView with the rotated bitmap
            photoPreviewImageView.setImageBitmap(rotatedBitmap)
            photoPreviewImageView.visibility = View.VISIBLE

            // Hide the camera preview
            binding.cameraPreviewView.visibility = View.INVISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception as needed
        }
    }



// In ClassifyFragment.kt

    private fun navigateToNextFragment() {
        val bundle = Bundle().apply {
            putString("photoFilePath", photoFilePath)
        }

        val classificationResultFragment = ClassificationResultFragment()
        classificationResultFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, classificationResultFragment)
            .addToBackStack(null)
            .commit()

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