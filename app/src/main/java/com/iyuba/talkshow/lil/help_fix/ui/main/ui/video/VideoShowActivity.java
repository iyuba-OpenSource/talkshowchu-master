package com.iyuba.talkshow.lil.help_fix.ui.main.ui.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.favor.ui.BasicFavorActivity;
import com.iyuba.module.headlinetalk.ui.mytalk.MyTalkActivity;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ActivityVideoShowBinding;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 视频展示界面
 * @date: 2023/9/4 16:57
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class VideoShowActivity extends BaseActivity {

    //布局样式
    private ActivityVideoShowBinding binding;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,VideoShowActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVideoShowBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initToolbar(){
        binding.toolbar.getRoot().setVisibility(View.VISIBLE);
        binding.toolbar.imgTopLeft.setImageResource(R.drawable.back);
        binding.toolbar.reTopLeft.setOnClickListener(v->{
            finish();
        });
        binding.toolbar.tvTopCenter.setText("小视频");

        //设置按钮显示
        binding.videoCollectLayout.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()) {
                NewLoginUtil.startToLogin(this);
                return;
            }

            List<String> types = new ArrayList<>();
            types.add(HeadlineType.SMALLVIDEO);
            BasicFavor.setTypeFilter(types);
            startActivity(BasicFavorActivity.buildIntent(this));
        });
        binding.videoDubbingLayout.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()) {
                NewLoginUtil.startToLogin(this);
                return;
            }

            ArrayList<String> types = new ArrayList<>();
            types.add(HeadlineType.SMALLVIDEO);
            startActivity(MyTalkActivity.buildIntent(this, types));
        });
    }

    private void initFragment(){
        VideoShowFragment showFragment = VideoShowFragment.getInstance(true,false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,showFragment);
        transaction.show(showFragment);
        transaction.commitNowAllowingStateLoss();
    }
}
