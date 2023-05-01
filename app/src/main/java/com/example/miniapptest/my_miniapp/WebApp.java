package com.example.miniapptest.my_miniapp;

import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniapptest.R;

public class WebApp extends AppCompatActivity {
    private WebView webView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_app);

        webView = (WebView)findViewById(R.id.wvpage);
        webView.loadUrl("https://abeshjha.github.io/");
        webView.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface"); // To call methods in Android from using js in the html, AndroidInterface.showToast, AndroidInterface.getAndroidVersion etc
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
    }

        private class MyWebViewClient extends WebViewClient {
            @Override
            public void onPageFinished (WebView view, String url) {
                //Calling a javascript function in html page
                view.loadUrl("javascript:alert(showVersion('called by Android'))");
            }
        }
        private class MyWebChromeClient extends WebChromeClient {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        }


}