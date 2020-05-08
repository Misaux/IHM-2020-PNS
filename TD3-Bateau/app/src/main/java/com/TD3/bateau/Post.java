package com.TD3.bateau;

import android.graphics.Bitmap;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

public class Post {
    private String bitmapName;
    private String title;
    private String comment;
    private GeoPoint location;
    private String theme = "Autre";
    Date date;
    private int likeCount;

    public String getBitmapName() {
        return bitmapName;
    }

    public String getTheme() {
        return theme;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getComment() {
        return comment;
    }

    public String getTitle() {
        return title;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setBitmapName(String bitmapName) {
        this.bitmapName = bitmapName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
