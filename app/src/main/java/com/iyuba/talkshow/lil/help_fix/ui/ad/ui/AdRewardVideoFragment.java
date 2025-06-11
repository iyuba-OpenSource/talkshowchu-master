package com.iyuba.talkshow.lil.help_fix.ui.ad.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.talkshow.databinding.FragmentAdRewardVideoBinding;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.reward.AdRewardShowManager;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.reward.AdRewardViewBean;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.upload.AdUploadManager;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 激励视频广告界面
 */
public class AdRewardVideoFragment extends BaseViewBindingFragment<FragmentAdRewardVideoBinding> {

    public static AdRewardVideoFragment getInstance(){
        AdRewardVideoFragment fragment = new AdRewardVideoFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //关闭广告
        AdRewardShowManager.getInstance().stopRewardVideoAd();
    }

    //初始化样式
    private void initView(){
        binding.title.setVisibility(View.GONE);
        binding.adRewardVideoCsjLayout.setVisibility(View.VISIBLE);
        binding.adRewardVideoCsjType.setVisibility(View.GONE);
        binding.adRewardVideoCsjMsg.setVisibility(View.GONE);
        binding.adRewardVideoCsjLayout.setOnClickListener(v->{
            showRewardVideoAd();
        });
    }

    /********************************新的激励视频广告*****************************/
    //是否已经获取奖励
    private boolean isGetReward = false;
    //激励视频广告模型
    private AdRewardViewBean viewBean = null;
    //展示激励视频广告
    private void showRewardVideoAd(){
        if (viewBean==null){
            viewBean = new AdRewardViewBean(new AdRewardShowManager.OnAdRewardShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String adType) {
                    isGetReward = false;
                }

                @Override
                public void onAdClick(String adType, boolean isJumpByUserClick, String jumpUrl,boolean isFinishReward) {
                    if (isFinishReward){
                        if (!isGetReward){
                            isGetReward = true;
                            //提交获取奖励信息
                            String showAdPosition = AdShowUtil.NetParam.AdShowPosition.show_rewardVideo;
                            String showAdType = adType;
                            AdUploadManager.getInstance().clickAdForReward(showAdPosition, showAdType, new AdUploadManager.OnAdClickCallBackListener() {
                                @Override
                                public void showClickAdResult(boolean isSuccess, String showMsg) {
                                    ToastUtil.showToast(getActivity(),showMsg);

                                    if (isSuccess){
                                        EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.userInfo));
                                    }
                                }
                            });
                        }
                    }else {
                        ToastUtil.show(getActivity(),"请等待视频倒计时结束后点击广告");
                    }
                }

                @Override
                public void onAdClose(String adType) {

                }

                @Override
                public void onAdError(String adType) {

                }
            });
            AdRewardShowManager.getInstance().setShowData(getActivity(),viewBean);
        }
        AdRewardShowManager.getInstance().showRewardVideoAd();
    }
}
