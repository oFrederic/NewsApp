package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String NETWORK_REQUEST_URL = "https://content.guardianapis.com/search" +
            "?order-date=published&show-section=true&show-fields=headline,thumbnail" +
            "&show-references=author&show-tags=contributor&page=10&page-size=20&api-key=test";


    private static final int NEWS_LOADER_ID = 1;

    private TextView emptyStateTextView;
    private View circleProgressBar;
    private NewsAdapter adapter;
    private String requestUrl = NETWORK_REQUEST_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsListView = findViewById(R.id.list);
        emptyStateTextView = findViewById(R.id.text);
        circleProgressBar = findViewById(R.id.loading);

        adapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(adapter);

        // Check if network is available before showing news.
        if (isConnected()) {
            // Request network and register this activity as listener.
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        } else {
            circleProgressBar.setVisibility(GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Intent to the appropriate news.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News item = adapter.getItem(position);
                if (item == null) {
                    Toast.makeText(MainActivity.this, "Link Invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create the search menu.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Instantiate the loader with the current search.
        return new NewsLoader(this, requestUrl);
    }

    /**
     * @return if network is available.
     */
    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void search(String query) {
        if (!isConnected()) {
            adapter.clear();
            emptyStateTextView.setVisibility(View.VISIBLE);
            emptyStateTextView.setText(R.string.no_internet_connection);
            return;
        }

        if (TextUtils.isEmpty(query)) {
            requestUrl = NETWORK_REQUEST_URL;
        } else {
            requestUrl = NETWORK_REQUEST_URL + "&q=" + query.replace(" ", "+");
        }

        emptyStateTextView.setVisibility(GONE);
        circleProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        circleProgressBar.setVisibility(GONE);

        adapter.clear();

        if (news != null && !news.isEmpty()) {
            adapter.addAll(news);
            emptyStateTextView.setVisibility(View.GONE);
        } else {
            emptyStateTextView.setText(R.string.news_empty);
            emptyStateTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
    }
}
