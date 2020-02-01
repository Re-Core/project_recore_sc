package com.recore.projectrecoresc.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.recore.projectrecoresc.Common.Common;
import com.recore.projectrecoresc.Interface.IOnLoadLocationListener;
import com.recore.projectrecoresc.MainActivity;
import com.recore.projectrecoresc.Model.Camera;
import com.recore.projectrecoresc.Model.User;
import com.recore.projectrecoresc.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.recore.projectrecoresc.MainApplication.NOTIFICATION_CHANNEL_ID_APPLICATION;

public class BackgroundUpdateService extends Service implements IOnLoadLocationListener, GeoQueryEventListener {

    MediaPlayer mediaPlayer;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location lastLocation;

    private DatabaseReference CameraArea;
    private DatabaseReference myLocationRef;


    private GeoFire geoFire;
    private GeoQuery geoQuery;

    private List<LatLng> cameraAreaList;
    private IOnLoadLocationListener listener;

    private boolean isWithinArea = false;


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID_APPLICATION)
                .setContentTitle("SPCAM")
                .setContentText("spcam is running")
                .setSmallIcon(R.drawable.spcam_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);

        buildLocationRequest();
        buildLocationCallBack();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BackgroundUpdateService.this);
        initArea();
        settingGeoFire();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (fusedLocationProviderClient!=null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

        if (Common.currentUser!=null){
            DatabaseReference userLocation =FirebaseDatabase.getInstance().getReference(Common.UserLocation);
            userLocation.child(Common.currentUser.getUserId())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("EXITED","User exited");
                }
            });
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void addUserMarker() {

        if (lastLocation!=null)
            geoFire.setLocation(Common.currentUser.getUserId(), new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                            Log.d("GEOFIRE", String.valueOf(geoFire.getDatabaseReference()));

                        }
                    });
    }

    private void settingGeoFire() {
        myLocationRef = FirebaseDatabase.getInstance().getReference(Common.UserLocation)
                .child(Common.currentUser.getUserId());

        geoFire = new GeoFire(myLocationRef);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                    lastLocation = locationResult.getLastLocation();
//                    addUserMarker();

            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(10f);
    }

    private void initArea() {

        CameraArea = FirebaseDatabase.getInstance().getReference(Common.CameraLocationRef)
                .child(Common.CameraArea);

        listener = this;

        CameraArea.orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (geoQuery!=null){
                    geoQuery.removeAllListeners();
//                        geoQuery.removeGeoQueryEventListener(HomeActivity.this);
                }

                List<Camera> latLngList = new ArrayList<>();


                for (DataSnapshot locationShot : dataSnapshot.getChildren()) {

                    //if the camera exceeded the 3 hour limit delete it
                    long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(3, TimeUnit.HOURS);
                    if ((long)locationShot.child("timeStamp").getValue()<cutoff){
                        locationShot.getRef().removeValue();


                    } else {  // if not then add it to the list and show it on map
                        String lat = locationShot.child("latitude").getValue().toString();
                        String lng = locationShot.child("longitude").getValue().toString();

                        Log.d("latitude", lat);
                        Log.d("latitude", lng);

                        double latitude = Double.parseDouble(lat);
                        double longitude = Double.parseDouble(lng);


                        Camera latLng = new Camera();
                        latLng.setLatitude(latitude);
                        latLng.setLongitude(longitude);

                        latLngList.add(latLng);
                    }
                }

                listener.OnLoadLocationSuccess(latLngList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.OnLoadLocationFailed(databaseError.getMessage());
            }
        });


    }

    private void sendNotification(String title, String content) {
        String NOTIFICATION_CHANNEL_ID = "com.recore.projectrecoresc.services";

        String soundPath = "android.resource://"+getPackageName()+"/"+R.raw.alarm;
        Uri uri = Uri.parse(soundPath);

        AudioAttributes audioAttributes = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ASSISTANT)
                    .build();
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "My Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.setDescription("Channel Description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 400, 200, 400});
            notificationChannel.enableVibration(true);

            notificationChannel.setSound(uri, audioAttributes);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.spcam_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spcam_icon));


        Notification notification = builder.build();
        notificationManager.notify(new Random().nextInt(), notification);


    }

    private void addCircle() {

        if (geoQuery != null) {

            //  geoQuery.removeGeoQueryEventListener(this);
//                geoQuery.removeGeoQueryEventListener(HomeActivity.this);
//
//                geoQuery.removeAllListeners();

        }

        for (LatLng latLng : cameraAreaList) {

            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.175f);
            geoQuery.addGeoQueryEventListener(BackgroundUpdateService.this);

            Log.d("GEOFIRE", String.valueOf(geoFire.getDatabaseReference()));

        }


    }

    // interface
    @Override
    public void OnLoadLocationSuccess(List<Camera> latLngs) {
        cameraAreaList = new ArrayList<>();

        for (Camera myLatLng : latLngs) {
            LatLng convert = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
            cameraAreaList.add(convert);
        }

            addUserMarker();
            addCircle();


    }

    @Override
    public void OnLoadLocationFailed(String msg) {
        Toast.makeText(this, msg
                , Toast.LENGTH_SHORT).show();
    }


    //Query
    @Override
    public void onKeyEntered(String key, GeoLocation location) {

        if (location!=null){
            if (key.equals(Common.currentUser.getUserId())){
                Log.d("LOCATIONENTER",location.latitude+" "+location.longitude);
                Log.d("KEYENTER",key);

                if (!isWithinArea){
                    sendNotification(getResources().getString(R.string.you_string), getResources().getString(R.string.danger_string));

                }
                isWithinArea = true;


                mediaPlayer.start();


            }

        }



    }

    @Override
    public void onKeyExited(String key) {


        if (key.equals(Common.currentUser.getUserId())) {

            if (isWithinArea){
                sendNotification(getResources().getString(R.string.you_string), getResources().getString(R.string.leaved_danger_area_string));
            }

            isWithinArea = false;
            mediaPlayer.stop();



        }

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
//        sendNotification(getResources().getString(R.string.you_string),getResources().getString(R.string.move_with_in_string));

        if (key.equals(Common.currentUser.getUserId())) {

            Log.d("LOCATIONENTER",location.latitude+" "+location.longitude);
            Log.d("KEYMOVED", key);

            isWithinArea = true;

//            mediaPlayer.start();



            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                mediaPlayer.setLooping(false);
            }

        }

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

}
