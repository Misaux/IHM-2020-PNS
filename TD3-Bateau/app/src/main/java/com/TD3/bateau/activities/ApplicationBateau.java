package com.TD3.bateau.activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Objects;

public class ApplicationBateau extends Application {
    public static final String CHANNEL_ID = "BateauTD3";
    private static NotificationManager notificationManager;


    private void createNotificationChannel(CharSequence name, String descriptionChannel, int importance) {
        // for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(descriptionChannel);
            // upadate NotificationManager - cannot be changed after
            notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }




    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(  "Td3Bateau",
                "Channel pour l'application td3 bateau",
                NotificationManager.IMPORTANCE_MAX);
    }
}