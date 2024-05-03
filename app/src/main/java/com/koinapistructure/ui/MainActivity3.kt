package com.koinapistructure.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.koinapistructure.R


class MainActivity3 : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var videoLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        webView = findViewById(R.id.webView)
        videoLayout = findViewById(R.id.full_screen_view_container)

        // Enable JavaScript
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Load YouTube video with autoplay
        val videoId = "eWEF1Zrmdow"
        val videoUrl = "https://www.youtube.com/embed/$videoId?autoplay=1"
        webView.loadDataWithBaseURL(null, getVideoHtml(videoUrl), "text/html", "UTF-8", null)

        // Handle full-screen requests
        webView.webChromeClient = object : WebChromeClient() {
            private var mCustomView: View? = null
            private var mCustomViewCallback: CustomViewCallback? = null

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden()
                    return
                }
                mCustomView = view
                mCustomViewCallback = callback
                // Rotate the screen to landscape mode
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                videoLayout.addView(mCustomView)
                videoLayout.visibility = View.VISIBLE
                webView.visibility = View.GONE
            }

            override fun onHideCustomView() {
                if (mCustomView == null) return
                // Rotate the screen back to portrait mode
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                mCustomView!!.visibility = View.GONE
                videoLayout.removeView(mCustomView)
                mCustomViewCallback!!.onCustomViewHidden()
                mCustomView = null
                webView.visibility = View.VISIBLE
            }
        }
    }

    private fun getVideoHtml(videoUrl: String): String {
        return "<html><body style='margin:0;padding:0;'><iframe width=\"100%\" height=\"100%\" src=\"$videoUrl\" frameborder=\"0\" allowfullscreen></iframe></body></html>"
    }
}
