package com.filemanager.picture.file;

import android.content.Context;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;


import com.filemanager.picture.config.StatusConfig;
import com.filemanager.picture.model.FileMode;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 文件工具类
 */
public class FileUtils {

    private static String[] postFixs;

    /**
     * 通过反射得到所有设备路径
     *
     * @param context
     * @return
     */
    public static List<String> getVolumePaths(Context context) {
        StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<String> mPaths = new ArrayList<>();
        try {
            Method method = StorageManager.class.getMethod("getVolumePaths");
            String[] path = (String[]) method.invoke(manager);
            for (String p : path) {
                mPaths.add(p);
            }
            return mPaths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 屏蔽不合格路径
     *
     * @param file
     * @return
     */
    public static boolean isQualified(File file) {
        if (file == null)
            return false;
        String name = file.getName().toLowerCase(Locale.getDefault());

        if (file.isDirectory()) {
            if (file.isHidden())
                return false;
            if (name.startsWith("."))
                return false;
            if (name.startsWith("com."))
                return false;
            if (name.contains("cache"))
                return false;

            File[] files = file.listFiles();
            if (files == null || files.length == 0)
                return false;
            if (file.getAbsolutePath().split("/").length > 20)
                return false;
        } else {
            if (file.isHidden())
                return false;
            if (name.startsWith("."))
                return false;
        }
        return true;
    }

    public interface GetModeCallBack {
        void onFileNull(boolean isFileNull);
    }

    public interface OnFileFound {
        void onFileFound(File file);
    }

    public static boolean canListFiles(File file) {
        return file.canRead() && file.isDirectory();
    }

    public static boolean isImageFile(File file) {
        return !(file == null || !file.isFile()) && getMimeType(file).contains("image/");
    }

    public static boolean isMediaFile(File file) {
        return !(file == null || !file.isFile()) && (getMimeType(file).contains("audio/") || getMimeType(file).contains("video/"));
    }

    public static boolean isAudioFile(File file) {
        return !(file == null || !file.isFile()) && getMimeType(file).contains("audio/");
    }

    public static boolean isVideoFile(File file) {
        return !(file == null || !file.isFile()) && getMimeType(file).contains("video/");
    }

    public static boolean isFileSuffix(File file) {
        if (file.exists()) {
            String folderPath = file.getAbsolutePath().toLowerCase();
            String extension = folderPath.substring(folderPath.lastIndexOf("."));
            return extension.equals(StatusConfig.COURSE_WARE_FILE_SUFFIX);
        }
        return false;
    }

    /**
     * 根据type显示文件后缀类型
     *
     * @param file
     * @param type
     * @return
     */
    public static boolean checkStringPostFix(File file, int type) {
        if (type == 0 || type == 2 || type == 3) {
            postFixs = new String[]{".png", ".jpg", ".bmp", ".mp4", ".WMV", ".rmvb", ".AVI", ".MOV", ".mp3", ".pdf", ".doc", ".docx", ".xlsx", ".xls", ".pptx", ".ppt", StatusConfig.COURSE_WARE_FILE_SUFFIX};
        } else if (type == 1) {
            return false;
        } else if (type == 4) {
            postFixs = new String[]{".pdf"};
        } else if (type == 5) {
            postFixs = new String[]{".xlsx", ".xls"};
        } else if (type == 6) {
            postFixs = new String[]{".doc", ".docx"};
        } else if (type == 7) {
            postFixs = new String[]{".pptx", ".ppt"};
        } else if (type == 8) {
            postFixs = new String[]{".pdf", ".xlsx", ".xls", ".doc", ".docx", ".pptx", ".ppt"};
        } else if (type == 9) {
            postFixs = new String[]{".png", ".jpg"};
        }
        if (file.exists() && file.isFile()) {
            for (String postfix : postFixs) {
                String name = file.getName().toLowerCase();
                try {
                    String postName = name.substring(name.lastIndexOf("."));
                    if (postName.equals(postfix.toLowerCase())) {
                        return true;
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 得到后缀
     *
     * @param file
     * @return
     */
    public static String getMimeType(File file) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String pathName = file.getAbsolutePath().toLowerCase(Locale.getDefault());//将字符串转换成小写          获取系统默认语言
        String extension = pathName.substring(pathName.lastIndexOf(".") + 1);
        String mimeType = map.getMimeTypeFromExtension(extension);//返回给定扩展名的MIME类型
        if (TextUtils.isEmpty(mimeType))//如果字符串为空或长度为0的返回true
            mimeType = "*/*";
        return mimeType;
    }

    public static void getFiles(FileMode fileMode, int resource_type, String path,
                                GetModeCallBack getModeCallBack, OnFileFound fileCallback) {
        if (!TextUtils.isEmpty(path) && canListFiles(new File(path))) {
            getFilesList(fileMode, resource_type, path, getModeCallBack, fileCallback);
        }
    }

    /**
     * 得到所有文件路径
     *
     * @param fileMode
     * @param type
     * @param path
     * @param getModeCallBack
     * @param listener
     * @return
     */
    public static ArrayList<File> getFilesList(final FileMode fileMode, final int type, String path, GetModeCallBack getModeCallBack, OnFileFound listener) {
        final File file = new File(path);
        ArrayList<File> files = new ArrayList<>();
        if (file.exists() && file.isDirectory()) {
            File[] fileSort = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (fileMode == FileMode.FILE) {
                        if (pathname.isFile()) {
                            return checkStringPostFix(pathname, type);
                        } else {
                            return true;
                        }
                    } else if (fileMode == FileMode.PICTURE || fileMode == FileMode.PICTURE_FILE) {
                        return isImageFile(pathname);
                    } else if (fileMode == FileMode.MEDIA) {
                        return isMediaFile(pathname);
                    }
                    return false;
                }
            });
            if (fileSort.length == 0) {
                getModeCallBack.onFileNull(true);
            } else {
                Collections.sort(Arrays.asList(fileSort), new FileComparator());
                int length = fileSort.length;
                int hideLength = 0;
                for (File f : fileSort) {
                    String name = f.getName();
                    if (!name.startsWith(".")) {
                        files.add(f);
                        listener.onFileFound(f);
                    } else {
                        hideLength++;
                    }
                }
                if (length == hideLength) {
                    getModeCallBack.onFileNull(true);
                } else {
                    if (getModeCallBack != null)
                        getModeCallBack.onFileNull(false);
                }
            }
        }
        return files;
    }

    /**
     * 字体转换为全角符
     *
     * @param name
     * @return
     */
    public static String conversionToDBC(String name) {
        char[] c = name.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }

            if (c[i] > 32 && c[i] < 127)    //其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName) {
        if (fileName == null || fileName.length() <= 0)
            return null;
        try {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return null;
        }
    }

}
