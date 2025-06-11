package com.iyuba.wordtest.utils;

import android.media.MediaRecorder;
import android.os.Handler;
import android.widget.ImageView;


import java.io.File;
import java.io.IOException;

/**
 * amr音频处理
 *
 * @author hongfa.yy
 * @version 创建时间2012-11-21 下午4:33:28
 *          <p>
 *          来源网络 http://shuimuqinghua77.iteye.com/blog/1739128
 */
public class RecordManager {
    public static final int MAX_LENGTH = 1000 * 60 * 10;// �?大录音时�?1000*60*10;
    private final Handler mHandler = new Handler();
    private MediaRecorder mMediaRecorder;
    private final File file;
    private long startTime;
    private long endTime;
    /**
     * 更新话筒状�?? 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅�? Vi基准值为600：我是�?�么制定基准值的呢？ �?20
     * * Math.log10(mMediaRecorder.getMaxAmplitude() / Vi)==0的时候vi就是我所�?要的基准�?
     * 当我不对�?麦克风说任何话的时�?�，测试获得的mMediaRecorder.getMaxAmplitude()值即为基准�?��??
     * Log.i("mic_", "麦克风的基准值：" + mMediaRecorder.getMaxAmplitude());前提时不对麦克风说任何话
     */
    private final int BASE = 600;
    private final int SPACE = 100;// 间隔取样时间
    private ImageView view;
    private final Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };
    public RecordManager(File file, ImageView view) {
        this.file = file;
        this.view = view;
    }

    public RecordManager(File file) {
        this.file = file;
    }

    /**
     * �?始录�? 使用amr格式
     *
     * @return
     */
    public void startRecord() {
        // �?始录�?
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
			/* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克�?
			/*
			 * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
			 * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
			 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			/* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			/* ③准�? */
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            /* ④开�? */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
			/* 获取�?始时�?* */
            startTime = System.currentTimeMillis();
            // pre=mMediaRecorder.getMaxAmplitude();
            updateMicStatus();
        } catch (IllegalStateException e) {

        } catch (IOException e) {
        }

    }

    /**
     * 停止录音
     *
     */
    public long stopRecord() {
        mHandler.removeCallbacks(mUpdateMicStatusTimer);
        if (mMediaRecorder == null)
            return 0L;
        if (startTime == 0 || (startTime < endTime)) {
            return 0L;
        } else {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            } catch (Exception var2) { }
        }
        endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private void updateMicStatus() {
        if (mMediaRecorder != null && view != null) {
            int vuSize = 8 * mMediaRecorder.getMaxAmplitude() / 32768;
            // int ratio = mMediaRecorder.getMaxAmplitude() / BASE;
			/*
			 * int db = 0;// 分贝 if (ratio > 1) db = (int) (20 *
			 * Math.log10(ratio));
			 */
//            switch (vuSize % 8) {
//                case 0:
//                    view.setBackgroundResource(R.drawable.amp1);
//                    break;
//                case 1:
//                    view.setBackgroundResource(R.drawable.amp1);
//                    break;
//                case 2:
//                    view.setBackgroundResource(R.drawable.amp2);
//                    break;
//                case 3:
//                    view.setBackgroundResource(R.drawable.amp3);
//                    break;
//                case 4:
//                    view.setBackgroundResource(R.drawable.amp4);
//                    break;
//                case 5:
//                    view.setBackgroundResource(R.drawable.amp5);
//                    break;
//                case 6:
//                    view.setBackgroundResource(R.drawable.amp6);
//                    break;
//                case 7:
//                    view.setBackgroundResource(R.drawable.amp7);
//                    break;
//                default:
//                    // view.setImageResource(R.drawable.amp6);
//                    break;
//            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public double getVolume(){
        double ratio = mMediaRecorder.getMaxAmplitude();
        if (ratio > 1)
            ratio = 20 * Math.log10(ratio);
        return ratio;
    }


}