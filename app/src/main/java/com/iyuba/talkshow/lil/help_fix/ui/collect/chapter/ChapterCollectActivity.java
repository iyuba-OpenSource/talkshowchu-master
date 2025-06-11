package com.iyuba.talkshow.lil.help_fix.ui.collect.chapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.databinding.LayoutViewpager2TabTitleBinding;
import com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.junior.JuniorChapterCollectFragment;
import com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.novel.NovelChapterCollectFragment;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;
import com.iyuba.talkshow.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 章节收藏界面
 * @date: 2023/7/17 16:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ChapterCollectActivity extends BaseActivity {

    private JuniorChapterCollectFragment juniorFragment;
    private NovelChapterCollectFragment novelFragment;

    private ChapterCollectFragmentAdapter fragmentAdapter;

    //布局样式
    private LayoutViewpager2TabTitleBinding binding;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,ChapterCollectActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = LayoutViewpager2TabTitleBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*************************初始化*********************/
    private void initToolbar(){
        binding.toolbar.tvTopCenter.setText("收藏文章");
        binding.toolbar.imgTopLeft.setImageResource(R.mipmap.img_back);
        binding.toolbar.imgTopLeft.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
    }

    private void initList(){
        List<Pair<String, Fragment>> pairList = new ArrayList<>();
        juniorFragment = JuniorChapterCollectFragment.getInstance();
        pairList.add(new Pair<>("初中",juniorFragment));
        if (!AbilityControlManager.getInstance().isLimitNovel()){
            novelFragment = NovelChapterCollectFragment.getInstance();
            pairList.add(new Pair<>("故事",novelFragment));
        }

        fragmentAdapter = new ChapterCollectFragmentAdapter(this,pairList);
        binding.viewPager2.setAdapter(fragmentAdapter);

        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLayout.setTabIndicatorFullWidth(true);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(pairList.get(position).first);
            }
        }).attach();

        //如果只有一个，则不用显示
        if (pairList.size()<=1){
            binding.tabLayout.setVisibility(View.GONE);
        }else {
            binding.tabLayout.setVisibility(View.VISIBLE);
        }
    }

    /************************辅助功能*********************/
    private void showFragment(boolean isAdd,List<Pair<String,Fragment>> list,int position){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < list.size(); i++) {
            Fragment fragment = list.get(i).second;
            if (!isAdd){
                transaction.add(R.id.container,fragment);
            }
            transaction.hide(fragment);
        }
        Fragment fragment = list.get(position).second;
        transaction.show(fragment).commitNowAllowingStateLoss();
    }
}
