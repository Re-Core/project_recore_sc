package com.recore.projectrecoresc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class TutorialActivity extends AppCompatActivity {

    private ImageView btnCloseTutorial;
    private VideoView videTutorial;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (restorePrefData()) {
            Intent i = new Intent(TutorialActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        setContentView(R.layout.activity_tutorial);



        Log.d("introCondition", String.valueOf(restorePrefData()));

        btnCloseTutorial =(ImageView)findViewById(R.id.btn_close);
        videTutorial = (VideoView)findViewById(R.id.videoTutorial);

        btnCloseTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TutorialActivity.this,MainActivity.class);
                startActivity(intent);
                savePref();
                Log.d("introCondition", String.valueOf(restorePrefData()));
                finish();
            }
        });
        playVideo();
    }
    private void playVideo(){
        videoPath = "android.resource://"+getPackageName()+"/"+R.raw.tutorial;
        Uri uri = Uri.parse(videoPath);
        videTutorial.setVideoURI(uri);

        MediaController mediaController =  new MediaController(this);
        videTutorial.setMediaController(mediaController);
        mediaController.setAnchorView(videTutorial);

        videTutorial.start();

        videTutorial.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Intent intent = new Intent(TutorialActivity.this,MainActivity.class);
                startActivity(intent);
                savePref();
                Log.d("introCondition", String.valueOf(restorePrefData()));
                finish();
            }
        });
    }

    private void savePref() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("tutorialViewed", true);
        editor.commit();

    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
        Boolean isIntroActivityOpendBefore = pref.getBoolean("tutorialViewed", false);
        return isIntroActivityOpendBefore;
    }



}
