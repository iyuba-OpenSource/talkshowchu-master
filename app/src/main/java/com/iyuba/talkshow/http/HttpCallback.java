package com.iyuba.talkshow.http;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * HttpCallback
 */

public abstract class HttpCallback implements Callback {
    public abstract void onSucceed(Call call, String response);

    public abstract void onError(Call call, Exception e);

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onFailure(final Call call, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onError(call, e);
            }
        });
    }

    @Override
    public void onResponse(final Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
            onFailure(call, new IOException("HTTP REQUEST ERROR"));
            return;
        }
        final String res;
        try {
            res = response.body().string().trim();
        } catch (Exception e) {
            e.printStackTrace();
            onFailure(call, new IOException("HTTP REQUEST ERROR"));
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onSucceed(call, res);
            }
        });
    }
}
