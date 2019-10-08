package com.rugehub.meeting.picture.file;

/**
 * 历史纪录
 */
public class ResourceHistory {

    private String currentFilePath = "";
    private String currentPicturePath = "";
    private String currentMediaPath = "";

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(String currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    public String getCurrentPicturePath() {
        return currentPicturePath;
    }

    public void setCurrentPicturePath(String currentPicturePath) {
        this.currentPicturePath = currentPicturePath;
    }

    public String getCurrentMediaPath() {
        return currentMediaPath;
    }

    public void setCurrentMediaPath(String currentMediaPath) {
        this.currentMediaPath = currentMediaPath;
    }
}
