package com.iyuba.talkshow.ui.user.collect;

import com.iyuba.talkshow.data.model.Collect;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/12/27/027.
 */

public interface CollectionMvpView extends MvpView {

    void showLoadingLayout();

    void dismissLoadingLayout();

    void setAdapterEmpty();

    void setAdapterData(String msg,List<Collect> data);

    void showToast(int resId);

}
