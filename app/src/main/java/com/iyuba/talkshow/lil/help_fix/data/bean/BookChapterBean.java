package com.iyuba.talkshow.lil.help_fix.data.bean;

import java.io.Serializable;

/**
 * @title: 章节数据
 * @date: 2023/5/7 18:40
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 通用数据类，如果缺失需要显示的数据，则直接加上即可，需要保证数据库的内容正确就可以
 */
public class BookChapterBean implements Serializable {

    private String types;
    private String voaId;
    private String bookId;

    private String audioUrl;//课程音频链接
    private String picUrl;//章节图片链接
    private String videoUrl;//章节的视频链接
    private String bgAudioUrl;//背景音的音频链接

    private String titleEn;
    private String titleCn;

    //小说的数据
    private String orderNumber;
    private String chapterOrder;
    private String level;

    //通用的数据
    private boolean showWord = false;//显示单词
    private boolean showImage = false;//显示点读
    private boolean showExercise = false;//显示习题
    private boolean showVideo = false;//显示口语秀

    public BookChapterBean(String types, String voaId, String bookId, String audioUrl, String picUrl, String videoUrl, String bgAudioUrl, String titleEn, String titleCn, String orderNumber, String chapterOrder, String level, boolean showWord, boolean showImage, boolean showExercise, boolean showVideo) {
        this.types = types;
        this.voaId = voaId;
        this.bookId = bookId;
        this.audioUrl = audioUrl;
        this.picUrl = picUrl;
        this.videoUrl = videoUrl;
        this.bgAudioUrl = bgAudioUrl;
        this.titleEn = titleEn;
        this.titleCn = titleCn;
        this.orderNumber = orderNumber;
        this.chapterOrder = chapterOrder;
        this.level = level;
        this.showWord = showWord;
        this.showImage = showImage;
        this.showExercise = showExercise;
        this.showVideo = showVideo;
    }

    public String getTypes() {
        return types;
    }

    public String getVoaId() {
        return voaId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getBgAudioUrl() {
        return bgAudioUrl;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public String getTitleCn() {
        return titleCn;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getChapterOrder() {
        return chapterOrder;
    }

    public String getLevel() {
        return level;
    }

    public void setShowWord(boolean showWord) {
        this.showWord = showWord;
    }

    public boolean isShowWord() {
        return showWord;
    }

    public boolean isShowImage() {
        return showImage;
    }

    public boolean isShowExercise() {
        return showExercise;
    }

    public boolean isShowVideo() {
        return showVideo;
    }
}
