package com.recore.projectrecoresc.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.recore.projectrecoresc.Model.TimelineItem;
import com.recore.projectrecoresc.R;


public class HeaderTextViewHolder extends BaseViewHolder {
    private TextView tvHeader;


    public HeaderTextViewHolder(@NonNull View itemView) {
        super(itemView);
        tvHeader =itemView.findViewById(R.id.header_text);
    }

    @Override
    public void setData(TimelineItem item) {
//        HeaderTextItem headerTextItem =item.getHeaderTextItem();
//        tvHeader.setText(headerTextItem.getHeaderText());
//
    }
}
