package id.cervicam.mobile.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import id.cervicam.mobile.R
import id.cervicam.mobile.activities.CameraActivity
import id.cervicam.mobile.services.MainService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.*
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Collection of common functions
 *
 */
class Utility {
    companion object {
        /**
         * Hide all notifications on status bar
         * It is helpful to avoid user from notifications and let them to focus on current activity
         *
         * @param window    Window of activity
         */
        fun hideStatusBar(window: Window) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        /**
         * Set status bar color as you want by passing color id through argument
         *
         * @param window    Window of activity
         * @param context   context of activity
         * @param color     Color id
         */
        fun setStatusBarColor(window: Window, context: Context, @ColorRes color: Int) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context, color)
        }

        /**
         * Get available output directory from media directory
         * Use file directory if media direc