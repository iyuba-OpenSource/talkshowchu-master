package com.iyuba.talkshow.newce.study.dubbingNew;

/**
 * @desction: 功能回调
 * @date: 2023/2/14 18:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public interface DubbingMoreListener {

    //分享
    void onShare();

    //下载
    void onDownload();

    //导出PDF
    void onImportPDF();

    //更新原文
    void onUpdateCourse();

    //保存视频
    void onSaveVideo();
}
