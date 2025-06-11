package com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.template;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 广告显示的模型-信息流广告
 */
public class AdTemplateViewBean {

    //默认视图
    private int layoutId;//布局
    private int templateContainerId;//模版容器的id
    private int nativeContainerId;//容器的id
    private int nativeImageId;//图片的id
    private int nativeTitleId;//标题的id

    private RecyclerView recyclerView;//控件
    private RecyclerView.Adapter listAdapter;//数据的列表适配器
    private OnAdTemplateShowListener onAdTemplateShowListener;//广告回调

    public AdTemplateViewBean(int layoutId, int templateContainerId, int nativeContainerId, int nativeImageId, int nativeTitleId, RecyclerView recyclerView, RecyclerView.Adapter listAdapter, OnAdTemplateShowListener onAdTemplateShowListener) {
        this.layoutId = layoutId;
        this.templateContainerId = templateContainerId;
        this.nativeContainerId = nativeContainerId;
        this.nativeImageId = nativeImageId;
        this.nativeTitleId = nativeTitleId;
        this.recyclerView = recyclerView;
        this.listAdapter = listAdapter;
        this.onAdTemplateShowListener = onAdTemplateShowListener;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public int getTemplateContainerId() {
        return templateContainerId;
    }

    public int getNativeContainerId() {
        return nativeContainerId;
    }

    public int getNativeImageId() {
        return nativeImageId;
    }

    public int getNativeTitleId() {
        return nativeTitleId;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public RecyclerView.Adapter getListAdapter() {
        return listAdapter;
    }

    public OnAdTemplateShowListener getOnAdTemplateShowListener() {
        return onAdTemplateShowListener;
    }
}
