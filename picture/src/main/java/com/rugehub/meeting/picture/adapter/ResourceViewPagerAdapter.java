package com.rugehub.meeting.picture.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.common.collect.Sets;
import com.rugehub.meeting.picture.R;
import com.rugehub.meeting.picture.activity.ResourceLibraryActivity;
import com.rugehub.meeting.picture.file.FileUtils;
import com.rugehub.meeting.picture.interfaces.OnItemClickListener;
import com.rugehub.meeting.picture.model.FileMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResourceViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private FileMode mode;
    private int typeResource;
    private boolean isFileNull = false;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    public PictureAdapter pictureAdapter;
    private MediaAdapter mediaAdapter;
    private OnPagerItemClickListener mListener;

    private List<String> mTitles = new ArrayList<>();
    private List<File> mFiles = new ArrayList<>();
    private Set<File> mPictures = Sets.newConcurrentHashSet();
    private Set<File> mMedias = Sets.newConcurrentHashSet();

    private OnItemClickListener mItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(final FileMode fileMode, final String path) {
            if (fileMode == FileMode.FILE)
                mFiles.clear();
            if (fileMode == FileMode.PICTURE)
                mPictures.clear();
            if (fileMode == FileMode.MEDIA)
                mMedias.clear();
            if (new File(path).isDirectory()) {
                FileUtils.getFiles(fileMode, typeResource, path, new FileUtils.GetModeCallBack() {
                    @Override
                    public void onFileNull(boolean isNull) {
                        mode = fileMode;
                        isFileNull = isNull;
                    }
                }, new FileUtils.OnFileFound() {
                    @Override
                    public void onFileFound(File file) {
                        if (fileMode == FileMode.FILE) {
                            mFiles.add(file);
                        } else if (fileMode == FileMode.PICTURE || fileMode == FileMode.PICTURE_FILE) {
                            mPictures.add(file);
                        } else if (fileMode == FileMode.MEDIA) {
                            mMedias.add(file);
                        }
                        mode = fileMode;
                    }
                });
                mListener.onPagerItemClick(path, mode, isFileNull);
            } else {
                if (typeResource > 1) {
                    //获取文件路径
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(
                            new Intent(ResourceLibraryActivity.RESOURCE_FILE_PATH_ACTION)
                                    .putExtra(ResourceLibraryActivity.RESOURCE_FILE_PATH_KEY, path));
                } else if (typeResource == 0) {
                    //插入白板
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(
                            new Intent(ResourceLibraryActivity.RESOURCE_INSERT_FILE_ACTION)
                                    .putExtra(ResourceLibraryActivity.RESOURCE_INSERT_FILE_KEY, path));
                }
                mListener.onResourceFinish();
            }
        }
    };

    public ResourceViewPagerAdapter(Context context, int type, List<String> titles, List<File> files) {
        this.mContext = context;
        this.typeResource = type;
        mTitles.addAll(titles);
        if (type == 0
                || type == 1
                || type == 4
                || type == 5
                || type == 6
                || type == 7
                || type == 8) {
            this.mFiles.addAll(files);
        } else if (type == 2 || type == 9) {
            this.mPictures.addAll(files);
        } else if (type == 3) {
            this.mMedias.addAll(files);
        }
    }

    public void setOnPagerItemClickListener(OnPagerItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnPagerItemClickListener {
        void onResourceFinish();

        void onPagerTextNull(boolean isFileNull);

        void onPagerItemClick(String path, FileMode fileMode, boolean isFileNull);
    }

//    public void setInitLists(List<File> pictures, List<File> medias) {
//        mPictures.clear();
//        mMedias.clear();
//
//        this.mPictures.addAll(pictures);
//        this.mMedias.addAll(medias);
//    }

    public void refreshPagerData(FileMode fileMode) {
        if (fileMode == FileMode.FILE && fileAdapter != null) {
            fileAdapter.refreshData(mFiles, fileMode);
        } else if ((fileMode == FileMode.PICTURE || fileMode == FileMode.PICTURE_FILE) && pictureAdapter != null) {
            pictureAdapter.refreshData(mPictures, fileMode);
        } else if (fileMode == FileMode.MEDIA && mediaAdapter != null) {
            mediaAdapter.refreshData(mMedias, fileMode);
        }
    }

    public void refreshPagerData(List<File> files, FileMode fileMode) {
        if (files != null && files.size() != 0) {
            if (fileMode == FileMode.FILE) {
                mFiles.clear();
                mFiles.addAll(files);
            } else if ((fileMode == FileMode.PICTURE || fileMode == FileMode.PICTURE_FILE) && pictureAdapter != null) {
                mPictures.clear();
                mPictures.addAll(files);
            } else if (fileMode == FileMode.MEDIA) {
                mMedias.clear();
                mMedias.addAll(files);
            }
            mListener.onPagerItemClick("", fileMode, false);
        } else {
            mListener.onPagerItemClick("", fileMode, true);
            if (fileMode == FileMode.FILE && fileAdapter != null) {
                fileAdapter.removeAll();
            } else if ((fileMode == FileMode.PICTURE || fileMode == FileMode.PICTURE_FILE) && pictureAdapter != null) {
                pictureAdapter.removeAll();
            } else if (fileMode == FileMode.MEDIA && mediaAdapter != null) {
                mediaAdapter.removeAll();
            }
        }
        mode = fileMode;
    }

    /**
     * 该刷新 只实时更新搜索数据，不做任何资源库数据更改
     *
     * @param files
     * @param fileMode
     */
    public void refreshData(List<File> files, FileMode fileMode) {
        if (files != null && files.size() != 0) {
            if (fileMode == FileMode.FILE) {
                fileAdapter.refreshData(files, fileMode);
            } else if (fileMode == FileMode.PICTURE || fileMode == FileMode.PICTURE_FILE) {
                pictureAdapter.refreshData(files, fileMode);
            } else if (fileMode == FileMode.MEDIA) {
                mediaAdapter.refreshData(files, fileMode);
            }
            mListener.onPagerTextNull(false);
        } else {
            if (fileMode == FileMode.FILE) {
                fileAdapter.removeAll();
            } else if (fileMode == FileMode.PICTURE || fileMode == FileMode.PICTURE_FILE) {
                pictureAdapter.removeAll();
            } else if (fileMode == FileMode.MEDIA) {
                mediaAdapter.removeAll();
            }
            mListener.onPagerTextNull(true);
        }
    }

    public void clear() {
        if (mFiles.size() != 0 && mFiles != null)
            mFiles.clear();
        if (mPictures != null && mPictures.size() != 0)
            mPictures.clear();
        if (mMedias != null && mMedias.size() != 0)
            mMedias.clear();
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.resource_pager_item, null);
        recyclerView = layout.findViewById(R.id.resource_pager_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        switch (position) {
            case 0:
                mode = FileMode.FILE;
                fileAdapter = new FileAdapter(mContext, mFiles, mode);
                recyclerView.setAdapter(fileAdapter);
                fileAdapter.setOnItemClickListener(mItemListener);
                break;
            case 1:
                mode = FileMode.PICTURE;
                pictureAdapter = new PictureAdapter(mContext, mPictures, mode, typeResource);
                recyclerView.setAdapter(pictureAdapter);
                pictureAdapter.setOnItemClickListener(mItemListener);
                break;
            case 2:
                mode = FileMode.MEDIA;
                mediaAdapter = new MediaAdapter(mContext, mMedias, mode);
                recyclerView.setAdapter(mediaAdapter);
                mediaAdapter.setOnItemClickListener(mItemListener);
                break;
        }
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
