package com.recore.projectrecoresc.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.recore.projectrecoresc.ChatActivity;
import com.recore.projectrecoresc.Config.Config;
import com.recore.projectrecoresc.R;

public class NotificationHelper extends ContextWrapper {
    private  static  final  String RECORE_CHANNEL_ID ="com.recore.projectrecoresc";
    private  static  final  String RECORE_CHANNEL_NAME ="RECORE";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            createChannel();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel channel = new NotificationChannel(RECORE_CHANNEL_ID,RECORE_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);


    }

    public NotificationManager getManager() {
        if (manager==null){
            manager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannel(String title, String body, Bitmap bitmap){

        Notification.Style style = new Notification.BigPictureStyle().bigPicture(bitmap);

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =PendingIntent.getActivity(getApplicationContext(),0,intent,0);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new Notification.Builder(getApplicationContext(),RECORE_CHANNEL_ID)
                .setSmallIcon(R.drawable.spcam_icon)
                .setContentTitle(Config.title)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(defaultSound)
                .setStyle(style);



    }


}
