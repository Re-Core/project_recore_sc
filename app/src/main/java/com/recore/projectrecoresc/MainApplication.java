package com.recore.projectrecoresc;

import android.app.Application;
import android.app.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.recore.projectrecoresc.Helper.LocalHelper;

import java.util.Random;

public class MainApplication extends Application {

    public static  final String NOTIFICATION_CHANNEL_ID_APPLICATION = "com.recore.projectrecoresc.application";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalHelper.onAttachContext(base,"en"));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_APPLICATION,
                    "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }


    }
}


