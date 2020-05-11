package com.TD3.bateau;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class Post {
    private String bitmapName;
    private String title;
    private String comment;
    private GeoPoint location;
    private String theme = "Autre";
    Date date;
    private int likeCount;
    private long userID;

    public Post(){

    }

    public Post(String title, String comment, GeoPoint location, String theme, Date date, int likeCount, long userID){
        this.title = title;
        this.comment = comment;
        this.location = location;
        this.theme = theme;
        this.date = date;
        this.likeCount = likeCount;
        this.userID = userID;
    }

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

    public long getUserID() {
        return userID;
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

    public void setUserID(long userID) {
        this.userID = userID;
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
