package com.iyuba.talkshow.lil.help_fix.data.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 口语秀预览的展示数据
 * @date: 2023/6/7 16:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingPreviewShowBean implements Serializable {

    private String types;//类型
    private String bookId;//书籍
    private String voaId;//章节

    private String videoPath;//视频路径
    private String videoUrl;//视频链接
    private String bgAudioPath;//背景音路径
    private String bgAudioUrl;//背景音链接

    private double rightScore;//准确度--总分/评测的句子数量
    private double completeScore;//完成度--评测数量/总数量
    private double fluentScore;//流畅度--单词总分/单词总量

    private List<DubbingBean> dubbingList;//评测数据

    public DubbingPreviewShowBean(String types, String bookId, String voaId, String videoPath, String videoUrl, String bgAudioPath, String bgAudioUrl, double rightScore, double completeScore, double fluentScore, List<DubbingBean> dubbingList) {
        this.types = types;
        this.bookId = bookId;
        this.voaId = voaId;
        this.videoPath = videoPath;
        this.videoUrl = videoUrl;
        this.bgAudioPath = bgAudioPath;
        this.bgAudioUrl = bgAudioUrl;
        this.rightScore = rightScore;
        this.completeScore = completeScore;
        this.fluentScore = fluentScore;
        this.dubbingList = dubbingList;
    }

    public String getTypes() {
        return types;
    }

    public String getBookId() {
        return bookId;
    }

    public String getVoaId() {
        return voaId;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getBgAudioPath() {
        return bgAudioPath;
    }

    public String getBgAudioUrl() {
        return bgAudioUrl;
    }

    public double getRightScore() {
        return rightScore;
    }

    public double getCompleteScore() {
        return completeScore;
    }

    public double getFluentScore() {
        return fluentScore;
    }

    public List<DubbingBean> getDubbingList() {
        return dubbingList;
    }

    public static class DubbingBean{
        private String sentence;
        private double startTime;
        private double endTime;

        private String localPath;
        private String remoteUrl;
        private boolean isBgAudio;

        private int curIndex;

        public DubbingBean(String sentence, double startTime, double endTime, String localPath, String remoteUrl, boolean isBgAudio, int curIndex) {
            this.sentence = sentence;
            this.startTime = startTime;
            this.endTime = endTime;
            this.localPath = localPath;
            this.remoteUrl = remoteUrl;
            this.isBgAudio = isBgAudio;
            this.curIndex = curIndex;
        }

        public String getSentence() {
            return sentence;
        }

        public double getStartTime() {
            return startTime;
        }

        public double getEndTime() {
            return endTime;
        }

        public String getLocalPath() {
            return localPath;
        }

        public String getRemoteUrl() {
            return remoteUrl;
        }

        public boolean isBgAudio() {
            return isBgAudio;
        }

        public int getCurIndex() {
            return curIndex;
        }
    }
}
