package com.filemanager.picture.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.filemanager.picture.GlideApp;
import com.filemanager.picture.R;
import com.filemanager.picture.adapter.ViewHolder;
import com.filemanager.picture.file.FileUtils;
import com.filemanager.picture.interfaces.OnItemClickListener;
import com.filemanager.picture.model.FileMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MediaAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context mContext;
    private List<File> mMedias = new ArrayList<>();
    private FileMode fileMode;

    private ViewHolder viewHolder;
    private OnItemClickListener mListener;

    public MediaAdapter(Context context, Set<File> mFiles, FileMode fileMode) {
        this.mContext = context;
        this.mMedias.addAll(mFiles);
        this.fileMode = fileMode;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.resource_adapter_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final File file = mMedias.get(position);
        String name = file.getName();
        long date = file.lastModified();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = df.format(new Date(date));
        holder.resource_adapter_date.setText(dateTime);
        holder.resource_adapter_name.setText(name);
        holder.resource_adapter_name.setSelected(true);
        if (FileUtils.isAudioFile(file)) {
            holder.resource_adapter_imageView.setVisibility(View.VISIBLE);
            holder.resource_icon_imageView.setVisibility(View.GONE);
            GlideApp.with(mContext)
                    .load(file.getPath())
                    .error(R.mipmap.ic_resource_audio)
                    .into(holder.resource_adapter_imageView);
        } else if (FileUtils.isVideoFile(file)) {
            holder.resource_adapter_imageView.setVisibility(View.GONE);
            holder.resource_icon_imageView.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .load(file.getPath())
                    .error(R.mipmap.ic_resource_video)
                    .into(holder.resource_icon_imageView);
        }

        holder.resource_adapter_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(fileMode, file.getPath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMedias.size() == 0 ? 0 : mMedias.size();
    }

    public void refreshData(List<File> medias, FileMode fileMode) {
        mMedias.clear();
        this.fileMode = fileMode;
        this.mMedias.addAll(medias);
        notifyDataSetChanged();
    }

    public void refreshData(Set<File> medias, FileMode fileMode) {
        mMedias.clear();
        this.fileMode = fileMode;
        this.mMedias.addAll(medias);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mMedias.clear();
        notifyDataSetChanged();
    }
}
