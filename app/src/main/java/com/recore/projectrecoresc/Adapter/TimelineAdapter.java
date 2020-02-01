package com.recore.projectrecoresc.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.recore.projectrecoresc.Common.Constant;
import com.recore.projectrecoresc.Model.TimelineItem;
import com.recore.projectrecoresc.R;
import com.recore.projectrecoresc.ViewHolder.BaseViewHolder;
import com.recore.projectrecoresc.ViewHolder.HeaderTextViewHolder;
import com.recore.projectrecoresc.ViewHolder.PostImageViewHolder;
import com.recore.projectrecoresc.ViewHolder.PostTextViewHolder;
import com.recore.projectrecoresc.ViewHolder.PostVideoViewHolder;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context mContext;
    private List<TimelineItem> mData;

    public TimelineAdapter(Context mContext, List<TimelineItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType){

            case (Constant.ITEM_HEADER_TEXT_VIEWTYPE):
                view = LayoutInflater.from(mContext).inflate(R.layout.item_header,parent,false);
                return new HeaderTextViewHolder(view);

            case (Constant.ITEM_HEADER_POST_TEXT_VIEWTYPE):
                view = LayoutInflater.from(mContext).inflate(R.layout.item_post_text,parent,false);
                return new PostTextViewHolder(view);

            case (Constant.ITEM_HEADER_POST_VIDEO_VIEWTYPE):
                view = LayoutInflater.from(mContext).inflate(R.layout.item_post_video,parent,false);
                return new PostVideoViewHolder(view);

            case (Constant.ITEM_HEADER_POST_IMAGE_VIEWTYPE):
                view = LayoutInflater.from(mContext).inflate(R.layout.item_post_image,parent,false);
                return new PostImageViewHolder(view);

                default: throw new IllegalArgumentException();
        }

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.setData(mData.get(position));
    }


    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        if (mData!=null){
            return mData.size();
        }else {
            return 0;
        }

    }
}
