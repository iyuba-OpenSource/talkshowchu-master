package com.iyuba.wordtest.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * @desction: 声音播放
 * @date: 2023/2/9 14:06
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class SoundUtil {

    /**
     * 播放声音
     *
     * 文件小，资源占用小
     */
    public static SoundPool playSound(Context context,int rawId){
        SoundPool soundPool = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder attributes = new AudioAttributes.Builder();
            attributes.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attributes.build());
            soundPool = builder.build();
        }else {
            soundPool = new SoundPool(1,AudioManager.STREAM_MUSIC,5);
        }
        soundPool.load(context,rawId,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(1,1,1,0,0,1);
            }
        });
        return soundPool;
    }

    /**
     * 播放声音
     *
     * 文件大，占用资源大
     */
    public static void playMedia(){

    }
}
