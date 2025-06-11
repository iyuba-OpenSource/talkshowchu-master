package com.iyuba.talkshow.newce.study.section;

import android.util.Pair;

import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * @title:
 * @date: 2023/8/31 18:10
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface SectionView extends MvpView {

    //展示详情数据
    void showVoaText(List<Pair<String,String>> list);

    //展示阅读报告提交结果
    void showReadReportResult(boolean isSubmit);

    /************广告点击逻辑***********/
    //点击广告结果
    void showClickAdResult(boolean isSuccess,String showMsg);
}
