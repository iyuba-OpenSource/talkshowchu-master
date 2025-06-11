package com.iyuba.talkshow.lil.help_fix.ui.study.section;


import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;

/**
 * @title:
 * @date: 2023/7/7 09:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface SectionView extends BaseView {

    //展示提交结果
    void showReadReportResult(boolean isSubmit);

    /************广告点击逻辑***********/
    //点击广告结果
    void showClickAdResult(boolean isSuccess,String showMsg);
}
