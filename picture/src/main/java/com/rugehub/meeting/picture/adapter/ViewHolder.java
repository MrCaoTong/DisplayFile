package com.rugehub.meeting.picture.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rugehub.meeting.picture.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout resource_adapter_layout;
    public ImageView resource_adapter_imageView;
    public ImageView resource_icon_imageView;
    public ImageView resource_check_imageView;
    public TextView resource_adapter_name;
    public TextView resource_adapter_date;

    public ViewHolder(View itemView) {
        super(itemView);
        resource_adapter_layout = itemView.findViewById(R.id.resource_adapter_layout);
        resource_adapter_imageView = itemView.findViewById(R.id.resource_adapter_imageView);
        resource_icon_imageView = itemView.findViewById(R.id.resource_icon_imageView);
        resource_check_imageView = itemView.findViewById(R.id.check_icon_grid);
        resource_adapter_name = itemView.findViewById(R.id.resource_adapter_name);
        resource_adapter_date = itemView.findViewById(R.id.resource_adapter_date);
    }
}
