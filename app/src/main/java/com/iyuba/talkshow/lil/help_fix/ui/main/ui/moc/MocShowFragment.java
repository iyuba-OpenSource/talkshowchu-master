package com.iyuba.talkshow.lil.help_fix.ui.main.ui.moc;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.imooclib.IMooc;
import com.iyuba.imooclib.ui.mobclass.MobClassFragment;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.AdTestKeyData;
import com.iyuba.talkshow.databinding.LayoutContainerBinding;
import com.iyuba.talkshow.event.VIpChangeEvent;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_fix.ui.main.ui.BaseVBFragment;
import com.iyuba.talkshow.lil.help_fix.util.ScreenUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * @title: 微课展示界面
 * @date: 2023/7/13 17:02
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 *
 * 类型如下：
 * -2 --> 全部
 * -1 --> 最新
 * 2 -- > 四级
 * 3 -- > VOA
 * 4 -- > 六级
 * 7 -- > 托福
 * 8 -- > 考研
 * 9 -- > BBC
 * 21 -- > 新概念
 * 22 -- > 走遍美国
 * 28 -- > 学位
 * 52 -- > 考研二
 * 61 -- > 雅思
 * 91 -- > 中职
 * 24 -- > 初中
 * 25 -- > 小学
 */
public class MocShowFragment extends BaseVBFragment<LayoutContainerBinding> {

    public static MocShowFragment getInstance(){
        MocShowFragment fragment = new MocShowFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showMoc();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void showMoc(){
        //设置广告
        IMooc.setAdAppId(String.valueOf(AdShowUtil.NetParam.getAdId()));
        IMooc.setStreamAdPosition(AdShowUtil.NetParam.SteamAd_startIndex,AdShowUtil.NetParam.SteamAd_intervalIndex);
        IMooc.setYoudaoId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IMooc.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,AdTestKeyData.KeyData.TemplateAdKey.template_vlion);
        //设置广告自适应
        int adWidth = ScreenUtil.getScreenW(getActivity());
        IMooc.setYdsdkTemplateAdWidthHeight(adWidth,0);
        //设置显示内容
        ArrayList<Integer> typeIdFilter = new ArrayList<>();
        typeIdFilter.add(Constant.PRODUCT_ID);//小学
        typeIdFilter.add(3);//VOA
        typeIdFilter.add(21);//新概念

        Bundle args = MobClassFragment.buildArguments(Constant.PRODUCT_ID, false, typeIdFilter);
        MobClassFragment mMocFragment = MobClassFragment.newInstance(args);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.container,mMocFragment).show(mMocFragment).commitNowAllowingStateLoss();
    }
}
