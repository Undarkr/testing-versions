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
    private var classifications: ArrayList<Pair<String, String>> = ArrayList()
    private var nextUrl: String? = null


    /**
     * Create a view of main activity and set all fragments
     *
     * @param savedInstanceState    Bundle of activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        Utility.setUser(this)
        showAllClassifications()

        resultListView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                openResultActivity(classifications[position].second)
            }
        }

        refresher.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        refresher.setColorSchemeColors(Color.WHITE)
        refresher.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                showAllClassifications()
                refresher.isRefreshing = false
            }
        })

        scrollView.viewTreeObserver
            .addOnScrollChangedListener(object : ViewTreeObserver.OnScrollChangedListener {
                override fun onScrollChanged() {
//                    println("${scrollView.getChildAt(0).height} | ${scrollView.height}")
                    if (scrollView.getChildAt(0).bottom <= scrollView.height + scrollView.scrollY) {
                        showAllClassifications(true)
                    }
                }

            })

        val openCameraButton = Button.newInstance(
            getString(R.string.activity_main_opencamera),
            clickable = true,
            type = Button.ButtonType.FILLED,
            onClick = {
                val openCameraActivityIntent = Intent(this, CameraActivity::class.java)
                startActivityForResult(openCameraActivityIntent, GO_TO_ANOTHER_ACTIVITY_CODE)
            }
        )

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.openCameraButton, o