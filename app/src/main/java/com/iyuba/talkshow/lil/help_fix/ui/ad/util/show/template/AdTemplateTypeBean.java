package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template;

import com.iyuba.sdk.nativeads.NativeRecyclerAdapter;

import io.reactivex.disposables.Disposable;

/**
 * 信息流广告-根据类型处理
 */
public class AdTemplateTypeBean {

    //控件数据
    private AdTemplateViewBean viewBean;
    //广告适配器
    private NativeRecyclerAdapter adAdapter;
    //网络数据
    private Disposable adDisposable;

    public AdTemplateTypeBean(AdTemplateViewBean viewBean, NativeRecyclerAdapter adAdapter, Disposable adDisposable) {
        this.viewBean = viewBean;
        this.adAdapter = adAdapter;
        this.adDisposable = adDisposable;
    }

    public AdTemplateViewBean getViewBean() {
        return viewBean;
    }

    public void setViewBean(AdTemplateViewBean viewBean) {
        this.viewBean = viewBean;
    }

    public NativeRecyclerAdapter getAdAdapter() {
        return adAdapter;
    }

    public void setAdAdapter(NativeRecyclerAdapter adAdapter) {
        this.adAdapter = adAdapter;
    }

    public Disposable getAdDisposable() {
        return adDisposable;
    }

    public void setAdDisposable(Disposable adDisposable) {
        this.adDisposable = adDisposable;
    }
}
