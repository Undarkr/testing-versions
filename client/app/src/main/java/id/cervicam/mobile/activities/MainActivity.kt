package id.cervicam.mobile.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.cervicam.mobile.R
import id.cervicam.mobile.fragments.Button
import id.cervicam.mobile.helper.Utility
import id.cervicam.mobile.services.MainService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A starter activity when the app is starting
 *
 */
class MainActivity : AppCompatActivity() {
    companion object {
        const val GO_TO_ANOTHER_ACTIVITY_CODE = 101
    }

    // List of classification request that has been sent from server
    private var classifications: