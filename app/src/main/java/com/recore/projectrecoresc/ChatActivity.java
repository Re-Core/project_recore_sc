package com.recore.projectrecoresc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;

import com.recore.projectrecoresc.Adapter.TimelineAdapter;
import com.recore.projectrecoresc.Common.Common;
import com.recore.projectrecoresc.Model.PostImageItem;
import com.recore.projectrecoresc.Model.PostTextItem;
import com.recore.projectrecoresc.Model.PostVideoItem;
import com.recore.projectrecoresc.Model.TimelineItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity{

    private TimelineAdapter adapter;
    private RecyclerView timelineRv;
    private List<TimelineItem> timelineItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        timelineRv =(RecyclerView)findViewById(R.id.timelineRv);
        LinearLayoutManager lin = new LinearLayoutManager(this);
        timelineRv.setLayoutManager(lin);


    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference timelineRef = FirebaseDatabase.getInstance().getReference(Common.CHAT);
        timelineItemList =new ArrayList<>();

        timelineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                timelineItemList.clear();
                for (DataSnapshot timelineShot:dataSnapshot.getChildren()){

                    if (timelineShot.child("contentType").getValue().equals("Video")){

                        PostVideoItem postVideoItem = timelineShot.getValue(PostVideoItem.class);
                        TimelineItem timelineItem=new TimelineItem(postVideoItem);
                        timelineItemList.add(timelineItem);
                    }
                    if (timelineShot.child("contentType").getValue().equals("Photo")){
                        PostImageItem postTextItem = timelineShot.getValue(PostImageItem.class);
                        TimelineItem timelineItem=new TimelineItem(postTextItem);
                        timelineItemList.add(timelineItem);

                    }
                    if (timelineShot.child("contentType").getValue().equals("Text")){
                        PostTextItem postTextItem = timelineShot.getValue(PostTextItem.class);
                        TimelineItem timelineItem=new TimelineItem(postTextItem);
                        timelineItemList.add(timelineItem);
                    }

                }


                adapter =new TimelineAdapter(ChatActivity.this,timelineItemList);
                timelineRv.setAdapter(adapter);
                timelineRv.scrollToPosition(timelineItemList.size() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}