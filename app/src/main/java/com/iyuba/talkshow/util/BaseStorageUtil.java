package com.iyuba.talkshow.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/1/12/012.
 */

public class BaseStorageUtil {

    private static boolean isExistSDCard() {
        return Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
    }

    public static File getInnerStorageDir(Context context) {
        return context.getFilesDir();
    }

    public static File getExternalStorageDir(Context context) {
        return context.getExternalFilesDir(null);
    }

    //获取文件的存放路径。有sd卡放在存储卡中，没有放在手机存储卡中
    public static String getStorageDir(Context context) {
        File dirFile =  isExistSDCard() ? getExternalStorageDir(context) : getInnerStorageDir(context);
        return dirFile.getAbsolutePath();
    }

    public static File getInnerFile(Context context, String filename) {
        return new File(getInnerStorageDir(context), filename);
    }

    public static File getInnerFile(Context context, String dir, String filename) {
        File dirFile = new File(getInnerStorageDir(context), dir);
        if(!dirFile.exists()) dirFile.mkdirs();
        return new File(dirFile, filename);
    }

    public static File getInnerDir(Context context, String dir) {
        File dirFile = new File(getInnerStorageDir(context), dir);
        if(!dirFile.exists()) dirFile.mkdirs();
        return dirFile;
    }

    public static File getExternalFile(Context context, String filename) {
        return new File(getExternalStorageDir(context), filename);
    }

    public static File getExternalFile(Context context, String dir, String filename) {
        File dirFile = new File(getExternalStorageDir(context), dir);
        if(!dirFile.exists()) dirFile.mkdirs();
        return new File(dirFile, filename);
    }

    public static File getExternalDir(Context context, String dir) {
        File dirFile = new File(getExternalStorageDir(context), dir);
        if(!dirFile.exists()) dirFile.mkdirs();
        return dirFile;
    }

    public static File getFile(Context context, String dir, String filename) {
        return isExistSDCard() ?
                getExternalFile(context, dir, filename) : getInnerFile(context, dir, filename);
    }

    public static File getDir(Context context, String dir) {
        return isExistSDCard() ?
                getExternalDir(context, dir) : getInnerDir(context, dir);
    }

    //获取音频的存放路径，先从手机内置的存储卡中找，然后在sd卡中找。找不到返回null。
    public static String getExistFilePath(Context context, String filename) {
        File innerFile = getInnerFile(context, filename);
        File externalFile = getExternalFile(context, filename);
        return innerFile.exists() ? innerFile.getAbsolutePath() : (externalFile.exists() ? externalFile.getAbsolutePath() : null);
    }

    //获取文件的存放路径（如果有sd卡，则存放在sd卡）
    public static String getFilePath(Context context, String filename) {
        File innerFile = getInnerFile(context, filename);
        File externalFile = getExternalFile(context, filename);
        return isExistSDCard() ? externalFile.getAbsolutePath() : innerFile.getAbsolutePath();
    }

    public static String getDirPath(Context context, String dirName) {
        String dir = getStorageDir(context);
        File newDir = new File(dir + File.separator + dirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        return newDir.getAbsolutePath();
    }

    public static boolean renameFile(String dir, String fromName, String toName) {
        if (fromName.startsWith("tmp")){
            File fromFile = new File(dir, fromName);
            File toFile = new File(dir, toName);
            return fromFile.renameTo(toFile);
        }else {
            return false ;
        }

    }

}
