package com.iyuba.talkshow.event;

/**
 * Created by Administrator on 2017/2/7/007.
 */

public class CommentEvent {
    public int status;

    public CommentEvent(int status) {
        this.status = status;
    }

    public interface Status {
        int GONE = 0;
        int SHOW = 1;
    }
}
