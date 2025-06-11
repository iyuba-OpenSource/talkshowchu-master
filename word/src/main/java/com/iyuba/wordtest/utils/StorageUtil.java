package com.iyuba.wordtest.utils;

import android.content.Context;
import android.util.Log;

import androidx.collection.ArraySet;


import com.iyuba.wordtest.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageUtil {

    private static final String  WAV_SUFFIX = ".wav";
    private static final String  TMP_PREFIX = ".tmp";
    private static final String  MP4_SUFFIX = ".mp4";
    private static final String  MP3_SUFFIX = ".mp3";
    private static final String  JPG_SUFFIX = ".jpg";
    private static final String  AAC_SUFFIX = ".aac";
    private static final String  AMR_SUFFIX = ".amr";


    public static File getMediaRankingDir(Context context, int voaId) {
        return new File(getMediaDir(context, voaId), "rank");
    }

    public static File getMediaDir(Context context, int voaId) {
        return BaseStorageUtil.getDir(context, String.valueOf(voaId));
    }

    public static File getWordZipDir(Context context) {
        return BaseStorageUtil.getDir(context, "words/");
    }

    public static File getWordDir(Context context) {
        return BaseStorageUtil.getDir(context, "words/"+context.getResources().getString(R.string.wordpath));
    }

    public static File getMediaDir(Context context , String url) {
        return BaseStorageUtil.getDir(context, parseUrlTOVoaId(url));
    }

    private static String parseUrlTOVoaId(String url) {
//        Log.d("diao123", url);
        String result = url.substring(url.lastIndexOf(File.separator)+1,url.indexOf("_")) ;
//        Log.e("diao123", "parseUrlTOVoaId: "+result);
        return result;
    }


    //  单词的音频等
    public static File getMediaDir(Context context, int bookId, int unitId) {
        return BaseStorageUtil.getDir(context, bookId + File.separator + unitId);
    }

    public static File getBookFolder(Context context, int bookId) {
        return BaseStorageUtil.getDir(context, String.valueOf(bookId));
    }

    public static File getImageUnzipDir(Context context,int bookId) {
        return BaseStorageUtil.getDir(context,String.valueOf(bookId));
    }



    public static String getVideoTmpFilename(int voaId) {
        return TMP_PREFIX + getVideoFilename(voaId);
    }

    public static String getAudioTmpFilename(int voaId) {
        return TMP_PREFIX + getAudioFilename(voaId);
    }

    public static String getVideoFilename(int voaId) {
        return voaId + MP4_SUFFIX;
    }

    private static String getAudioFilename(int voaId) {
        return voaId + MP3_SUFFIX;
    }

    public static String getImageFilename(int voaId) {
        return voaId + JPG_SUFFIX;
    }

    public static File getVideoFile(Context context, int voaId) {
        return new File(getMediaDir(context, voaId), getVideoFilename(voaId));
    }

    public static File getAudioFile(Context context, int voaId) {
        return new File(getMediaDir(context, voaId), getAudioFilename(voaId));
    }

    public static boolean checkFileExist(String dir, int voaId) {
        return isAudioExist(dir, voaId) && isVideoExist(dir, voaId);
    }

//    public static String getVideoUrl(int cat , int voaId) {
//        return VIDEO_PREFIX+cat+File.separator+ voaId +MP4_SUFFIX;
//    }


    public static boolean isVideoExist(String dir, int voaId) {
        return new File(dir, getVideoFilename(voaId)).exists();
    }

    public static boolean isAudioExist(String dir, int voaId) {
        return new File(dir, getAudioFilename(voaId)).exists();
    }


    public static boolean isWordAudioExist(String dir, int voaId) {
        return new File(dir, getWordAudioFilename(voaId)).exists();
    }

    public static boolean isWordAudioExist(String dir, String  wordUrl) {
        return new File(dir, getWordName(wordUrl)).exists();
    }

    public static String getWordName(String wordUrl) {
        return wordUrl.substring(wordUrl.lastIndexOf(File.separator)+1);
    }


    public static boolean deleteVideoFile(Context context, int voaId) {
        File dir = getMediaDir(context, voaId);
        File videoFile = new File(dir, getVideoFilename(voaId));
        return !videoFile.exists() || videoFile.delete();
    }

    public static boolean deleteAudioFile(Context context, int voaId) {
        File dir = getMediaDir(context, voaId);
        File audioFile = new File(dir, getAudioFilename(voaId));
        return !audioFile.exists() || audioFile.delete();
    }

    public static boolean renameVideoFile(String dir, int voaId) {
        String fromName = getVideoTmpFilename(voaId);
        String toName = getVideoFilename(voaId);
        return BaseStorageUtil.renameFile(dir, fromName, toName);
    }

    public static boolean renameAudioFile(String dir, int voaId) {
        String fromName = getAudioTmpFilename(voaId);
        String toName = getAudioFilename(voaId);
        return BaseStorageUtil.renameFile(dir, fromName, toName);
    }

    public static boolean hasRecordFile(Context context, int voaId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        File dirFile = new File(BaseStorageUtil.getDirPath(context, dir));
        return dirFile.exists() && dirFile.list().length != 0;
    }

    public static File getWavRecordFile(Context context, int voaId, long timeStamp, int paraId) {
        String dir = getShortRecordDir(voaId, timeStamp);
        return BaseStorageUtil.getFile(context, dir, paraId + WAV_SUFFIX);
    }

    public static File getAccRecordFile(Context context, int voaId, long timeStamp, int paraId) {
        String dir = getShortRecordDir(voaId, timeStamp);
        return BaseStorageUtil.getFile(context, dir, paraId + AAC_SUFFIX);
    }

    public static int getRecordNum(Context context, int voaId, long timestamp) {
        String dirPath = getShortRecordDir(voaId, timestamp);
        File dirFile = BaseStorageUtil.getDir(context, dirPath);
        Set<Integer> set = new ArraySet<>();
        for (File file : dirFile.listFiles()) {
            String name = file.getName();
            String index = name.split("\\.")[0];
            if (isNumeric(index)) {
                set.add(Integer.valueOf(index));
            }
        }
        return set.size();
    }

    private static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static void cleanRecordDir(Context context, int voaId, long timeStamp) {
        String dir = getShortRecordDir(voaId, timeStamp);
        File dirFile = BaseStorageUtil.getDir(context, dir);
        for (File file : dirFile.listFiles()) {
            file.delete();
        }
        dirFile.delete();
    }

    public static void cleanDraft(Context context, int voaId, long timeStamp) {
        String dir = getShortRecordDir(voaId, timeStamp);
        File dirFile = BaseStorageUtil.getDir(context, dir);
        for (File file : dirFile.listFiles()) {
            String name = file.getName();
            String index = name.split("\\.")[0];
            if (isNumeric(index)) {
                file.delete();
            }
        }
        if (dirFile.list().length <= 0) {
            dirFile.delete();
        }
    }

    public static File getParaRecordWavFile(Context context, int voaId, long timestamp, int paraId) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, paraId + WAV_SUFFIX);
    }

    public static File getParaRecordAacFile(Context context, int voaId, int paraId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, paraId + AAC_SUFFIX);
    }

    public static File getParaRecordAmrFile(Context context, int voaId, int paraId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, paraId + AMR_SUFFIX);
    }


    public static File getM4AMergeFile(Context context, int voaId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        return BaseStorageUtil.getFile(context, dir, "merge.mp3");
    }

    public static File getWavParaFile(Context context, int voaId, int paraId, long timestamp) {
        String dir = getShortRecordDir(voaId, timestamp);
        String filename = paraId + WAV_SUFFIX;
        return BaseStorageUtil.getFile(context, dir, filename);
    }

    public static File getRecordDir(Context context, int voaId, long timestamp) {
        return BaseStorageUtil.getDir(context, getShortRecordDir(voaId, timestamp));
    }

    public static String getShortRecordDir(int voaId, long timestamp) {
        return voaId + File.separator + timestamp;
    }



    public static File getAvatarFile(Context context, String filename) {
        return new File(BaseStorageUtil.getFilePath(context, filename));
    }




    public static String copyFile(Context context, int voaId, int paraId, long timestamp) {
        String filename = System.currentTimeMillis() + WAV_SUFFIX;
        String dir = voaId + File.separator + timestamp;
        File srcFile = BaseStorageUtil.getFile(context, dir, paraId + WAV_SUFFIX);
        File destFile = BaseStorageUtil.getFile(context, dir, filename);
        copyFile(srcFile, destFile);
        return filename;
    }

    // 复制文件
    public static void copyFile(File srcFile, File destFile) {
        int len; // 读取的字节数
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;

        try {
            bin = new BufferedInputStream(new FileInputStream(srcFile));
            bout = new BufferedOutputStream(new FileOutputStream(destFile));
            byte[] buffer = new byte[1024];

            while ((len = bin.read(buffer)) != -1) {
                bout.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(bin);
            close(bout);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static File getParaRecordMp3File(Context context, int voaId, int i, long timeStamp) {
        String dir = getShortRecordDir(voaId, timeStamp);
        return BaseStorageUtil.getFile(context, dir, i + ".mp3");
    }

    public  static String getWordAudioFilename(int voaId) {
        return "word"+voaId +MP3_SUFFIX;
    }

    public static boolean isImageExists(String mDir, int position) {
        return new File(mDir, getImageFilename(position)).exists();
    }

    public static boolean isImageExists(String mDir, String subDir) {
        return new File(mDir, subDir).exists();
    }

    public static boolean isVideoClipExist(String dir, String voaId) {
        String fileNameWithoutSuffix = getVideoNameFromClip(voaId);
        String fileNameWithSuffix = getVideoFilename(fileNameWithoutSuffix);
        return new File(dir, fileNameWithSuffix).exists();
    }

    public static String getVideoClipFilename(String url) {
        return  getVideoFilename(getVideoNameFromClip(url));
    }

    public static String getVideoFilename(String voaId) {
        return voaId + MP4_SUFFIX;
    }


    private static String getVideoNameFromClip(String videoUrl) {
        String result = videoUrl.substring(videoUrl.lastIndexOf(File.separator)+1,videoUrl.lastIndexOf("."));
        return result;
    }
}
