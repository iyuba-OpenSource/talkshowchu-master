package com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload.submit;

/**
 * 本地记录的广告模型
 */
public class AdLocalMarkBean {

    private String adType;//广告类型
    private String adPosition;//广告显示位置
    private String adOperation;//广告操作
    private long createTime;//创建时间

    public AdLocalMarkBean(String adType, String adPosition, String adOperation, long createTime) {
        this.adType = adType;
        this.adPosition = adPosition;
        this.adOperation = adOperation;
        this.createTime = createTime;
    }

    public String getAdType() {
        return adType;
    }

    public String getAdPosition() {
        return adPosition;
    }

    public String getAdOperation() {
        return adOperation;
    }

    public long getCreateTime() {
        return createTime;
    }
}
