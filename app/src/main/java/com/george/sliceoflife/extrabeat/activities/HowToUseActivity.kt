package com.george.sliceoflife.extrabeat.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.george.sliceoflife.extrabeat.R

class HowToUseActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)
        try {
            val webView = findViewById<WebView>(R.id.webview)
            webView.settings.javaScriptEnabled = true
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false
            webView.loadUrl("https://www.youtube.com/watch?v=jkvMr2hfQuQ")
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            webView.webChromeClient = WebChromeClient()
        } catch (e:Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        // How to use activity
    }

}