package com.iyuba.talkshow.lil.help_fix.ui.ad.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.LayoutContainerTabTitleBinding;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingActivity;

/**
 * 广告的容器界面
 */
public class AdContainerActivity extends BaseViewBindingActivity<LayoutContainerTabTitleBinding> {

    /**
     * @param context
     * @param adType 广告类型，数据来自{@link AdShowUtil.NetParam 的AdType数据}
     */
    public static void start(Context context,String adType){
        Intent intent = new Intent(context, AdContainerActivity.class);
        intent.putExtra(StrLibrary.type,adType);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initToolbar();
        showFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //初始化标题
    private void initToolbar(){
        binding.tabLayout.setVisibility(View.GONE);

        binding.toolbar.imgTopLeft.setImageResource(R.drawable.back);
        binding.toolbar.imgTopLeft.setOnClickListener(v->{
            finish();
        });

        String adPageType = getIntent().getStringExtra(StrLibrary.type);
        String showAdTitle = "";
        if (TextUtils.isEmpty(adPageType)){
            showAdTitle = "未知类型";
        }else {
            switch (adPageType){
                case AdShowUtil.NetParam.AdShowPosition.show_spread:
                    //开屏广告
                    break;
                case AdShowUtil.NetParam.AdShowPosition.show_template:
                    //信息流广告
                    break;
                case AdShowUtil.NetParam.AdShowPosition.show_banner:
                    //banner广告
                    break;
                case AdShowUtil.NetParam.AdShowPosition.show_interstitial:
                    //插屏广告
                    break;
                case AdShowUtil.NetParam.AdShowPosition.show_rewardVideo:
                    //激励广告
                    showAdTitle = "激励视频";
                    break;
                default:
                    showAdTitle = "未知类型";
                    break;
            }
        }
        binding.toolbar.tvTopCenter.setText(showAdTitle);
    }

    //显示界面
    private void showFragment(){
        String adPageType = getIntent().getStringExtra(StrLibrary.type);
        Fragment showFragment = null;

        if (TextUtils.isEmpty(adPageType)){
            showFragment = AdEmptyFragment.getInstance(adPageType);
        }else {
            switch (adPageType){
                case AdShowUtil.NetParam.AdShowPosition.show_spread:
                    //开屏广告
                    break;
                case AdShowUtil.NetParam.AdShowPosition.show_template:
                    //信息流广告
                    break;
                case AdShowUtil.NetParam.AdShowPosition.show_banner:
                    //banner广告
                    break;
                case AdShowUtil.NetParam.AdShowPosition.show_interstitial:
                    //插屏广告
                    break;
                case AdShowUtil.NetParam.AdShowPosition.show_rewardVideo:
                    //激励广告
                    showFragment = AdRewardVideoFragment.getInstance();
                    break;
                default:
                    showFragment = AdEmptyFragment.getInstance(adPageType);
                    break;
            }
        }

        //设置显示
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,showFragment).show(showFragment).commitNowAllowingStateLoss();
    }
}
