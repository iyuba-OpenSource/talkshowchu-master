package com.iyuba.talkshow.lil.help_fix.ui.main.ui.video;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.data.model.AdInfoTypes;
import com.iyuba.headlinelibrary.event.HeadlineGoVIPEvent;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew;
import com.iyuba.headlinelibrary.ui.title.HolderType;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.sdk.data.iyu.IyuAdClickEvent;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.AdTestKeyData;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.databinding.LayoutContainerBinding;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.ui.main.ui.BaseVBFragment;
import com.iyuba.talkshow.lil.help_fix.util.ScreenUtil;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.web.WebActivity;
import com.iyuba.talkshow.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @title: 视频展示界面
 * @date: 2023/7/13 18:10
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class VideoShowFragment extends BaseVBFragment<LayoutContainerBinding> {

    private static final String showBack = "showBack";
    private static final String showTitleBar = "showTitleBar";

    public static VideoShowFragment getInstance(){
        VideoShowFragment fragment = new VideoShowFragment();
        return fragment;
    }

    public static VideoShowFragment getInstance(boolean isShowBack,boolean isShowTitleBar){
        VideoShowFragment fragment = new VideoShowFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(showBack,isShowBack);
        bundle.putBoolean(showTitleBar,isShowTitleBar);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void showVideo(){
        //这里注意修改共通模块的url
        IHeadline.resetMseUrl();
        String extraUrl = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/", "") + ":9001/test/ai/";
        IHeadline.setExtraMseUrl(extraUrl);
        String extraMergeUrl = "http://iuserspeech." + Constant.Web.WEB_SUFFIX.replace("/", "") + ":9001/test/merge/";
        IHeadline.setExtraMergeAudioUrl(extraMergeUrl);
        //设置app的信息
        IHeadlineManager.appId = String.valueOf(App.APP_ID);
        IHeadlineManager.appName = App.APP_NAME_EN;
        //设置显示的广告
        IHeadline.setAdAppId(String.valueOf(AdShowUtil.NetParam.getAdId()));
        IHeadline.setStreamAdPosition(AdShowUtil.NetParam.SteamAd_startIndex,AdShowUtil.NetParam.SteamAd_intervalIndex);
        IHeadline.setYoudaoStreamId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IHeadline.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,AdTestKeyData.KeyData.TemplateAdKey.template_vlion);
        //设置广告自适应
        int adWidth = ScreenUtil.getScreenW(getActivity());
        IHeadline.setYdsdkTemplateAdWidthHeight(adWidth,0);

        String[] types = new String[]{
                HeadlineType.SMALLVIDEO
        };

        //是否显示按钮
        boolean isShowBack = getArguments().getBoolean(showBack,false);
        //是否显示toolbar
        boolean isShowTitleBar = getArguments().getBoolean(showTitleBar,true);

        Bundle videoBundle = null;
        if (isShowTitleBar){
            videoBundle = DropdownTitleFragmentNew.buildArguments(10,types,isShowBack);
        }else {
            videoBundle = DropdownTitleFragmentNew.buildArguments(10,types,HolderType.LARGE,isShowBack,"",false,0L);
        }
        DropdownTitleFragmentNew videoFragment = DropdownTitleFragmentNew.newInstance(videoBundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.container,videoFragment).show(videoFragment).commitNowAllowingStateLoss();
    }


    /**
     * 视频模块下载后点击
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceive(DLItemEvent dlEvent) {
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);

        switch (dlPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(getActivity(),
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivity.getIntent2Me(getActivity(),
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
        }
    }

    /**
     * 获取视频模块“现在升级的点击”
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HeadlineGoVIPEvent headlineGoVIPEvent) {
        Intent intent = new Intent(mContext, NewVipCenterActivity.class);
        startActivity(intent);
    }

    /**
     * 处理广告点击事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IyuAdClickEvent event){
        switch (event.info.type){
            case AdInfoTypes.WEB:
                if (TextUtils.isEmpty(event.info.linkUrl)){
                    ToastUtil.showToast(getActivity(),"暂无数据");
                    return;
                }

                Intent intent = WebActivity.buildIntent(getActivity(),event.info.linkUrl,event.info.title);
                startActivity(intent);
                break;
        }
    }
}
