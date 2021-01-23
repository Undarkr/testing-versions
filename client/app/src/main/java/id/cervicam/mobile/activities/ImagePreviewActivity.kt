
package id.cervicam.mobile.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import id.cervicam.mobile.R
import id.cervicam.mobile.fragments.Button
import id.cervicam.mobile.helper.Utility
import id.cervicam.mobile.services.MainService
import kotlinx.android.synthetic.main.activity_image_preview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.File
import java.io.IOException

/**
 * Preview image, expect an image path from the activity that calls this activity
 *
 */
class ImagePreviewActivity : AppCompatActivity() {
    companion object {
        const val KEY_IMAGE_PATH = "IMAGE_URI"
    }

    private var originalImage: File? = null
    private var previewedImage: File? = null

    /**
     * Create a view of image preview and load an image
     *
     * @param savedInstanceState    Bundle of activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.hideStatusBar(window)
        Utility.setStatusBarColor(window, this@ImagePreviewActivity, R.color.colorBlack)
        supportActionBar?.hide()
        setContentView(R.layout.activity_image_preview)
