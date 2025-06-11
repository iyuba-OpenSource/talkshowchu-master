package com.iyuba.wordtest.ui.listen.singleWrite.model;

public class MlKitModelEvent {
    public static final String tag_success = "success";
    public static final String tag_fail = "fail";

    private String showTag;
    private String showMsg;

    public MlKitModelEvent(String showTag, String showMsg) {
        this.showTag = showTag;
        this.showMsg = showMsg;
    }

    public String getShowTag() {
        return showTag;
    }

    public String getShowMsg() {
        return showMsg;
    }
}
