package com.recore.projectrecoresc.ViewHolder;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;


import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.recore.projectrecoresc.Common.Common;
import com.recore.projectrecoresc.Model.PostVideoItem;
import com.recore.projectrecoresc.Model.TimelineItem;
import com.recore.projectrecoresc.R;


public class PostVideoViewHolder extends BaseViewHolder {

    private TextView txtTime;
    private ImageView imgUser;
    private VideoView videoView;
    private FloatingActionButton btnPlay;
    public PostVideoViewHolder(@NonNull View itemView) {
        super(itemView);
        txtTime =itemView.findViewById(R.id.post_video_time);
        imgUser =itemView.findViewById(R.id.post_video_img);
        videoView=(VideoView)itemView.findViewById(R.id.video_view);
        btnPlay =itemView.findViewById(R.id.post_video_play_btn);
    }

    @Override
    public void setData(TimelineItem item) {
        PostVideoItem postVideoItem =item.getPostVideoItem();

        long time  =(long)postVideoItem.getTimeStamp();
        String timeStamp = Common.timeStampToString(time);
        txtTime.setText(timeStamp);

        final String path=postVideoItem.getVideoURL();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.setVisibility(View.VISIBLE);
                btnPlay.setVisibility(View.GONE);
                Uri uri=Uri.parse(path);
                videoView.setVideoURI(uri);
                videoView.start();
            }
        });
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (videoView.isPlaying()){
                    videoView.stopPlayback();
                    btnPlay.setVisibility(View.VISIBLE);
                }

            }
        });
        if (!videoView.isPlaying()){
            btnPlay.setVisibility(View.VISIBLE);
        }


        Glide.with(itemView.getContext()).load(postVideoItem.getUserImg())
                .into(imgUser);

    }
}
