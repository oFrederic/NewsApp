package com.example.android.newsapp;

import android.os.Parcel;
import android.os.Parcelable;

public class News implements Parcelable {

    // Different fields of the object.
    private final String title;
    private final String section;
    private final String author;
    private final String date;
    private final String link;

    public News(String title, String section, String author, String date, String link) {
        this.title = title;
        this.section = section;
        this.author = author;
        this.date = date;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    protected News(Parcel in) {
        this.title = in.readString();
        this.section = in.readString();
        this.author = in.readString();
        this.date = in.readString();
        this.link = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.section);
        dest.writeString(this.author);
        dest.writeString(this.date);
        dest.writeString(this.link);
    }
}
