package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private static final String TAG = NewsLoader.class.getName();
    private String url;

    // Constructs a NewsLoader
    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // Background thread
    @Override
    public List<News> loadInBackground() {
        if (url == null) {
            return null;
        }

        List<News> news = null;
        try {
            news = NetworkUtils.fetchNewsData(url);
        } catch (NetworkUtils.NetworkException e) {
            Log.e(TAG, "Error while using network: " + e, e);
        }
        return news;
    }
}
