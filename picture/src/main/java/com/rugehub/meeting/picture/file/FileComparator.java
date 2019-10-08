package com.rugehub.meeting.picture.file;

import java.io.File;
import java.util.Comparator;

/**
 * 文件排序
 */
public class FileComparator implements Comparator<File> {

    @Override
    public int compare(File lhs, File rhs) {
        if (lhs.isDirectory() && rhs.isFile()) {
            return -1;
        } else if (lhs.isFile() && rhs.isDirectory()) {
            return 1;
        }

        if (lhs.lastModified() > rhs.lastModified()) {
            return -1;
        } else if (lhs.lastModified() == rhs.lastModified()) {
            return lhs.getName().compareTo(rhs.getName());
        } else {
            return 1;
        }
    }
}