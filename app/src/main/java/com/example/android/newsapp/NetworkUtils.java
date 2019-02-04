package com.example.android.newsapp;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class NetworkUtils {

    /**
     * Custom exception with meaningful message and an optional cause.
     */
    public static class NetworkException extends IOException {
        public NetworkException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    private NetworkUtils() {
    }


    public static List<News> fetchNewsData(String requestUrl) throws NetworkException {
        if (TextUtils.isEmpty(requestUrl)) {
            throw new NetworkException("Empty request url", null);
        }

        URL url;
        try {
            url = new URL(requestUrl);
        } catch (Exception e) {
            throw new NetworkException("Error while building URL", e);
        }

        String jsonResponse = requestNetwork(url);

        return convertJsonToNews(jsonResponse);
    }

    private static String requestNetwork(URL url) throws NetworkException {
        String jsonResponse;

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.connect();

            InputStream inputStream = null;
            try {
                // Check if network response is correct.
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    throw new NetworkException("Error response code: " + urlConnection.getResponseCode(), null);
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        // Ignores this error
                    }
                }
            }
        } catch (IOException e) {
            throw new NetworkException("Error while requesting network: " + e, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        StringBuilder output = new StringBuilder();

        InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        return output.toString();
    }

    private static List<News> convertJsonToNews(String newsJSON) throws NetworkException {
        final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        List<News> news = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(newsJSON);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);

                String author = null;
                if (item.has("tags")) {
                    JSONArray tagsArray = item.getJSONArray("tags");

                    if (tagsArray.length() > 0) {
                        JSONObject tagObject = (JSONObject) tagsArray.get(0);

                        author = tagObject.getString("webTitle");
                    }
                }

                String title = item.getString("webTitle");
                String section = item.getString("sectionName");
                Date date = dateFormatter.parse(item.getString("webPublicationDate"));
                String link = item.getString("webUrl");

                news.add(new News(
                        title,
                        section,
                        author,
                        dateFormatter.format(date),
                        link));
            }
        } catch (Exception e) {
            throw new NetworkException("Error while decoding JSON: " + e, e);
        }

        return news;
    }
}
