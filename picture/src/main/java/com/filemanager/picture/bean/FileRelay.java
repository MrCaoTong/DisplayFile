package com.filemanager.picture.bean;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.filemanager.picture.activity.ResourceLibraryActivity;
import com.filemanager.picture.interfaces.OnFilePathListener;

public class FileRelay {

    private static OnFilePathListener mListener;

    /**
     * @param activity
     * @param type     all or null 全部显示
     *                 directory 只显示目录文件
     *                 picture 只显示图片
     *                 video 只显示音视频
     *                 wps  只显示该类型文件
     * @param listener
     */
    public static void startActivtiy(Activity activity, String type, OnFilePathListener listener) {
        Intent intent = new Intent(activity, ResourceLibraryActivity.class);
        mListener = listener;
        if (TextUtils.isEmpty(type) || type.equals("all")) {
            intent.putExtra("resource_type", 0);
        } else if (type.equals("directory")) {
            intent.putExtra("resource_type", 1);
        } else if (type.equals("picture")) {
            intent.putExtra("resource_type", 2);
        } else if (type.equals("video")) {
            intent.putExtra("resource_type", 3);
        } else if (type.equals("wps")) {
            intent.putExtra("resource_type", 8);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void setFilePath(String path) {
        mListener.onFilePath(path);
    }
}
