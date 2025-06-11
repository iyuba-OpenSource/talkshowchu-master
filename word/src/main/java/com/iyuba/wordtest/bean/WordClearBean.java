package com.iyuba.wordtest.bean;

import com.iyuba.wordtest.entity.TalkShowWords;

import java.io.Serializable;

/**
 * 单词消消乐的模型
 */
public class WordClearBean implements Serializable {

    public static final int TAG_SHOW_PORN = 0;//单词释义
    public static final int TAG_SHOW_WORD = 1;//单词

    private int showTag;//显示哪些信息
    private String lineId;//连线的id(bookid_unitid_position)
    private boolean isVisible;//是否显示

    private TalkShowWords data;//数据

    public WordClearBean(int showTag, String lineId, boolean isVisible, TalkShowWords data) {
        this.showTag = showTag;
        this.lineId = lineId;
        this.isVisible = isVisible;
        this.data = data;
    }

    public int getShowTag() {
        return showTag;
    }

    public void setShowTag(int showTag) {
        this.showTag = showTag;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public TalkShowWords getData() {
        return data;
    }

    public void setData(TalkShowWords data) {
        this.data = data;
    }
}
