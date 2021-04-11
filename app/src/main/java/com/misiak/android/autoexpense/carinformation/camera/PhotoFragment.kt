package com.misiak.android.autoexpense.carinformation.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.lifecycle.Observer
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.misiak.android.autoexpense.FragmentWithOverflowMenu
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.authentication.SignInFragment
import com.misiak.android.autoexpense.carinformation.CarInformationFragmentArgs
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentPhotoBinding
import com.misiak.android.autoexpense.mainscreen.saveorupdate.SaveOrUpdateCarFragmentArgs
import com.misiak.android.autoexpense.repository.CarRepository
import kotlinx.android.synthetic.main.fragment_save_or_update_car.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoFragment : FragmentWithOverflowMenu() {
    private lateinit var viewModel: PhotoFragmentViewModel
    private lateinit var binding: FragmentPhotoBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var baseContext: Context
    private lateinit var account: GoogleSignInAccount
    private lateinit var repository: CarRepository
    private var carReady = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo, container, false)
        account = CarInformationFragmentArgs.fromBundle(requireArguments()).account
        val database = getDatabase(requireContext().applicationContext)
        val carId = PhotoFragmentArgs.fromBundle(requireArguments()).carId
        repository = CarRepository(database, account)
        val viewModelFactory = PhotoFragmentViewModelFactory(repository, carId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PhotoFragmentViewModel::class.java)
        baseContext = this.requireContext()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        outputDirectory = getOutputDirectory()


        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        setUpTakePhotoObserver()
        setUpConnectionErrorListener()
        setUpTokenExpirationListener()
        setUpOperationSuccessListener()
        setUpOperationFailedListener()
        setUpCarObserver()

        cameraExecutor = Executors.newSingleThreadExecutor()

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this.context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(Surface.ROTATION_90)
                .build()
            val viewPort = ViewPort.Builder(Rational(binding.viewFinder.width, binding.viewFinder.width), Surface.ROTATION_180).build()
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageCapture!!)
                .setViewPort(viewPort)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, useCaseGroup)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this.requireContext()))
    }

    private fun takePhoto() {
        if (carReady) {
            val imageCapture = imageCapture ?: return
            val photoFileName = SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"

            val photoFile = File(
                outputDirectory,
                photoFileName
            )

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this.requireContext()), object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)
                        val msg = "Photo capture succeeded!"
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)
                        viewModel.saveFileFromUri(photoFileName, outputDirectory)
                    }
                })
        }
        else {
            showSnackbar(requireActivity().getString(R.string.operation_failed_error))
        }

    }

    private fun getOutputDirectory(): File {
        val mediaDir = this.requireContext().getExternalFilesDirs("data").firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else this.requireContext().filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this.requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setUpTakePhotoObserver() {
        viewModel.takePhoto.observe(viewLifecycleOwner, Observer {
            if (it) {
                takePhoto()
                viewModel.doneTakingPhoto()
            }
        })
    }

    private fun setUpConnectionErrorListener() {
        viewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackbar(requireActivity().getString(R.string.internet_connection_error))
                viewModel.connectionErrorHandled()
            }
        })
    }

    private fun setUpTokenExpirationListener() {
        viewModel.tokenExpired.observe(viewLifecycleOwner, Observer {
            if (it) {
                SignInFragment.getAccount(requireContext()) { account ->
                    updateRepositoryAccount(
                        account
                    )
                }
            }
        })
    }

    private fun updateRepositoryAccount(account: GoogleSignInAccount) {
        repository.updateToken(account)
        viewModel.expiredTokenHandled()
        showSnackbar(requireActivity().getString(R.string.operation_failed_error))
    }

    private fun setUpOperationSuccessListener() {
        viewModel.updateSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                parentFragmentManager.popBackStack()
                viewModel.navigatedOnSuccess()
            }
        })
    }

    private fun setUpOperationFailedListener() {
        viewModel.unknownError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackbar(requireActivity().getString(R.string.operation_failed_error))
                viewModel.unknownErrorHandled()
            }
        })
    }

    private fun setUpCarObserver() {
        viewModel.car.observe(viewLifecycleOwner, Observer {
            it?.let {
                carReady = true
            }
        })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}