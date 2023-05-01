package com.example.miniapptest.helper;

public class MiniAppList {
    private String mName;
    private String mUrl;
    private String mPermissions;
    private String mVersion;
    private String mDescription;

    public MiniAppList(String name, String url, String permissions, String version, String description) {
        mName = name;
        mUrl = url;
        mPermissions = permissions;
        mVersion = version;
        mDescription = description;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getPermissions() {
        return mPermissions;
    }

    public String getVersion() {
        return mVersion;
    }
}
