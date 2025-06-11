//package com.iyuba.talkshow.util;
//
//import android.content.Context;
//
//import com.iyuba.talkshow.ui.main.MainMvpView;
//
//import java.io.File;
//
//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
//import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
//import cafe.adriel.androidaudioconverter.model.AudioFormat;
//
//public class ConvertAudio {
//
//    public static void load(Context context) {
//        AndroidAudioConverter.load(context, new ILoadCallback() {
//            @Override
//            public void onSuccess() {
//                // Great!
//            }
//            @Override
//            public void onFailure(Exception error) {
//                // FFmpeg is not supported by device
//            }
//        });
//    }
//
//    public static void wavToMp3(Context context, String wavPath, IConvertCallback callback) {
//        AndroidAudioConverter.with(context)
//                .setFile(new File(wavPath))
//                .setFormat(AudioFormat.AAC)
//                .setCallback(callback)
//                .convert();
//    }
//
//}
