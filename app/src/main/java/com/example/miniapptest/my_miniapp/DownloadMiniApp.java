package com.example.miniapptest.my_miniapp;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.miniapptest.R;
import com.example.miniapptest.helper.ListMiniApp;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DownloadMiniApp extends AppCompatActivity {
    private EditText mUrlInput;
    private Button mLoadButton;
    private static final int REQUEST_CODE_QR_SCAN = 101;

    private Button mListButton;
    private Button mScanButton;
    private Button mDeleteButton;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_mini_app);

        mUrlInput = findViewById(R.id.url_input);
        mLoadButton = findViewById(R.id.load_button);
        mListButton = findViewById(R.id.fetch_miniapp);
        mScanButton = findViewById(R.id.scan_button);
        textView = findViewById(R.id.mini_app_info_text);
        mDeleteButton = findViewById(R.id.delete_miniapp);

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(DownloadMiniApp.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan QR code containing mini app URL");
                integrator.setCameraId(0);  // Use the rear-facing camera
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            }
        });

        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadMiniApp.this, ListMiniApp.class);
                startActivity(intent);
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("MiniApps", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(DownloadMiniApp.this, "Cache Succesfully cleared!", Toast.LENGTH_SHORT).show();
            }
        });

        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mUrlInput.getText().toString();
                loadMiniApp(url);
            }
        });
    }

    private void loadMiniApp(String url) {
        String manifestUrl = url + "/manifest.json";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(manifestUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showError("Manifest not found");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Manifest.json found
                    try {
                        JSONObject manifestJson = new JSONObject(response.body().string());
                        JSONArray permissionsJson = manifestJson.optJSONArray("permissions");
                        List<String> permissionsList = new ArrayList<>();
                        if (permissionsJson != null) {
                            for (int i = 0; i < permissionsJson.length(); i++) {
                                permissionsList.add(permissionsJson.getString(i));
                            }
                        }
                        String permissions = TextUtils.join(", ", permissionsList);
                        String appName = manifestJson.optString("name");
                        String version = manifestJson.optString("version");
                        String description = manifestJson.optString("description");

                        SharedPreferences sharedPrefs = getSharedPreferences("MiniApps", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString(url + "_name", appName);
                        editor.putString(url + "_permissions", permissions);
                        editor.putString(url + "_version", version);
                        editor.putString(url + "_description", description);
                        editor.apply();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                textView.setText("AppName: " + appName + "\n" + "Version: " + version + "\n" + "Description: " + description + "\n" + "Permissions Required: " + "\n" + permissions);

                                textView.setClickable(true);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(DownloadMiniApp.this, DisplayMiniApp.class);
                                        intent.putExtra("url", url);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        showError("Invalid manifest format");
                    }
                } else {
                    // Manifest.json not found
                    showError("Manifest not found");
                }
            }
        });
    }


    private void showError(String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DownloadMiniApp.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Method to handle the result of QR code scanning activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (resultCode == RESULT_OK) {
                String qrContent = data.getStringExtra(Intents.Scan.RESULT);
                mUrlInput.setText(qrContent);
                Toast.makeText((Context) DownloadMiniApp.this, (CharSequence) mUrlInput, Toast.LENGTH_SHORT).show();
                loadMiniApp(qrContent);
            } else {
                Toast.makeText(this, "Scan failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

