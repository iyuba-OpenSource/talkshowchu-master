package com.iyuba.talkshow.ui.user.download;

import android.content.Context;

import com.iyuba.talkshow.data.model.Download;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/12/27/027.
 */

public interface DownloadMvpView extends MvpView {

    void showLoadingLayout();

    void dismissLoadingLayout();

    void setAdapterEmpty();

    void setAdapterData(List<Download> data);

    void showToast(int resId);

    Context getContextt();
}
