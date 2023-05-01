package com.example.miniapptest.my_miniapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.miniapptest.R;

public class ZipMiniApp extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_mini_app);
        webView = (WebView)findViewById(R.id.wvzip);
        download_miniapp downloader = new download_miniapp(this,webView);
        downloader.downloadMiniApp("https://ica.edu.np/Dems/API/miniapp.zip");


    }

}