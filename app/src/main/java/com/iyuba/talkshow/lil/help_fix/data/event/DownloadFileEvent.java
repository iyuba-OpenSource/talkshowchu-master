package com.iyuba.talkshow.lil.help_fix.data.event;

/**
 * @title:
 * @date: 2023/6/6 15:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DownloadFileEvent {

    public static final String DOWNLOAD_ING = "download_ing";
    public static final String DOWNLOAD_FINISH = "download_finish";
    public static final String DOWNLOAD_ERROR = "download_error";

    private String status;
    private String msg;

    public DownloadFileEvent(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
