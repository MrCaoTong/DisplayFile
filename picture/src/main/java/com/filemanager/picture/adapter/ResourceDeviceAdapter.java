package com.filemanager.picture.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.filemanager.picture.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备列表
 */
public class ResourceDeviceAdapter extends RecyclerView.Adapter<ResourceDeviceAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mNames;
    private int resource_type;
    private OnDevicesItemClickListener mListener;
    private List<TextView> mTextViews = new ArrayList<>();

    public ResourceDeviceAdapter(Context context, List<String> mNames, int resource_type) {
        this.mContext = context;
        this.mNames = mNames;
        this.resource_type = resource_type;
    }

    public void setOnDevicesItemClickListener(OnDevicesItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnDevicesItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.resource_devices_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(mNames.get(position));
        if (resource_type == 0 || resource_type == 1
                || resource_type == 4
                || resource_type == 5
                || resource_type == 6
                || resource_type == 7
                || resource_type == 8) {
            //TODO position
//            if (position == 1) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.subject_indicator));
//                holder.name.setTextSize(15);
//            }
            if (position == 0) {
                holder.name.setTextColor(mContext.getResources().getColor(R.color.subject_indicator));
                holder.name.setTextSize(15);
            }
        } else if (resource_type == 2 || resource_type == 3 || resource_type == 9) {
            if (position == 0) {
                holder.name.setTextColor(mContext.getResources().getColor(R.color.subject_indicator));
                holder.name.setTextSize(15);
            }
        }
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(position);
            }
        });
        mTextViews.add(holder.name);
    }

    public void refreshData() {
        notifyItemRemoved(0);
//        mNames.clear();
//        this.mNames.addAll(names);
//        notifyDataSetChanged();
    }

    public void setSelectText(int position) {
        for (int i = 0; i < mNames.size(); i++) {
            mTextViews.get(i).setTextColor(mContext.getResources().getColor(R.color.resource_device));
            mTextViews.get(i).setTextSize(13);
        }
        mTextViews.get(position).setTextColor(mContext.getResources().getColor(R.color.subject_indicator));
        mTextViews.get(position).setTextSize(15);
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.resource_devices_name);
        }
    }
}
