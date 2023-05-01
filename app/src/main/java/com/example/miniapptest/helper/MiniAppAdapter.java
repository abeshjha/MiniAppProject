package com.example.miniapptest.helper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniapptest.R;
import com.example.miniapptest.my_miniapp.DisplayMiniApp;

import java.util.List;

public class MiniAppAdapter extends RecyclerView.Adapter<MiniAppAdapter.MiniAppViewHolder> {

    private List<MiniAppList> mMiniApps;
    private Context mContext;

    public MiniAppAdapter(Context context, List<MiniAppList> miniApps) {
        mContext = context;
        mMiniApps = miniApps;
    }

    @NonNull
    @Override
    public MiniAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mini_app_adapter, parent, false);
        return new MiniAppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniAppViewHolder holder, int position) {
        MiniAppList miniApp = mMiniApps.get(position);
        holder.bind(miniApp);
    }

    @Override
    public int getItemCount() {
        return mMiniApps.size();
    }

    class MiniAppViewHolder extends RecyclerView.ViewHolder {

        private TextView mAppNameTextView;
        private TextView mPermissionsTextView;

        MiniAppViewHolder(View itemView) {
            super(itemView);
            mAppNameTextView = itemView.findViewById(R.id.tv_app_name);
            mPermissionsTextView = itemView.findViewById(R.id.tv_permissions);
        }

        void bind(final MiniAppList miniApp) {
            mAppNameTextView.setText(miniApp.getName());
            mPermissionsTextView.setText(miniApp.getPermissions());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DisplayMiniApp.class);
                    intent.putExtra("url", miniApp.getUrl());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}

