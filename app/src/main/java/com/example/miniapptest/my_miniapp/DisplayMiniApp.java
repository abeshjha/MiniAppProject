package com.example.miniapptest.my_miniapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.miniapptest.R;

public class DisplayMiniApp extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_mini_app);
        webView = (WebView)findViewById(R.id.display_apps);
        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
        webView.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface"); // To call methods in Android from using js in the html, AndroidInterface.showToast, AndroidInterface.getAndroidVersion etc
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                final String[] permissions = request.getResources();
                if (permissions.length > 0) {
                    // Check if the mini app has permission in its manifest
                    String permission = permissions[0];
                  //  boolean hasPermission = check_permission_from_manifest(permissions);
                    boolean hasPermission = true;

                    if (hasPermission) {
                        // Grant the permission
                        if(permission == "android.webkit.audio"){
                            //grant audio permission.

                        }
                        else if(permission == "android.webkit.camera"){
                            //grant audio permission.
                        }
                        else if(permission == "android.webkit.location"){
                            //grant audio permission.
                        }
                        Toast.makeText(DisplayMiniApp.this, permission, Toast.LENGTH_SHORT).show();
                        request.grant(permissions);
                    } else {
                        // Deny the permission
                        Toast.makeText(DisplayMiniApp.this, "Sorry! This miniapp doesn't have this permission.", Toast.LENGTH_SHORT).show();
                        request.deny();
                    }
                }
            }


        });


    }

}