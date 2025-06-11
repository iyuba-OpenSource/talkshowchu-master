package com.iyuba.talkshow.ui.detail.introduction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.talkshow.databinding.FragmentIntroductionBinding;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.umeng.analytics.MobclickAgent;



public class IntroductionFragment extends BaseFragment {
    private static final String TAG = IntroductionFragment.class.getSimpleName();

    private static final String INTRODUCTION = "introduction";

    FragmentIntroductionBinding binding ;

    public static IntroductionFragment newInstance(String introduction) {
        IntroductionFragment fragment = new IntroductionFragment();
        Bundle args = new Bundle();
        args.putString(INTRODUCTION, introduction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIntroductionBinding.inflate(inflater,container,false);
        if (getArguments() != null) {
            String introduction = getArguments().getString(INTRODUCTION);
            binding.introduction.setText(introduction);
        }
        return  binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }
}
