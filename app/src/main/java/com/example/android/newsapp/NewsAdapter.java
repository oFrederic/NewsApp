package com.example.android.newsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Activity context, ArrayList<News> news) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the current position of News.
        final News item = getItem(position);

        // Find the TextView in the list_item.
        TextView titleNewsTextView = listItemView.findViewById(R.id.news_title);
        TextView sectionNewsTextView = listItemView.findViewById(R.id.news_section);
        TextView authorNewsTextView = listItemView.findViewById(R.id.news_author);
        TextView publicationDateTextView = listItemView.findViewById(R.id.news_date);

        // Set proper value in each fields.
        titleNewsTextView.setText(item.getTitle());
        sectionNewsTextView.setText(String.format("#%s", item.getSection()));
        authorNewsTextView.setText(String.format("By %s", item.getAuthor()));
        publicationDateTextView.setText(String.valueOf(item.getDate()));

        return listItemView;
    }
}
