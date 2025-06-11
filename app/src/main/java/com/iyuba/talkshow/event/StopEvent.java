package com.iyuba.talkshow.event;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public class StopEvent {
    public int source;

    public StopEvent(int notStop) {
        this.source = notStop;
    }

    public interface SOURCE {
        int VIDEO = 1;
        int COMMENT = 2;
        int VOICE = 3;
    }
}
