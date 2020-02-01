package com.recore.projectrecoresc.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.recore.projectrecoresc.Common.Common;
import com.recore.projectrecoresc.Model.PostTextItem;
import com.recore.projectrecoresc.Model.TimelineItem;
import com.recore.projectrecoresc.R;

public class PostTextViewHolder extends BaseViewHolder  {

    private TextView txtPost, txtTime;
    private ImageView imgUser;

    public PostTextViewHolder(@NonNull View itemView) {
        super(itemView);
        txtPost =itemView.findViewById(R.id.post_text_content);
        txtTime =itemView.findViewById(R.id.post_text_time);
        imgUser =itemView.findViewById(R.id.post_text_img);

    }

    @Override
    public void setData(TimelineItem item) {
        PostTextItem postTextItem = item.getPostTextItem();
        txtPost.setText(postTextItem.getPostText());

        long time  =(long)postTextItem.getTimeStamp();
        String timeStamp = Common.timeStampToString(time);
        txtTime.setText(timeStamp);

        Glide.with(itemView.getContext()).load(postTextItem.getImgUser()).into(imgUser);



    }

}
