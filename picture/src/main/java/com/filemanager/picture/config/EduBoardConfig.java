package com.filemanager.picture.config;

import android.os.Environment;

import java.io.File;

/**
 * 白板相关配置信息
 */
public class EduBoardConfig {

    /**
     * 课件目录根位置
     */
    public static String EDUCATION_DIR_BASE = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "PartyLauncher";

    /**
     * 课件文件名后缀
     */
    public static final String COURSE_WARE_FILE_SUFFIX = ".jboard";
}
