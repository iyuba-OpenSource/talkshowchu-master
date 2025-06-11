package com.iyuba.talkshow.event;

/**
 * Created by carl shen on 2021/7/29
 * New Primary English, new study experience.
 */
public class PlayListEvent {
    public int PlayState = 0;

    public PlayListEvent(int state) {
        PlayState = state;
    }
}
