package com.george.sliceoflife.extrabeat.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.george.sliceoflife.extrabeat.R

class PrivacyPolicyActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        val webview = findViewById<WebView>(R.id.webViewpolicy)
        webview.settings.javaScriptEnabled = true
        webview.settings.builtInZoomControls = true
        webview.settings.displayZoomControls = false
        webview.webChromeClient = WebChromeClient()

        webview.loadUrl("https://github.com/emmanuel-george/Music--bay/blob/main/privacy%20policy")
        webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }
}