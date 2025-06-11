package com.iyuba.talkshow.util.request;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

public interface ProgressListener {
    void onProgress(long hasWrittenLen, long totalLen, boolean hasFinish);
}
