package com.recore.projectrecoresc;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.gigamole.library.PulseView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;


import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.recore.projectrecoresc.Common.Common;
import com.recore.projectrecoresc.Common.Constant;
import com.recore.projectrecoresc.Helper.LocalHelper;
import com.recore.projectrecoresc.Interface.IOnLoadLocationListener;
import com.recore.projectrecoresc.Model.Camera;
import com.recore.projectrecoresc.Model.User;
import com.recore.projectrecoresc.Services.BackgroundUpdateService;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.tecorb.hrmovecarmarkeranimation.AnimationClass.HRMarkerAnimation;
import com.tecorb.hrmovecarmarkeranimation.CallBacks.UpdateLocationCallBack;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import io.paperdb.Paper;



public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GeoQueryEventListener, IOnLoadLocationListener, RewardedVideoAdListener {

    MediaPlayer mediaPlayer;
    //Bottomsheet
    ImageView imgExpandable;
    BottomSheetRiderFragment mBottomSheet;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;

    private Marker currentMarker;

    private Location lastLocation;
    private Location oldLocation;

    private DatabaseReference CameraArea;
    private DatabaseReference myLocationRef;
    private DatabaseReference feedbackRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private StorageReference thumbImageRef;

    private GeoFire geoFire;
    private GeoQuery geoQuery;

    private List<LatLng> cameraAreaList;
    private IOnLoadLocationListener listener;

    private boolean isWithinArea = false;

    private User currentUser = Common.currentUser;
    private Button btnAddCamera;
    private ImageView btnRefresh;
    private ImageView pulseView;

    private Bitmap thumb_bitmap;

    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;

    private SwitchCompat backgroundTaskSwitch;


    SupportMapFragment mapFragment;

    private boolean isBackgroundUpdate = true;

    //Declare timer
    CountDownTimer cTimer = null;
    long startTime = 30000; //first add comes after 30sec

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalHelper.onAttachContext(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        thumbImageRef = FirebaseStorage.getInstance().getReference().child("Thumb_imgs/");
        btnRefresh =(ImageView)findViewById(R.id.refresh_btn);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this ,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
        });

        MobileAds.initialize(this, "ca-app-pub-7311925889927437~2042716974");
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        //InterstitialAd
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        Paper.init(this);

        String language = Paper.book().read("language");
        if (language == null) {
            Paper.book().write("language", "en");
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setLooping(false);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        buildLocationRequest();
                        buildLocationCallBack();
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);

                        initArea();
                        settingGeoFire();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        showToastMessage(getResources().getString(R.string.permission_request_string));
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();


        Log.d("Current","name: "+Common.currentUser.getUsername());

        btnAddCamera = (Button) findViewById(R.id.btnAddNewCamera);
        imgExpandable = (ImageView) findViewById(R.id.imgExpandable);
        pulseView = (ImageView) findViewById(R.id.pv);



        btnAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWithinArea) {
                    Toast.makeText(HomeActivity.this, getResources().getText(R.string.leave_first_msg), Toast.LENGTH_SHORT).show();
                } else {
                    if (lastLocation != null) {
                        addCameraDialog();
                    }
                }

            }
        });

        mBottomSheet = BottomSheetRiderFragment.newInstance("Rider Bottom Sheet");
        imgExpandable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationViewHeader = navigationView.getHeaderView(0);

        TextView txtName = (TextView) navigationViewHeader.findViewById(R.id.txtUserName);
        TextView txtphoneNumber = (TextView) navigationViewHeader.findViewById(R.id.txtPhoneNumber);
        CircleImageView imageAvatar = (CircleImageView) navigationViewHeader.findViewById(R.id.user_avatar);

        backgroundTaskSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.switch_background_task).getActionView();
        backgroundTaskSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isBackgroundUpdate = b;
                savePref();
            }
        });

        txtName.setText(currentUser.getUsername());
        txtphoneNumber.setText(currentUser.getUserPhone());

        if (currentUser.getUserAvatar() != null && !TextUtils.isEmpty(currentUser.getUserAvatar())) {
            Picasso.get()
                    .load(currentUser.getUserAvatar())
                    .into(imageAvatar);
        }

        checkUserState();
        updateView((String) Paper.book().read("language"));


        startTimer();
    }

    private void startTimer() {
        cTimer = new CountDownTimer(startTime, 1000) {
            public void onTick(long millisUntilFinished) {

                if (millisUntilFinished<=10000){
                    long millisecond = millisUntilFinished;
                    // or you already have long value of date, use this instead of milliseconds variable.
//                    String dateString = DateFormat.format("ss", new Date(millisecond)).toString();
//                    btnAddCamera.setText("ad coming in: "+dateString);
                }


            }
            public void onFinish() {

                Random rand=new Random();
                int x = rand.nextInt(2);

               if (x==0){
                   loadInterAd();
               }else{
                   loadRewardedVideoAd();
               }
            }
        };
        cTimer.start();
    }

    private void loadInterAd() {
        mInterstitialAd.show();
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                startTime = 600000; //load next ad in 10min
                startTimer();
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                startTime += 18000;
                startTimer();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    //todo add
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    private void updateView(String language) {
        Context context = LocalHelper.setLocale(this, language);
        Resources resources = context.getResources();

    }

    private void changeLanguageDialog() {
        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);

        dialog.setTitle(getResources().getString(R.string.choose_language_string));

        LayoutInflater inflater = LayoutInflater.from(this);
        View change_language_view = inflater.inflate(R.layout.layout_language, null);

        RadioButton enLang = (RadioButton) change_language_view.findViewById(R.id.radio_english_lng);
        RadioButton kuLang = (RadioButton) change_language_view.findViewById(R.id.radio_kurdish_lng);
        RadioButton arLang = (RadioButton) change_language_view.findViewById(R.id.radio_arabic_lng);

        enLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Paper.book().write("language", "en");
                    updateView((String) Paper.book().read("language"));
                }
            }
        });

        arLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Paper.book().write("language", "ar");
                    updateView((String) Paper.book().read("language"));
                }
            }
        });
        kuLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Paper.book().write("language", "ku");
                    updateView((String) Paper.book().read("language"));
                }
            }
        });

        dialog.setView(change_language_view);

        dialog.setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        dialog.show();

    }

    private void addCameraDialog() {

        final String city,country;
        Geocoder gcd = new Geocoder(HomeActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0){
            city = addresses.get(0).getLocality();
            country=addresses.get(0).getCountryName();
        }else{
            city ="UNKNOWN";
            country="UNKNOWN";
        }




        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.confirm_title));
        builder.setPositiveButton(getResources().getString(R.string.dialog_posative_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Camera camera = new Camera();
                camera.setLatitude(lastLocation.getLatitude());
                camera.setLongitude(lastLocation.getLongitude());
                camera.setTimeStamp(ServerValue.TIMESTAMP);
                camera.setUserId(currentUser.getUserId());
                camera.setUserPhone(currentUser.getUserPhone());
                camera.setUsername(currentUser.getUsername());

                camera.setCameraCountry(country);
                camera.setCameraCity(city);

                CameraArea.push()
                        .setValue(camera)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                updateUserPointAndContribution();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialogInterface.dismiss();
            }
        }).setNegativeButton(getResources().getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void updateUserPointAndContribution() {

        final DatabaseReference userRef= FirebaseDatabase.getInstance().getReference(Common.UserInformation)
                .child(Common.currentUser.getUserId());
        final HashMap<String,Object> pointMap = new HashMap<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String pointStr=Common.currentUser.getUserPoint();
                    int point;
                    int updatedPoint;
                    String updatedPointStr;

                    String contributionStr=Common.currentUser.getUserContribution();
                    int contribution;
                    int updatedcontribution;
                    String updatecontributionStr;


                    pointStr =(String) dataSnapshot.child("userPoint").getValue();
                    point=Integer.parseInt(pointStr);
                    Log.d("POINT",pointStr);
                    updatedPoint =point+25;
                    updatedPointStr =String.valueOf(updatedPoint);

                    contributionStr =(String) dataSnapshot.child("userContribution").getValue();
                    contribution=Integer.parseInt(contributionStr);
                    Log.d("CONTRIBUTION",contributionStr);
                    updatedcontribution =contribution+1;
                    updatecontributionStr =String.valueOf(updatedcontribution);


                    pointMap.put("userPoint",updatedPointStr);
                    pointMap.put("userContribution",updatecontributionStr);
                    userRef.updateChildren(pointMap);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                if (mMap != null) {
                    lastLocation = locationResult.getLastLocation();
                    addUserMarker();
                }
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

    private void showToastMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
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

    private void addUserMarker() {

        if (lastLocation!=null)
        geoFire.setLocation(Common.currentUser.getUserId(), new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                        if (currentMarker != null) {
                            currentMarker.remove();

                        }

                        Log.d("GEOFIRE", String.valueOf(geoFire.getDatabaseReference()));

                         new HRMarkerAnimation(mMap,1000, new UpdateLocationCallBack() {
                            @Override
                            public void onUpdatedLocation(Location updatedLocation) {
                                oldLocation = updatedLocation;
                            }
                        }).animateMarker(lastLocation, oldLocation,currentMarker);



                        updateCameraBearing(mMap, lastLocation.getBearing());


                    }
                });
    }

    private void addCircle() {

            if (geoQuery != null) {

                  //  geoQuery.removeGeoQueryEventListener(this);
//                geoQuery.removeGeoQueryEventListener(HomeActivity.this);
//
//                geoQuery.removeAllListeners();

        }

        for (LatLng latLng : cameraAreaList) {
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(175)
                    .strokeColor(Color.RED)
                    .fillColor(0x22ff0000)
                    .strokeWidth(5.0f));


            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.175f);
            geoQuery.addGeoQueryEventListener(HomeActivity.this);

            Log.d("GEOFIRE", String.valueOf(geoFire.getDatabaseReference()));

        }


    }

    private void sendNotification(String title, String content) {
        String NOTIFICATION_CHANNEL_ID = "com.recore.projectrecoresc";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            notificationChannel.setDescription("Channel Description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 400, 200, 400});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.drawable.spcam_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spcam_icon));

        Notification notification = builder.build();
        notificationManager.notify(new Random().nextInt(), notification);


    }

    private void showInviteIntent() {
        Intent intent =new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,Common.currentUser.getUserId()+getString(R.string.invite_string));
        intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.invite_string));
        startActivity(intent);
    }

    private void showDialogUpdateInformation() {

        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);

        dialog.setTitle(getResources().getString(R.string.update_info_string));
        dialog.setMessage(getResources().getString(R.string.update_info_msg_string));
        LayoutInflater inflater = LayoutInflater.from(this);
        View update_information_layout = inflater.inflate(R.layout.layout_update_information, null);
        final MaterialEditText edtName = update_information_layout.findViewById(R.id.edtName);
        final CircleImageView imgUpload = (CircleImageView) update_information_layout.findViewById(R.id.image_upload);

        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        dialog.setView(update_information_layout);

        dialog.setPositiveButton(getResources().getString(R.string.update_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);
                waitingDialog.show();

                String name = edtName.getText().toString();

                HashMap<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", name);

                if (!TextUtils.isEmpty(name)) {

                    DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference(Common.UserInformation)
                            .child(currentUser.getUserPhone());

                    userInfoRef.updateChildren(userInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.information_updated_string), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.information_updated_error_string), Toast.LENGTH_SHORT).show();

                                    }
                                    waitingDialog.dismiss();
                                }
                            });

                }


            }
        });

        dialog.setNegativeButton(getResources().getString(R.string.cancel_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.img_request_string)), Common.PICK_IMAGE_REQUEST);

    }

    private void checkUserState() {

        if (currentUser.getUserState().equals("blocked")) {

            Toast.makeText(this, currentUser.getUserState(), Toast.LENGTH_SHORT).show();
            androidx.appcompat.app.AlertDialog.Builder msg =new androidx.appcompat.app.AlertDialog.Builder(this);
            msg.setTitle("Message");
            msg.setMessage("Sorry it appears you've benn blocked from the server\n try to conntact our support team viea example@recore.com");

            msg.setCancelable(false);

            msg.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    System.exit(0);
                    dialogInterface.dismiss();
                    return;
                }
            });


            mediaPlayer.stop();
            mediaPlayer.setLooping(false);

            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            msg.show();

        }
    }

    private void deleteUserLocation(){
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

    private void startService(){
        Intent serviceIntent = new Intent(this, BackgroundUpdateService.class);
        ContextCompat.startForegroundService(this,serviceIntent);

    }

    private void stopService(){
        Intent serviceIntent = new Intent(this, BackgroundUpdateService.class);
        stopService(serviceIntent);
    }

    private void savePref() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.backgroundTask,isBackgroundUpdate);
        editor.commit();

    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
        Boolean isIntroActivityOpendBefore = pref.getBoolean(Constant.backgroundTask, isBackgroundUpdate);
        return isIntroActivityOpendBefore;
    }
//    private void showRefillDialog() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);
//        dialog.setTitle(getResources().getString(R.string.plan_title));
//        dialog.setMessage(getResources().getString(R.string.plan_msg));
//        dialog.setCancelable(false);
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View refill_dialog = inflater.inflate(R.layout.layout_rifill, null);
//
//
//        dialog.setView(refill_dialog);
//        dialog.show();
//    }

/*    private void checkUserEndedFreePlan(){
        long timeStamp =(long)Common.currentUser.getUserTimeStamp();

        Date currentDate=new Date();
        Calendar calendar=new GregorianCalendar(); //this initialises to the current system time
        calendar.setTimeInMillis(timeStamp); //change to whatever the long timestamp value from your server is

        calendar.add(GregorianCalendar.DAY_OF_MONTH, 30); //set a time 5 minutes after the timestamp

        Date afterThisDate = calendar.getTime();

        currentDate.setTime(System.currentTimeMillis());
        if ((currentDate.after(afterThisDate))){


            final DatabaseReference userRef =FirebaseDatabase.getInstance().getReference(Common.UserInformation)
                    .child(currentUser.getUserId());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    HashMap<String,Object> planMap =new HashMap<>();
                    planMap.put("userPlane","ended");
                    planMap.put("SubscriptionDate",ServerValue.TIMESTAMP);
                    userRef.updateChildren(planMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("PLAN","one year Plan ended");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("PLAN ",e.getMessage());

                        }
                    });

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });


        }

    }*/

/**private void checkUserEndedSubscriptionPlan(){
//        long timeStamp =(long)Common.currentUser.getUserTimeStamp();
//
//        Date currentDate=new Date();
//        Calendar calendar=new GregorianCalendar(); //this initialises to the current system time
//        calendar.setTimeInMillis(timeStamp); //change to whatever the long timestamp value from your server is
//
//        calendar.add(GregorianCalendar.YEAR, 1); //set a time 5 minutes after the timestamp
//
//        Date afterThisDate = calendar.getTime();
//
//        currentDate.setTime(System.currentTimeMillis());
//        if ((currentDate.after(afterThisDate))){
//
//
//            final DatabaseReference userRef =FirebaseDatabase.getInstance().getReference(Common.UserInformation)
//                    .child(currentUser.getUserId());
//
//            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//
//                    HashMap<String,Object> planMap =new HashMap<>();
//                    planMap.put("userPlan","ended");
//                    planMap.put("SubscriptionDate",ServerValue.TIMESTAMP);
//                    userRef.updateChildren(planMap)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Log.d("PLAN","one year Plan ended");
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("PLAN ",e.getMessage());
//
//                        }
//                    });
//
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    throw databaseError.toException();
//                }
//            });
//
//
//        }
//
/   } **/

    private void updateCameraBearing(GoogleMap googleMap, float bearing) {
        if ( googleMap == null) return;

        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());


        CameraPosition camPos = CameraPosition
                .builder(
                        googleMap.getCameraPosition() // current Camera
                )
                .bearing(bearing)
                .zoom(14.5f)
                .target(latLng)
                .build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));

        if (lastLocation != null && lastLocation.getLongitude() != 0.0 && lastLocation.getLongitude() != 0.0) {

            if (mMap != null) {
                currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                        .flat(true)
                        .rotation(bearing));
            }

        }
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_language) {
            changeLanguageDialog();
        } else if (id == R.id.nav_update_profile) {
            showDialogUpdateInformation();
        }else if (id==R.id.menu_invite_friend){
            showInviteIntent();
        }else if (id==R.id.menu_message){
            startActivity(new Intent(HomeActivity.this,ChatActivity.class));
        }else if(id==R.id.logout){
            deleteUserLocation();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,MainActivity.class));
            finish();
            Paper.book().delete("currentUser");
        }else if (id==R.id.menu_feedback){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showFeedbackDialog();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFeedbackDialog() {
    final FlatDialog feedbackDialogue = new FlatDialog(HomeActivity.this);
        feedbackDialogue.setTitle(getString(R.string.feedback_heading))
                .setSubtitle(getString(R.string.feedback_subtitle))
                .setBackgroundColor(getColor(R.color.headings))
                .setFirstTextFieldHint(getString(R.string.feedback_hint))
                .setFirstButtonText(getString(R.string.feedback_btn_send))
                .setFirstButtonColor(getColor(R.color.followers))
                .setSecondButtonText(getString(R.string.feedback_btn_cancel))
                .setSecondButtonColor(getColor(R.color.address))
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        feedbackRef = FirebaseDatabase.getInstance().getReference(Common.feedback);
                        HashMap<String,Object> feedbackMap = new HashMap<>();

                        if (TextUtils.isEmpty(feedbackDialogue.getFirstTextField())){
                            Toast.makeText(HomeActivity.this, getString(R.string.feedback_toast), Toast.LENGTH_SHORT).show();
                        }else {
                            feedbackMap.put("feedback",feedbackDialogue.getFirstTextField());
                            feedbackRef.push().updateChildren(feedbackMap);
                        }
                        feedbackDialogue.dismiss();
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        feedbackDialogue.dismiss();
                    }
                })
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri saveUri = data.getData();
            //compression
            if (saveUri != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getResources().getString(R.string.upload_string));
                progressDialog.show();


                File thumb_filePath_uri = new File(saveUri.getPath());


                try {

                    thumb_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), saveUri);

                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(25)
                            .compressToBitmap(thumb_filePath_uri);

                }catch (IOException e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                final  byte[] thumb_byte = byteArrayOutputStream.toByteArray();

//                String imgName = UUID.randomUUID().toString();

                final StorageReference imageFolder = storageReference.child("image/"+Common.currentUser.getUserId() + ".jpg");

                UploadTask uploadTask = imageFolder.putBytes(thumb_byte);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return imageFolder.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            String thumb_downloadURL = downloadUri.toString();
                            Map<String, Object> avatarUpdate = new HashMap<>();

                            avatarUpdate.put("userAvatar", thumb_downloadURL);

                            DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference(Common.UserInformation)
                                    .child(currentUser.getUserPhone());

                            userInfoRef.updateChildren(avatarUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(HomeActivity.this, getResources().getString(R.string.uploaded_string), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, getResources().getString(R.string.uploaded_error_string), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                        } else {
                            // Handle failures
                            // ...
                            progressDialog.dismiss();
                        }
                    }
                });

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(true);
//
//        mMap.setMyLocationEnabled(true);

//        try {
//            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(HomeActivity.this, R.raw.uber_style_map));
//            if (!success) {
//                Log.e("STYLE", "Style parsing failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e("STYLE", "Can't find style. Error: ", e);
//        }

        if (fusedLocationProviderClient != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        }

        if (CameraArea!=null)
            addCircle();
    }

    @Override
    protected void onStart() {

        if (Common.currentUser!=null){

            HashMap<String,Object> activeMap = new HashMap<>();

            activeMap.put("userState","Active");

            DatabaseReference userState =FirebaseDatabase.getInstance().getReference(Common.UserInformation);
            userState.child(Common.currentUser.getUserId()).updateChildren(activeMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Error",e.getMessage());
                }
            });
        }

        super.onStart();
//        compareDate();
        if (mMap != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
//        if (currentUser.getUserPlane().equals("FreeTrial")){
//            checkUserEndedFreePlan();
//        }
//        if (currentUser.getUserPlane().equals("oneYearPlan")){
//            checkUserEndedSubscriptionPlan();
//        }
//        if (currentUser.getUserPlane().equals("ended")){
//            showRefillDialog();
//        }

        backgroundTaskSwitch.setChecked(restorePrefData());

    }

    @Override
    protected void onResume() {

        if (Common.currentUser!=null){

            HashMap<String,Object> activeMap = new HashMap<>();

            activeMap.put("userState","Active");

            DatabaseReference userState =FirebaseDatabase.getInstance().getReference(Common.UserInformation);
            userState.child(Common.currentUser.getUserId()).updateChildren(activeMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Error",e.getMessage());
                }
            });
        }

        super.onResume();
        if (mMap != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    @Override
    protected void onPause() {

        if (Common.currentUser!=null){
            HashMap<String,Object> activeMap = new HashMap<>();
            activeMap.put("userState","offline");

            DatabaseReference userState =FirebaseDatabase.getInstance().getReference(Common.UserInformation);
            userState.child(Common.currentUser.getUserId()).updateChildren(activeMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Error",e.getMessage());
                }
            });
        }

        super.onPause();
        mediaPlayer.stop();


        Log.d("LIFECYCLE","onPause");
    }

    @Override
    protected void onStop() {
//        if (!isBackgroundUpdate) {
//            if (fusedLocationProviderClient != null) {
//                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//            }
//        }

        if (backgroundTaskSwitch.isChecked()){
            startService();
        }else {
            stopService();
        }

        if (Common.currentUser!=null){

            HashMap<String,Object> activeMap = new HashMap<>();

            activeMap.put("userState","offline");

            DatabaseReference userState =FirebaseDatabase.getInstance().getReference(Common.UserInformation);
            userState.child(Common.currentUser.getUserId()).updateChildren(activeMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Error",e.getMessage());
                }
            });
        }

        super.onStop();

        Log.d("LIFECYCLE","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();

        if (isBackgroundUpdate){

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

        Log.d("LIFECYCLE","onDestroy");
    }

    //Query
    @Override
    public void onKeyEntered(String key, GeoLocation location) {

        pulseView.setVisibility(View.VISIBLE);
        if (location!=null){
            if (key.equals(Common.currentUser.getUserId())){
                Log.d("LOCATIONENTER",location.latitude+" "+location.longitude);
                Log.d("KEYENTER",key);

                if (!isWithinArea){
                    sendNotification(getResources().getString(R.string.you_string), getResources().getString(R.string.danger_string));

                }
                isWithinArea = true;

                btnAddCamera.setText(getResources().getString(R.string.leave_area));
                mediaPlayer.start();
                btnAddCamera.setBackground(getResources().getDrawable(R.drawable.btn_danger_bg));

            }

        }



    }

    @Override
    public void onKeyExited(String key) {

        pulseView.setVisibility(View.GONE);
        if (key.equals(Common.currentUser.getUserId())) {

            if (isWithinArea){
                sendNotification(getResources().getString(R.string.you_string), getResources().getString(R.string.leaved_danger_area_string));
            }

            isWithinArea = false;
            btnAddCamera.setText(getResources().getString(R.string.new_camera));
            mediaPlayer.stop();
            btnAddCamera.setBackground(getResources().getDrawable(R.drawable.button_gradiant_style));


        }

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
//        sendNotification(getResources().getString(R.string.you_string),getResources().getString(R.string.move_with_in_string));

        if (key.equals(Common.currentUser.getUserId())) {

            Log.d("LOCATIONENTER",location.latitude+" "+location.longitude);
            Log.d("KEYMOVED", key);
            btnAddCamera.setText(getResources().getString(R.string.leave_area));
            isWithinArea = true;
            btnAddCamera.setText(getResources().getString(R.string.leave_area));
//            mediaPlayer.start();
            btnAddCamera.setBackground(getResources().getDrawable(R.drawable.btn_danger_bg));

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

    // interface
    @Override
    public void OnLoadLocationSuccess(List<Camera> latLngs) {
        cameraAreaList = new ArrayList<>();

        for (Camera myLatLng : latLngs) {
            LatLng convert = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
            cameraAreaList.add(convert);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(HomeActivity.this);
        }

        if (mMap != null) {
            mMap.clear();
            addUserMarker();
            addCircle();
        }



    }

    @Override
    public void OnLoadLocationFailed(String msg) {
        Toast.makeText(this, msg
                , Toast.LENGTH_SHORT).show();
    }

    //AD MOB
    @Override
    public void onRewardedVideoAdLoaded() {
      //  Toast.makeText(this, "ad load", Toast.LENGTH_SHORT).show();
        if (mRewardedVideoAd.isLoaded()){
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
     //   Toast.makeText(this, "ad opend", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoStarted() {
      //  Toast.makeText(this, "ad start", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoAdClosed() {
       // Toast.makeText(this, "ad closed", Toast.LENGTH_SHORT).show();
        startTime += 180000;
        btnAddCamera.setText(getString(R.string.new_camera));

        startTimer();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
      //  Toast.makeText(this, "ad left", Toast.LENGTH_SHORT).show();
        startTime += 180000;
        btnAddCamera.setText(getString(R.string.new_camera));

        startTimer();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
      //  Toast.makeText(this, "ad", Toast.LENGTH_SHORT).show();
        startTime += 60000;
        btnAddCamera.setText(getString(R.string.new_camera));

        btnAddCamera.setText(getString(R.string.new_camera));
        startTimer();
    }

    @Override
    public void onRewardedVideoCompleted() {
        //Toast.makeText(this, "ad complited", Toast.LENGTH_SHORT).show();


//
//        final DatabaseReference userRef= FirebaseDatabase.getInstance().getReference(Common.UserInformation)
//                .child(Common.currentUser.getUserId());
//        final HashMap<String,Object> pointMap = new HashMap<>();
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//
//
//                    String pointStr=Common.currentUser.getUserPoint();
//                    int point;
//                    int updatedPoint;
//                    String updatedPointStr;
//
//
//                    pointStr =(String) dataSnapshot.child("userPoint").getValue();
//                    point=Integer.parseInt(pointStr);
//                    Log.d("POINT",pointStr);
//                    updatedPoint =point+25;
//                    updatedPointStr =String.valueOf(updatedPoint);
//
//
//                    pointMap.put("userPoint",updatedPointStr);
//                    userRef.updateChildren(pointMap);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        btnAddCamera.setText(getString(R.string.new_camera));

        startTime += 180000; //second ad comes after first ad + 3min
        startTimer();
    }

}