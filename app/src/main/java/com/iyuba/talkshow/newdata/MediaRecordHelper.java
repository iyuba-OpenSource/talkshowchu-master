package com.iyuba.talkshow.newdata;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;

/**
 * Created by carl shen on 2020/8/3
 * New Primary English, new study experience.
 */
public class MediaRecordHelper {
    private MediaRecorder mediaRecorder = new MediaRecorder();
    private File audioFile;

    //判断是否正在录音
    public boolean isRecording = false;

    private static final int BASE = 1;
    private long startTime,endtime,recordTime;

    public interface State {
        int ERROR = -1;
        int INITIAL = 0;
        int RECORDING = 1;
        int COMPLETED = 2;
        int RELEASED = 3;
    }

    private final int mBaseAmplitude = 80; // magic number...just a joke

    public String getFilePath() {
        return MP4FilePath;
    }

    public void setFilePath(String MP4FilePath) {
        this.MP4FilePath = MP4FilePath;
    }

    private String MP4FilePath;

    public void recorder_Media() {
        File path = new File(MP4FilePath);
        audioFile = path;
        if (audioFile.exists()) {
            audioFile.delete();
        }
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(path.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            startTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            mediaRecorder = new MediaRecorder();
        }
    }


    public void stop_record() {
        try {
            //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
            //报错为：RuntimeException:stop failed

            if (isRecording) {
                mediaRecorder.setOnErrorListener(null);
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.setPreviewDisplay(null);
                isRecording = false;
                mediaRecorder.stop();
                mediaRecorder.reset();
                endtime = System.currentTimeMillis();
                recordTime = endtime - startTime;
            }

        } catch (IllegalStateException e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }catch (RuntimeException e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }catch (Exception e) {
            // TODO: handle exception
            Log.i("Exception", Log.getStackTraceString(e));
        }
    }
    public long getRecordTime() {
        return  recordTime;
    }

    public void release() {
        mediaRecorder.release();
    }

    /**
     * 获取录音的声音分贝值
     *
     * @return
     */
    public int getDB() {
        int result = (int) (20 * Math.log10(mediaRecorder.getMaxAmplitude() / mBaseAmplitude));
        return (result >= 0) ? result : 0;
    }

}
