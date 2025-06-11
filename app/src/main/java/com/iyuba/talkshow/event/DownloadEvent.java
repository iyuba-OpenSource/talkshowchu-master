package com.iyuba.talkshow.event;

/**
 * Created by Administrator on 2017/1/6/006.
 */

public class DownloadEvent {
    public int status;
    public int percent;
    public int downloadId;
    public String msg;

    public DownloadEvent(int status, int downloadId) {
        this.status = status;
        this.downloadId = downloadId;
    }
    public DownloadEvent(int status) {
        this.status = status;
    }

    public DownloadEvent(int status, int percent, int downloadId) {
        this.status = status;
        this.percent = percent;
        this.downloadId = downloadId;
    }

    public DownloadEvent(int status, String msg, int downloadId) {
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
