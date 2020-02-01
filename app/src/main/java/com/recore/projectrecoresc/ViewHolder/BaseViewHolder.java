package com.recore.projectrecoresc.ViewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.recore.projectrecoresc.Model.TimelineItem;


public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public abstract void setData(TimelineItem item);

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

}
