package com.iyuba.talkshow.event;

public class AudioEvent {
    public int state;

    public AudioEvent(int state) {
        this.state = state;
    }

    public interface State {
        int ERROR = -1;
        int IDLE = ERROR + 1;
        int INITIALIZED = IDLE + 1;
        int PREPARING = INITIALIZED + 1;
        int PREPARED = PREPARING + 1;
        int PLAYING = PREPARED + 1;
        int PAUSED = PLAYING + 1;
        int INTERRUPTED = PAUSED + 1;
        int STOP_INTERRUPTED = INTERRUPTED + 1;
        int COMPLETED = STOP_INTERRUPTED + 1;
        int STOPPED = COMPLETED + 1;
        int RELEASED = STOPPED + 1;

    }
}
