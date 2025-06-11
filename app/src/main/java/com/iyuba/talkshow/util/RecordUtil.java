package com.iyuba.talkshow.util;


import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by ivotsm on 2017/5/4.
 */

public class RecordUtil {
    private static final int SAMPLE_RATE_IN_HZ = 16000;
    private final MediaRecorder recorder = new MediaRecorder();
    // 录音的路径
    private String mPath;
    private Handler mHandler;
    static {
        System.loadLibrary("Hello");
    }

    public native void convertmp3(String wav, String mp3);

    public RecordUtil(){

    }

    public RecordUtil(String path, Handler handler) {
        mPath = path;
        this.mHandler = handler;
    }

    public void pcm2mp3(String pcmFilePath,String mp3FilePath){
        pcm2wav(pcmFilePath,mp3FilePath.replace(".mp3",".wav"));
        wav2mp3(mp3FilePath.replace(".mp3",".wav"));
    }

    /**
     * 将pcm格式的文件加头处理成wav格式的音频文件
     */
    public void pcm2wav(String pcmFilePath, String wavFilePath) {
        try {
            FileInputStream fis = new FileInputStream(pcmFilePath);
            FileOutputStream fos = new FileOutputStream(wavFilePath);

            byte[] buf = new byte[1024 * 4];
            int size = fis.read(buf);
            int PCMSize = 0;
            while (size != -1) {
                PCMSize += size;
                size = fis.read(buf);
            }
            fis.close();

            // 添加wav头
            WaveHeader header = new WaveHeader();
            header.fileLength = PCMSize + (44 - 8);
            header.FmtHdrLeth = 16;
            header.BitsPerSample = 16;
            header.Channels = 1;
            header.FormatTag = 0x0001;
            header.SamplesPerSec = 16000;
            header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
            header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
            header.DataHdrLeth = PCMSize;

            byte[] h = header.getHeader();

            assert h.length == 44;
            // write header
            fos.write(h, 0, h.length);
            // 将pcm文件数据先读到buf，再从buf写到wav文件中
            fis = new FileInputStream(pcmFilePath);
            size = fis.read(buf);
            while (size != -1) {
                fos.write(buf, 0, size);
                size = fis.read(buf);
            }
            fis.close();
            fos.close();

            System.out.println("Convert OK!");
        } catch (Exception e) {
            Log.e("RecordUtil",
                    "pcm failed to convert into wav File:" + e.getMessage());
        }
    }

    public void deleteFile(String path) {

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * wav格式转化成MP3格式
     *
     * @param wavFileName
     */
    public void wav2mp3(final String wavFileName) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                convertmp3(wavFileName, wavFileName.replace(".wav", ".mp3"));
                // wav转换为MP3后通知应用上传MP3资源
//                mHandler.sendEmptyMessage(2);
            }

        });
        thread.start();
    }
}
