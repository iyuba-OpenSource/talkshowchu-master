package com.iyuba.talkshow.lil.help_mvp.mvp;

/**
 * @desction: mvp中的p
 * @date: 2023/3/15 15:56
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class BasePresenter<V extends BaseView> implements IPresenter<V>{

    private V view;

    //获取视图
    public V getMvpView(){
        return view;
    }

    //绑定
    @Override
    public void attachView(V v) {
        this.view = v;
    }

    //解绑
    @Override
    public void detachView() {
        this.view = null;
    }

    //是否绑定
    public boolean isViewAttach(){
        return view!=null;
    }

    //检查绑定状态
    public void checkViewAttach(){
        if (!isViewAttach()){
            throw new ViewNotAttachException();
        }
    }

    //绑定异常
    public static class ViewNotAttachException extends RuntimeException{

        public ViewNotAttachException(){
            super("please attach view before request data to presenter !");
        }
    }
}
