package com.iyuba.talkshow.lil.help_fix.ui.study.eval;

import com.iyuba.talkshow.lil.help_fix.data.bean.EvalChapterBean;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Publish_eval;
import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;

/**
 * @title:
 * @date: 2023/5/24 00:05
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface EvalView extends BaseView {

    //展示单个评测数据
    void showSingleEval(EvalChapterBean bean);

    //展示合成音频数据
    void showMargeAudio(String margeAudioUrl);

    //展示提交排行榜数据
    void showPublishRank(boolean isSingle, Publish_eval bean);
}
