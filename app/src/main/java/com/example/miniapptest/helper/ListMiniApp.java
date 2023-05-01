package com.example.miniapptest.helper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.miniapptest.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListMiniApp extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MiniAppAdapter mAdapter;
    private List<MiniAppList> mMiniApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_mini_app);

        // Initialize RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load mini apps from shared preferences
        loadMiniApps();

        // Set up adapter for RecyclerView
        mAdapter = new MiniAppAdapter(this, mMiniApps);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadMiniApps() {
        SharedPreferences sharedPrefs = getSharedPreferences("MiniApps", MODE_PRIVATE);

        mMiniApps = new ArrayList<>();

        Map<String, ?> allEntries = sharedPrefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith("_name")) {
                String url = key.substring(0, key.lastIndexOf("_name"));
                String name = entry.getValue().toString();
                String permissions = sharedPrefs.getString(url + "_permissions", "");
                String version = sharedPrefs.getString(url + "_version", "");
                String description = sharedPrefs.getString(url + "_description", "");

                MiniAppList miniApp = new MiniAppList(name, url, permissions, version, description);
                mMiniApps.add(miniApp);
            }
        }
    }
}
