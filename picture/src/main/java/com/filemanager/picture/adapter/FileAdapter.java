package com.filemanager.picture.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.filemanager.picture.GlideApp;
import com.filemanager.picture.R;
import com.filemanager.picture.config.StatusConfig;
import com.filemanager.picture.file.FileUtils;
import com.filemanager.picture.interfaces.OnItemClickListener;
import com.filemanager.picture.model.FileMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context mContext;
    private List<File> mFiles = new ArrayList<>();
    private FileMode fileMode;

    private ViewHolder viewHolder;
    private OnItemClickListener mListener;

    public FileAdapter(Context context, List<File> mFiles, FileMode fileMode) {
        this.mContext = context;
        this.mFiles.addAll(mFiles);
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
        final File file = mFiles.get(position);
        String name = file.getName();
        long date = file.lastModified();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = df.format(new Date(date));
        holder.resource_adapter_date.setText(dateTime);
        holder.resource_adapter_name.setText(name);
        if (file.exists() && file.isDirectory()) {
            holder.resource_icon_imageView.setVisibility(View.GONE);
            holder.resource_adapter_imageView.setVisibility(View.VISIBLE);
            holder.resource_adapter_imageView.setImageResource(R.mipmap.ic_resource_folder);
        } else {
            String postFix = name.substring(name.lastIndexOf(".")).toLowerCase();
            if (FileUtils.isImageFile(file) || FileUtils.isVideoFile(file)) {
                holder.resource_adapter_imageView.setVisibility(View.GONE);
                holder.resource_icon_imageView.setVisibility(View.VISIBLE);
                GlideApp.with(mContext)
                        .load(file.getPath())
                        .error(R.mipmap.ic_resource_error)
                        .into(holder.resource_icon_imageView);
            } else if (FileUtils.isAudioFile(file)) {
                holder.resource_adapter_imageView.setVisibility(View.VISIBLE);
                holder.resource_icon_imageView.setVisibility(View.GONE);
                holder.resource_adapter_imageView.setImageResource(R.mipmap.ic_resource_audio);
            } else if (postFix.equals(".pdf")) {
                holder.resource_adapter_imageView.setVisibility(View.VISIBLE);
                holder.resource_icon_imageView.setVisibility(View.GONE);
                holder.resource_adapter_imageView.setImageResource(R.mipmap.ic_resource_pdf);
            } else if (postFix.equals(".doc") || postFix.equals(".docx")) {
                holder.resource_adapter_imageView.setVisibility(View.VISIBLE);
                holder.resource_icon_imageView.setVisibility(View.GONE);
                holder.resource_adapter_imageView.setImageResource(R.mipmap.ic_resource_word);
            } else if (postFix.equals(".xlsx") || postFix.equals(".xls")) {
                holder.resource_adapter_imageView.setVisibility(View.VISIBLE);
                holder.resource_icon_imageView.setVisibility(View.GONE);
                holder.resource_adapter_imageView.setImageResource(R.mipmap.ic_resource_excel);
            } else if (postFix.equals(".pptx") || postFix.equals(".ppt")) {
                holder.resource_adapter_imageView.setVisibility(View.VISIBLE);
                holder.resource_icon_imageView.setVisibility(View.GONE);
                holder.resource_adapter_imageView.setImageResource(R.mipmap.ic_resource_ppt);
            } else if (postFix.equals(StatusConfig.COURSE_WARE_FILE_SUFFIX)) {
                holder.resource_adapter_imageView.setVisibility(View.VISIBLE);
                holder.resource_icon_imageView.setVisibility(View.GONE);
                holder.resource_adapter_imageView.setImageResource(R.mipmap.ic_browser_icon);
            }
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
        return mFiles.size() == 0 ? 0 : mFiles.size();
    }

    public void refreshData(List<File> files, FileMode fileMode) {
        mFiles.clear();
        this.fileMode = fileMode;
        this.mFiles.addAll(files);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mFiles.clear();
        notifyDataSetChanged();
    }
}
