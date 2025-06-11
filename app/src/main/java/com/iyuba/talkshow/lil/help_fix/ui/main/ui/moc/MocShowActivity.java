package com.iyuba.talkshow.lil.help_fix.ui.main.ui.moc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.LayoutContainerTabTitleBinding;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingActivity;

/**
 * @title:
 * @date: 2023/7/14 10:05
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class MocShowActivity extends BaseViewBindingActivity<LayoutContainerTabTitleBinding> {

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,MocShowActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        binding.tabLayout.setVisibility(View.GONE);
        binding.toolbar.reTopLeft.setVisibility(View.VISIBLE);
        binding.toolbar.reTopRight.setVisibility(View.INVISIBLE);

        binding.toolbar.tvTopCenter.setText("微课");
        binding.toolbar.imgTopLeft.setImageResource(R.drawable.back);
        binding.toolbar.imgTopLeft.setOnClickListener(v->{
            finish();
        });
    }

    private void initFragment(){
        MocShowFragment fragment = MocShowFragment.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,fragment).show(fragment).commitNowAllowingStateLoss();
    }
}
