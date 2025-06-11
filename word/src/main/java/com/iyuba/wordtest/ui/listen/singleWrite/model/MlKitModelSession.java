package com.iyuba.wordtest.ui.listen.singleWrite.model;

/**
 * 谷歌
 */
public class MlKitModelSession {
    private static MlKitModelSession instance;
    public static MlKitModelSession getInstance(){
        if (instance==null){
            synchronized (MlKitModelSession.class){
                if (instance==null){
                    instance = new MlKitModelSession();
                }
            }
        }
        return instance;
    }

    //是否正在下载模型
    private boolean isDownloadModeling = false;

    public void setModelDownloadState(boolean isDownloading){
        this.isDownloadModeling = isDownloading;
    }

    public boolean getModelDownloadState(){
        return isDownloadModeling;
    }
}
