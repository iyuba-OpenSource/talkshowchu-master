package com.iyuba.talkshow.event;

/**
 * Created by carl shen on 2021/8/25
 * New Primary English, new study experience.
 */
public class SyncDataEvent {
    public int status;
    public int percent;
    public int downloadId;
    public String msg;

    public SyncDataEvent(int status, int downloadId) {
        this.status = status;
        this.downloadId = downloadId;
    }
    public SyncDataEvent(int status) {
        this.status = status;
    }

    public SyncDataEvent(int status, int percent, int downloadId) {
        this.status = status;
        this.percent = percent;
        this.downloadId = downloadId;
    }

    public SyncDataEvent(int status, String msg, int downloadId) {
        this.status = status;
        this.msg = msg;
        this.downloadId = downloadId;
    }

    public interface Status {
        int FINISH = 1;
        int DOWNLOADING = 0;
        int ERROR = 2 ;
        int START = 3;
    }
}
