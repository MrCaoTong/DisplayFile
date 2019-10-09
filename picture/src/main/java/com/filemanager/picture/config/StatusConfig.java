package com.filemanager.picture.config;

import android.os.Environment;

import java.io.File;

/**
 * 白板相关配置信息
 */
public class StatusConfig {

    /**
     * 课件目录根位置
     */
    public static String EDUCATION_DIR_BASE = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "PartyLauncher";

    /**
     * 课件文件名后缀
     */
    public static final String COURSE_WARE_FILE_SUFFIX = ".jboard";

    /**
     * 返回选择的文件现对应的广播的 Action
     */
    public static final String RESOURCE_FILE_PATH_ACTION = "RESOURCE_FILE_PATH";
    /**
     * 插入文件对应的广播 Action
     */
    public static final String RESOURCE_INSERT_FILE_ACTION = "RESOURCE_INSERT_FILE";
    /**
     * 图片文件夹的ACTION
     */
    public static final String RESOURCE_PICTURE_FOLDER_ACTION = "resource_picture_folder";
    /**
     * 文件路径的 KEY
     */
    public static final String RESOURCE_FILE_PATH_KEY = "resource_file_path";
    /**
     * 文件夹路径的KEY
     */
    public static final String RESOURCE_FOLDER_PATH_KEY = "resource_folder_path";

    public static final String RESOURCE_INSERT_FILE_KEY = "resource_insert_file";
}
