package com.recore.projectrecoresc.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.recore.projectrecoresc.ChatActivity;
import com.recore.projectrecoresc.Config.Config;
import com.recore.projectrecoresc.Helper.NotificationHelper;
import com.recore.projectrecoresc.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    //creating target to show in notification
    Target target =new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                showNotificationWithImageLevel26(bitmap);
            }else{
                showNotificationWithImage(bitmap);
            }

        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationWithImageLevel26(Bitmap bitmap) {

        NotificationHelper helper = new NotificationHelper(getBaseContext());

        Notification.Builder builder = helper.getChannel(Config.title,Config.message,bitmap);
        helper.getManager().notify(0,builder.build());

    }

    private void showNotificationWithImage(Bitmap bitmap) {
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setSummaryText(Config.message);
        style.bigLargeIcon(bitmap);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =PendingIntent.getActivity(getApplicationContext(),0,intent,0);



        NotificationCompat.Builder notificationBuilder  = (NotificationCompat.Builder)new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(Config.title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent)
                .setStyle(style);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,notificationBuilder.build());

    }


    private void showNotification(Map<String, String> data) {

        String title = data.get("title").toString();
        String body =data.get("body").toString();

        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,444,intent,PendingIntent.FLAG_ONE_SHOT);


        NotificationManager notificationManager
                = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID ="com.recore.projectrecoresc.Services";

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("RECORE CHANNEL");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder notificationBuilder  = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setContentIntent(pendingIntent);

        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());



    }

    private void showNotification(String title, String body) {

        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,444,intent,PendingIntent.FLAG_ONE_SHOT);


        NotificationManager notificationManager
                = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID ="com.recore.projectrecoresc.Services";

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("RECORE CHANNEL");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder  = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setContentIntent(pendingIntent);

        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());

    }

    private void getImage(final RemoteMessage remoteMessage){
        //set title and message
        Config.message =remoteMessage.getNotification().getBody();
        Config.title = remoteMessage.getNotification().getTitle();

        //create thread to fetch image  from notification;

        if (remoteMessage.getData()!=null){
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Get image from data Notification
                    Picasso.get().load(remoteMessage.getData().get("image"))
                            .into(target);
                }
            });
        }
        }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        if (remoteMessage.getData().isEmpty())
//            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
//        else
//            showNotification(remoteMessage.getData());

        //todo make it work offline
//        if (remoteMessage.getData()!=null){
//            getImage(remoteMessage);
//        }

        if (remoteMessage.getData().isEmpty()){
            getImage(remoteMessage);
        }else{
            getImage(remoteMessage.getData());
        }

//        if (remoteMessage.getData().isEmpty()){
//            getImage(remoteMessage);
//        }else {
//            getImage(remoteMessage.getData());
//        }


    }

    private void getImage(Map<String, String> data) {
        final String img =data.get("image").toString();

        //set title and message
        Config.title = data.get("title").toString();
        Config.message =data.get("body").toString();

        //create thread to fetch image  from notification;

        if (data!=null){
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Get image from data Notification
                    Picasso.get().load(img)
                            .into(target);
                }
            });
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("TOKENFIREBASE",s);
    }



}
