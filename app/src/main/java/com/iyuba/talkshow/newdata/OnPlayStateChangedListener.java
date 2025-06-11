package com.iyuba.talkshow.newdata;

/**
 * Created by carl shen on 2020/8/7
 * New Junior English, new study experience.
 */
public interface OnPlayStateChangedListener {
	void playSuccess();
	void playFaild();
	void playCompletion();
	void playPause();
	void playStart();
	void bufferingUpdate(int progress);
}
