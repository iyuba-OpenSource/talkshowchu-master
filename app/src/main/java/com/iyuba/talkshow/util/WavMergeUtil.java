package com.iyuba.talkshow.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.util.request.CloseUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import timber.log.Timber;

public class WavMergeUtil {

    Context mContext ;

    interface Wav {
        int SAMPLE_RATE = 44100;
        int BITS_PER_SAMPLE = 16;
        int CHANNELS = 2;
    }

    private static final int M_SIZE = 1024;
    private static final int DIGIT_OF_BYTE = 8;
    private static final long PER_LENGTH = Wav.SAMPLE_RATE * Wav.BITS_PER_SAMPLE * Wav.CHANNELS / DIGIT_OF_BYTE;

    //音频合成文件名，以时间戳+.wav
//    public static boolean merge(Context context, int voaId, long timeStamp, List<VoaText> list, int duration) {
//        try {
//            File dirFile = StorageUtil.getRecordDir(context, voaId, timeStamp);
//            File lastMergeFile = StorageUtil.getAacMergeFile(context, voaId, timeStamp);
//            String mergePath = lastMergeFile.getAbsolutePath();
//            deleteFile(lastMergeFile);
//
//            RandomAccessFile mergeFile = new RandomAccessFile(mergePath, "rw");
//            writeWavHeader(context, mergePath);
//
//            float playTime = 0;
//            float needTime;
//            for (VoaText voaText : list) {
//                String filename = voaText.paraId() + Constant.Voa.WAV_SUFFIX;
//                File recordFile = new File(dirFile, filename);
//                //如果没有文件，创建空音频文件。否则判断文件是否完整，不完整补全
//                needTime = voaText.timing() - playTime;
//                if(needTime > 0) {
//                    writeSilent(context, mergeFile, getWavWriteTimes(needTime));//写录音之前的空白文件
//                }
//
//                needTime = voaText.endTiming() - voaText.timing();
//                if (!recordFile.exists()) {
//                    writeSilent(context, mergeFile, getWavWriteTimes(needTime));
//                } else {
//                    write(mergeFile, recordFile);
//                    int writeTimes = getWavWriteTimes(recordFile, needTime);
//                    if(writeTimes > 0) {
//                        writeSilent(context, mergeFile, writeTimes);
//                    }
//                }
//                playTime = getPlayTime(new File(mergePath));
//            }
//
//            //写最后的音频文件
//            needTime = TimeUtil.milliSecToSec(duration) - playTime;
//            writeSilent(context, mergeFile, getWavWriteTimes(needTime));
//
//            updateWavHeader(mergePath);
//            CloseUtil.close(mergeFile);
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public static void write(RandomAccessFile destFile, InputStream inputStream) {
        try {
            BufferedInputStream bin = new BufferedInputStream(inputStream);
            destFile.seek(destFile.length());
            byte[] bytes = new byte[M_SIZE];
            int len;
            bin.read(new byte[44]);
            while ((len = bin.read(bytes)) != 0 && len != -1) {
                destFile.write(bytes, 0, len);
            }
            bin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(RandomAccessFile destFile, File file) {
        try {
            write(destFile, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void writeWavHeader(Context context, String filePath) {
        try {
            BufferedInputStream bis =
                    new BufferedInputStream(context.getResources().openRawResource(R.raw.header));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] bytes = new byte[44];
            int len;
            if ((len = bis.read(bytes)) != 0 && len != -1) {
                bos.write(bytes, 0, len);
            }
            CloseUtil.close(bis);
            CloseUtil.close(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateWavHeader(String path) {
        int length = (int) new File(path).length();
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
            randomAccessFile.seek(4);
            randomAccessFile.write(TypeConvertUtil.intToByteArray(length - 8), 0, 4);
            randomAccessFile.seek(40);
            randomAccessFile.write(TypeConvertUtil.intToByteArray(length - 44), 0, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getWavWriteTimes(File file, float sec) {
        float playTime = getPlayTime(file);
        return getWavWriteTimes(sec - playTime);
    }

    private static float getPlayTime(File file) {
            long length = file.length();
            return ((float) (length - WavHeader.Style.WAV_FILE_HEADER_SIZE))
                            / PER_LENGTH;
    }

    private static int getWavWriteTimes(float sec) {
        return TimeUtil.secToMilliSec(sec) / Constant.Voa.SILENT_PIECE_TIME;
    }

    private static void writeSilent(Context context, RandomAccessFile destFile, int times) {
        for(int i = 0; i < times; i++) {
           write(destFile, context.getResources().openRawResource(R.raw.silent));
        }
    }

    private static void deleteFile(File file) {
        if(file.exists()) file.delete();
    }

    //讲每一句的wav和后面的空白调整
    public static void merge(Context context, int voaId, int paraId, long timeStamp,
                             String filename, List<VoaText> list, long duration) {
        try {
            File dirFile = StorageUtil.getRecordDir(context, voaId, timeStamp);
            File recordFile = new File(dirFile, filename);
            float totalTime = getParaAndBlackTime(list, paraId, duration);
            int needTimes = getWavWriteTimes(recordFile, totalTime);
            Timber.e(paraId + "++++++++" + totalTime + "++++++++" + getPlayTime(recordFile) + "*********" + needTimes);
            writeSilent(context, new RandomAccessFile(recordFile, "rw"), needTimes);
            updateWavHeader(recordFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static float getParaAndBlackTime(List<VoaText> list, int paraId, long duration) {
        float sec;
        if(paraId == list.size()) {
            sec = TimeUtil.milliSecToSec((int) duration) - list.get(paraId - 1).timing();
        } else {
            sec =  list.get(paraId).timing() - list.get(paraId - 1).timing();
        }
        return sec;
    }
    private static float getBlankTime(List<VoaText> list, int paraId, long duration) {
        float sec;
        if(paraId == list.size()) {
            sec = TimeUtil.milliSecToSec((int) duration) - list.get(paraId - 1).endTiming();
        } else {
            sec =  list.get(paraId).timing() - list.get(paraId - 1).endTiming();
        }
        return sec;
    }

    //aac操作
    private static final float AAC_PIECE_DURATION = 0.090f;

    private static final float M4A_PIECE_DURATION = 0.030f;


    private static int getAacWriteTimes(float sec) {
//        return (int) (sec / AAC_PIECE_DURATION);
        return (int) (sec / 0.19f);

    }

    private static int getM4AWriteTimes(float sec) {
        return (int) (sec / M4A_PIECE_DURATION);
    }

    private static int getParaAndBlankTimes(int paraId, List<VoaText> list, long duration) {
        float sec = getParaAndBlackTime(list, paraId, duration);
        return getAacWriteTimes(sec);
    }

    public static int getBlankTimes(int paraId, List<VoaText> list, long duration) {
        float sec = getBlankTime(list, paraId, duration);
        return getAacWriteTimes(sec);
    }

    private static void writeBlank(Context context, String filePath) {
        try {
            BufferedInputStream bis =
                    new BufferedInputStream(context.getResources().openRawResource(R.raw.silent61));
            RandomAccessFile randomAccess = new RandomAccessFile(filePath, "rw");
            randomAccess.seek(randomAccess.length());
            byte[] bytes = new byte[1024];
            int len;
            while ((len = bis.read(bytes)) != 0 && len != -1) {
                randomAccess.write(bytes, 0, len);
            }
            CloseUtil.close(bis);
            CloseUtil.close(randomAccess);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeParaBlank(Context context, List<VoaText> list, int paraId, String filePath, long duration) {
        int times = getParaAndBlankTimes(paraId, list, duration);
        Timber.e("writeParaBlank********" + paraId + ":" + times);
        for (int i = 0; i < times; i++) {
            writeBlank(context, filePath);
        }
    }

    private static void writeHeadBlank(Context context, List<VoaText> list, String filePath) {
        for (int i = 0; i < list.size(); i++) {
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).timing());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).getFilename());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).getScore());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).getVoaId());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).getParseData());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).imgPath());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).sentence());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).sentenceCn());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).endTiming());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).isIscore());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).isIsshowbq());
//            Log.d("com.iyuba.talkshow", "writeHeadBlank: "+i+list.get(i).paraId());
        }
//        Log.d("com.iyuba.talkshow", "writeHeadBlank: "+list.get(0).timing());

        int times = getAacWriteTimes(list.get(0).timing());
        //        int times = (int) list.get(0).timing();

        Timber.e("writeHeadBlank********" + times);
        for (int i = 0; i < times; i++) {
            writeBlank(context, filePath);
        }
    }

    public static float getFileTime(String path) {
        try {
            Timber.e("******" + path);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return TimeUtil.milliSecToSec(mediaPlayer.getDuration());

    }

    private static void writeFile(File file, String filePath) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            RandomAccessFile randomAccess = new RandomAccessFile(filePath, "rw");
            randomAccess.seek(randomAccess.length());
            byte[] bytes = new byte[1024];
            int len;
            while ((len = bis.read(bytes)) != 0 && len != -1) {
                randomAccess.write(bytes, 0, len);
            }
            CloseUtil.close(bis);
            CloseUtil.close(randomAccess);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeBlank(Context context, List<VoaText> list, int paraId, long duration, String filePath, String destPath) {
        float totalTime = getParaAndBlackTime(list, paraId, duration);
        float fileTime = getFileTime(filePath);
        int times = getAacWriteTimes(totalTime - fileTime);
        for (int i = 0; i < times; i++) {
            writeBlank(context, destPath);
        }
    }

    public static void merge(Context context, int voaId, long timeStamp, List<VoaText> list, long duration) {
//        File lastMergeFile = StorageUtil.getAACMergeFile(context, voaId, timeStamp);
        File lastMergeFile = StorageUtil.getM4AMergeFile(context, voaId, timeStamp);
        String mergePath = lastMergeFile.getAbsolutePath();
        deleteFile(lastMergeFile);

//        final File[] wantFile = new File[1];
        writeHeadBlank(context, list, mergePath);
        for(VoaText voaText : list) {
//            File file = StorageUtil.getParaRecordAacFile(context, voaId, voaText.paraId(), timeStamp);
            File file = StorageUtil.getParaRecordAacFile(context, voaId, voaText.paraId(), timeStamp);
            if(file.exists()) {
                writeFile(file, mergePath);
                writeBlank(context, list, voaText.paraId(), duration, file.getAbsolutePath(), mergePath);
            } else {
                writeParaBlank(context, list, voaText.paraId(), mergePath, duration);
            }
        }
//        File flacFile = new File(mergePath);
//        final Boolean[] i = {true};
//        IConvertCallback callback = new IConvertCallback() {
//            @Override
//            public void onSuccess(File convertedFile) {
//                // So fast? Love it!
//                wantFile[0] =convertedFile;
//                i[0] = false;
//            }
//            @Override
//            public void onFailure(Exception error) {
//                // Oops! Something went wrong
//            }
//        };
//
//        AndroidAudioConverter.with(context)
//                // Your current audio file
//                .setFile(flacFile)
//
//                // Your desired audio format
//                .setFormat(AudioFormat.M4A)
//
//                // An callback to know when conversion is finished
//                .setCallback(callback)
//
//                // Start conversion
//                .convert();
//        while(i[0]){
//
//        }2
    }

    private static final MediaPlayer mediaPlayer = new MediaPlayer();
}
