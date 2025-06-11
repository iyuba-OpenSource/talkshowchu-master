package com.iyuba.talkshow.lil.help_fix.ui.ad.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.talkshow.databinding.FragmentAdEmptyBinding;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.ui.ad.util.show.AdShowUtil;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;

/**
 * 广告-未知界面
 */
public class AdEmptyFragment extends BaseViewBindingFragment<FragmentAdEmptyBinding> {

    public static AdEmptyFragment getInstance(String showType){
        AdEmptyFragment fragment = new AdEmptyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.type,showType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //展示信息
    private void initData(){
        String showType = getArguments().getString(StrLibrary.type);
        binding.emptyView.setText(AdShowUtil.Util.showAdName(showType) +" 类型的广告暂时未处理");
    }
}
