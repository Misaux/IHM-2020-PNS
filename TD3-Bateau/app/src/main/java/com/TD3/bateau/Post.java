package com.TD3.bateau;

import android.graphics.Bitmap;

import org.osmdroid.util.GeoPoint;

public class Post {
    private Bitmap bitmap;
    private String title;
    private String comment;
    private GeoPoint location;

    public Bitmap getBitmap() {
        return bitmap;
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

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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
