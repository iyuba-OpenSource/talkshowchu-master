package com.iyuba.talkshow.lil.help_mvp.mvp;

/**
 * @desction:
 * @date: 2023/3/15 17:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public interface IPresenter<V extends BaseView> {

    void attachView(V v);

    void detachView();
}
