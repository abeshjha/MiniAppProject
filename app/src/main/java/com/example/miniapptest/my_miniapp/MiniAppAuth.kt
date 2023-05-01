package com.example.miniapptest.my_miniapp

import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.miniapptest.R

class MiniAppAuth : AppCompatActivity() {
    private var webView: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mini_app_auth)
        webView = findViewById<View>(R.id.wvpage) as WebView
        webView!!.loadUrl("https://mylocation.org/")
        webView!!.addJavascriptInterface(
            WebAppInterface(this),
            "AndroidInterface"
        ) // To call methods in Android from using js in the html, AndroidInterface.showToast, AndroidInterface.getAndroidVersion etc
        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        webView!!.webViewClient = MyWebViewClient()
        webView!!.webChromeClient = MyWebChromeClient()
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            //Calling a javascript function in html page
            view.loadUrl("javascript:alert(showVersion('called by Android'))")
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onJsAlert(
            view: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
            return super.onJsAlert(view, url, message, result)
        }

        override fun onGeolocationPermissionsShowPrompt(
            origin: String?,
            callback: GeolocationPermissions.Callback?
        ) {
            // Display a dialog to ask for permission to access the location
            val alertDialogBuilder = AlertDialog.Builder(this@MiniAppAuth)
            alertDialogBuilder.setMessage("Allow this website to access your location?")
            alertDialogBuilder.setPositiveButton("Allow") { _, _ ->
                val latitude = "100"
                val longitude = "100"
                webView?.loadUrl("javascript:navigator.geolocation.getCurrentPosition = function(success, error, options) { success({ coords: { latitude: $latitude, longitude: $longitude } }); };")


                callback?.invoke(origin, true, false)

            }
            alertDialogBuilder.setNegativeButton("Deny") { _, _ ->
                callback?.invoke(origin, false, false)
            }
            alertDialogBuilder.show()

        }



    }

}