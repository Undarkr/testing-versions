
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

        // Get image from argument and set it as original image
        val imagePath: String = intent.getStringExtra(KEY_IMAGE_PATH)!!
        originalImage = File(imagePath)

        // Compress image if the size is more than 300 Kb
        if (originalImage!!.length() >= 300 * 1000) {
            previewedImage = File("${cacheDir}/image-preview/${Utility.getBasename(originalImage!!.path)}")
            originalImage?.copyTo(previewedImage!!)
            Utility.compressImage(previewedImage!!.path, 25)
        } else {
            // Use the original image to preview if the size is small enough
            previewedImage = originalImage
        }

        val prevButton = Button.newInstance(
            getString(R.string.activity_imagepreview_previous),
            type = Button.ButtonType.CLEAN,
            color = ContextCompat.getColor(this, R.color.colorWhite),
            onClick = {
                onBackPressed()
            }
        )

        val nextButton = Button.newInstance(
            getString(R.string.activity_imagepreview_next),
            onClick = {
                sendImageAndOpenResultActivity()
            }
        )

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.prevButtonView, prevButton)
            .replace(R.id.nextButtonView, nextButton)
            .commit()

        // Load previewed image
        Picasso.with(this)
            .load(previewedImage)
            .config(Bitmap.Config.RGB_565)
            .into(imageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    progressBarContainer.visibility = View.GONE
                    imageContainer.visibility = View.VISIBLE
                }

                override fun onError() {
                    Toast.makeText(
                        this@ImagePreviewActivity,
                        "Unable to show the image",
                        Toast.LENGTH_LONG
                    ).show()
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            })
    }

    /**
     * Send a canceled result to the one calls this activity and delete preview image from cache folder
     *
     */
    override fun onBackPressed() {
        if (originalImage!!.path != previewedImage!!.path) {
            previewedImage!!.delete()
        }
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    /**
     * If user decides to use the image, then send it to server to be classified and call the page that shows the result
     *
     */
    private fun sendImageAndOpenResultActivity() {
        // Don't send any image if the image is none
        if (originalImage == null) return

        runBlocking {
            launch(Dispatchers.Default) {
                // Send to server
                MainService.classifyImage(
                    this@ImagePreviewActivity,
                    image = originalImage!!,
                    callback = object: Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                runOnUiThread {
                                    if (response.code() == 201) {
                                        // Get a request id from created classification
                                        val body = Utility.parseJSON(response.body()?.string())
                                        val id: String = body["id"].toString()

                                        // Open the result on another activity
                                        val openResultActivityIntent = Intent(this@ImagePreviewActivity, ResultActivity::class.java)
                                        openResultActivityIntent.putExtra(ResultActivity.KEY_REQUEST_ID, id)
                                        startActivity(openResultActivityIntent)

                                        setResult(Activity.RESULT_OK)
                                        finish()
                                    } else {
                                        Toast.makeText(this@ImagePreviewActivity, "Failed to create a classification on server", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this@ImagePreviewActivity, "Request failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
        }
    }
}