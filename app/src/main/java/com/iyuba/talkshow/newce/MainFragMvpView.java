package com.iyuba.talkshow.newce;

import android.content.DialogInterface.OnClickListener;

import com.iyuba.talkshow.data.model.CategoryFooter;
import com.iyuba.talkshow.data.model.LoopItem;
import com.iyuba.talkshow.data.model.TitleSeries;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.newview.LoginResult;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/12 0012.
 */

public interface MainFragMvpView extends MvpView {
    void showVoas(List<Voa> voas);

    void showTitleSeries(List<TitleSeries> voas);

    void showVoasByCategory(List<Voa> voas, CategoryFooter categoroy);

    void showVoasEmpty();

    void showError();

    void setBanner(List<LoopItem> loopItemList);

    void showToast(String text);

    void showToast(int resId);

    void startDetailActivity(Voa voa, String jumpTitle, int unit,int positionInList);

    void dismissRefreshingView();

    void showAlertDialog(String msg, OnClickListener listener);

    void startAboutActivity(String versionCode, String appUrl);

    void showMoreVoas(List<Voa> voas);

    void showLoadingDialog();

    void dismissLoadingDialog();

    //刷新网络数据操作
    void refreshNetVoaData();
}
