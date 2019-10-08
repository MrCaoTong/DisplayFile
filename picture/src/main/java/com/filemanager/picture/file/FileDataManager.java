package com.filemanager.picture.file;

import com.google.common.collect.Sets;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileDataManager {
    private static FileDataManager instance = new FileDataManager();

    public Set<String> mFilePath = Sets.newConcurrentHashSet();//所有路径
    public Set<String> mImageFolderPath = Sets.newConcurrentHashSet();//图片文件夹路径
    public Set<String> mImageFilePath = Sets.newConcurrentHashSet();//图片文件路径
    public Set<String> mMediaFilesPath = Sets.newConcurrentHashSet();//音视频文件路径
    public Map<Integer, List<String>> picturePaths = new HashMap<>();

    public static FileDataManager getInstance() {
        return instance;
    }

    public void addPath(String path,int type) {
        File file = new File(path);
        if (file.isDirectory()) {
            mFilePath.add(path);
        } else if (FileUtils.checkStringPostFix(file, type)) {
            mFilePath.add(path);
        }
    }

    public void addPicturePath(String path) {
        if (path.toLowerCase().endsWith(".png") || path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".bmp")) {
            mImageFilePath.add(path);
            String imageName = new File(path).getName();
            String paths = path.replace(File.separator + imageName, "");
            mImageFolderPath.add(paths);
        }
    }

    public void addMediaPath(String path) {
        if (path.toLowerCase().endsWith(".mp3") || path.toLowerCase().endsWith(".mp4")
                || path.toLowerCase().endsWith(".rmvb") || path.toLowerCase().endsWith(".mov")) {
            mMediaFilesPath.add(path);
        }
    }

    public void setPicturePaths(Map<Integer, List<String>> picturePaths) {
        this.picturePaths = picturePaths;
    }

    public void clean() {
        mFilePath.clear();
        mImageFolderPath.clear();
        mImageFilePath.clear();
        mMediaFilesPath.clear();
    }
}
