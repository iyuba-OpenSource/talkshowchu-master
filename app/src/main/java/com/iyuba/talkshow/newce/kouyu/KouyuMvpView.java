package com.iyuba.talkshow.newce.kouyu;

import com.iyuba.talkshow.data.model.CategoryFooter;
import com.iyuba.talkshow.data.model.LoopItem;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.newview.LoginResult;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/12 0012.
 */

public interface KouyuMvpView extends MvpView {
    void showVoas(List<Voa> voas);

    void showVoasByCategory(List<Voa> voas, CategoryFooter categoroy);

    void showVoasEmpty();

    void showError();

    void setBanner(List<LoopItem> loopItemList);

    void showToast(String text);

    void showToast(int resId);

    void startDetailActivity(Voa voa);

    void dismissRefreshingView();

    void showMoreVoas(List<Voa> voas);

    void showLoadingDialog();

    void dismissLoadingDialog();

}
