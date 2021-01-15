
package id.cervicam.mobile.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import id.cervicam.mobile.R
import id.cervicam.mobile.helper.Utility
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Use to get image either from camera or gallery
 *
 */
class CameraActivity : AppCompatActivity() {
    companion object {
        private val PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private const val REQUEST_PERMISSION_CODE = 101
        private const val IMAGE_GALLERY_REQUEST_CODE = 2001
        private const val IMAGE_PREVIEW_ACTIVITY_REQUEST_CODE = 90
    }

    private var savedImage: File? = null

    private var flashIsOn: Boolean = false
    private var cameraProvider: ProcessCameraProvider? = null
    private var selectedCamera: CameraSelector? = null
    private var camera: Camera? = null
    private var imagePreview: Preview? = null
    private var imageCapture: ImageCapture? = null

    /**
     * Not only create view but also set all listeners and check camera permission
     *
     * @param   savedInstanceState  Bundle of activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.hideStatusBar(window)
        Utility.setStatusBarColor(window, this@CameraActivity, R.color.colorBlack)
        supportActionBar?.hide()
        setContentView(R.layout.activity_camera)
        setListeners()

        if (hasCameraPermission()) {
            openCamera()
        } else {
            this.let { ActivityCompat.requestPermissions(it, PERMISSIONS, REQUEST_PERMISSION_CODE) }
        }
    }

    /**
     * Set button listeners
     *
     */
    private fun setListeners() {
        galleryButton.setOnClickListener {
            openGallery()
        }

        takePictureButton.setOnClickListener {
            takePicture()
        }

        flashButton.setOnClickListener {
            if (camera!!.cameraInfo.hasFlashUnit()) {
                flashIsOn = !flashIsOn
                camera!!.cameraControl.enableTorch(flashIsOn)
            } else {
                Toast.makeText(this, "Unable to use flash", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Check whether all needed permission are granted by client or not
     *
     */
    private fun hasCameraPermission(): Boolean {
        return PERMISSIONS.fold(
            true,
            { allPermissions, permission ->
                allPermissions && this.let {
                    ActivityCompat.checkSelfPermission(
                        it, permission
                    )
                } == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    /**
     * Setup camera and integrate it to the surface
     *
     */
    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind life-cycle of camera
            cameraProvider = cameraProviderFuture.get()

            // Set camera preview
            imagePreview = Preview.Builder().build()

            // Set image capture
            imageCapture = ImageCapture.Builder().build()

            // Select back camera
            selectedCamera =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            setCameraCycles()
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Send a pick request to gallery app
     *
     */
    private fun openGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            startActivityForResult(this, IMAGE_GALLERY_REQUEST_CODE)
        }
    }

    /**
     * Set all parts of camera in correct order of cycle
     *
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setCameraCycles() {
        if (cameraProvider == null || selectedCamera == null || imagePreview == null || imageCapture == null) return

        try {
            cameraProvider!!.unbindAll()
            camera =
                cameraProvider!!.bindToLifecycle(this, selectedCamera!!, imagePreview, imageCapture)
            imagePreview?.setSurfaceProvider(cameraView.createSurfaceProvider(camera?.cameraInfo))
        } catch (e: Exception) {
            // All exception
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

        // Set tap-to-focus handler
        cameraView.afterMeasured {
            cameraView.setOnTouchListener { _, event ->