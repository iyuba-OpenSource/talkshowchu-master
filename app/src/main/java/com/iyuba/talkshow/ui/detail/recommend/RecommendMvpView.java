package com.iyuba.talkshow.ui.detail.recommend;

import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

public interface RecommendMvpView extends MvpView {
    void showRecommend(List<Voa> voaList);

    void showEmptyRecommend();

    void showToast(int resId);
}
