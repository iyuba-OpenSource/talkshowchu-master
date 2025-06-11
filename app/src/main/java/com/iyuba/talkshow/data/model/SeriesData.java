package com.iyuba.talkshow.data.model;

public class SeriesData {
    private String DescCn;
    private String Category;
    private String SeriesName;
    private String CreateTime;
    private String UpdateTime;
    private int SeriesCount;
    private int haveMicro;
    private String isVideo;
    private String HotFlg;
    private String Id;
    private String pic;
    private String KeyWords;

    public void setVideo(String video) {
        isVideo = video;
    }

    public String getVideo() {
        return isVideo;
    }

    public String getDescCn() {
        return DescCn;
    }

    public void setDescCn(String DescCn) {
        this.DescCn = DescCn;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getSeriesName() {
        return SeriesName;
    }

    public void setSeriesName(String SeriesName) {
        this.SeriesName = SeriesName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }

    public String getHotFlg() {
        return HotFlg;
    }

    public void setHotFlg(String HotFlg) {
        this.HotFlg = HotFlg;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getKeyWords() {
        return KeyWords;
    }

    public void setKeyWords(String KeyWords) {
        this.KeyWords = KeyWords;
    }

    public int getSeriesCount() {
        return SeriesCount;
    }

    public void setSeriesCount(int seriesCount) {
        SeriesCount = seriesCount;
    }

    public int getHaveMicro() {
        return haveMicro;
    }

    public void setHaveMicro(int haveMicro) {
        this.haveMicro = haveMicro;
    }
}
