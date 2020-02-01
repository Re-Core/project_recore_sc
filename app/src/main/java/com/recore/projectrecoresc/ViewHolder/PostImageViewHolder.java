package com.recore.projectrecoresc.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.bumptech.glide.Glide;
import com.recore.projectrecoresc.Common.Common;
import com.recore.projectrecoresc.Model.PostImageItem;
import com.recore.projectrecoresc.Model.TimelineItem;
import com.recore.projectrecoresc.R;

public class PostImageViewHolder extends BaseViewHolder {

    private ImageView contentImg;
    private TextView txtTime;
    public PostImageViewHolder(@NonNull View itemView) {
        super(itemView);
        contentImg =itemView.findViewById(R.id.post_image_placeholder);
        txtTime=itemView.findViewById(R.id.post_image_time);
    }

    @Override
    public void setData(TimelineItem item) {
        PostImageItem postImageItem =item.getPostImageItem();

        long time  =(long)postImageItem.getTimeStamp();
        String timeStamp = Common.timeStampToString(time);
        txtTime.setText(timeStamp);

        Glide.with(itemView.getContext()).load(postImageItem.getImgURL())
                .into(contentImg);

    }
}
