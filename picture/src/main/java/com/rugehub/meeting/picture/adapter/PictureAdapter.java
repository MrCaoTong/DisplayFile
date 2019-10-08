package com.rugehub.meeting.picture.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.rugehub.meeting.picture.GlideApp;
import com.rugehub.meeting.picture.R;
import com.rugehub.meeting.picture.file.FileUtils;
import com.rugehub.meeting.picture.interfaces.OnItemClickListener;
import com.rugehub.meeting.picture.model.FileMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PictureAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context mContext;
    private int type;
    private FileMode fileMode;
    private List<File> mPictures = new ArrayList<>();

    private ViewHolder viewHolder;
    private OnItemClickListener mListener;

    public PictureAdapter(Context context, Set<File> mPictures, FileMode fileMode, int typeResource) {
        this.mContext = context;
        this.mPictures.addAll(mPictures);
        this.fileMode = fileMode;
        this.type = typeResource;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.resource_adapter_item, parent, false));
        return viewHolder;
    }

    int index = 1;
    Map<Integer, List<String>> picturePath = new HashMap<>();

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.resource_check_imageView.setColorFilter(mContext.getResources().getColor(R.color.subject_indicator));
        final File file = mPictures.get(position);
        String name = file.getName();
        long date = file.lastModified();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = df.format(new Date(date));
        holder.resource_adapter_date.setText(dateTime);
        holder.resource_adapter_name.setText(name);
        holder.resource_adapter_name.setSelected(true);
        holder.resource_icon_imageView.setVisibility(View.VISIBLE);
        holder.resource_adapter_imageView.setVisibility(View.GONE);
        final List<String> mPaths = new ArrayList<>();

        if (file.isDirectory()) {
            FileUtils.getFiles(fileMode, 2, file.getPath(), null, new FileUtils.OnFileFound() {
                @Override
                public void onFileFound(File file) {
                    mPaths.add(file.getPath());
                }
            });
            GlideApp.with(mContext).load(mPaths.get(0)).into(holder.resource_icon_imageView);
        } else {
            GlideApp.with(mContext)
                    .load(file.getPath())
                    .into(holder.resource_icon_imageView);
        }
        holder.resource_adapter_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 2) {
                    mListener.onItemClick(fileMode, file.getPath());
                } else if (type == 9) {
                    if (mPaths.size() > 8) {
                        Toast.makeText(mContext, "一个文件夹下面最多支持导入8张图片", Toast.LENGTH_SHORT).show();
                    } else {
                        if (holder.resource_check_imageView.getVisibility() == View.GONE) {
                            if (index > 5) {
                                Toast.makeText(mContext, "最多只能导入5个文件夹栏目", Toast.LENGTH_SHORT).show();
                            } else {
                                picturePath.put(index, mPaths);
                                holder.resource_check_imageView.setVisibility(View.VISIBLE);
                                index++;
                            }
                        } else {
                            index--;
                            picturePath.remove(index);
                            holder.resource_check_imageView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPictures.size() == 0 ? 0 : mPictures.size();
    }

    public void refreshData(List<File> pictures, FileMode fileMode) {
        mPictures.clear();
        this.fileMode = fileMode;
        this.mPictures.addAll(pictures);
        notifyDataSetChanged();
    }

    public void refreshData(Set<File> pictures, FileMode fileMode) {
        mPictures.clear();
        this.fileMode = fileMode;
        this.mPictures.addAll(pictures);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mPictures.clear();
        notifyDataSetChanged();
    }

    public Map<Integer, List<String>> getPicturePath() {
        return picturePath;
    }
}