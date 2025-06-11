package com.iyuba.talkshow.lil.junior.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.databinding.FragmentJuniorBinding;
import com.iyuba.talkshow.event.SelectBookEvent;
import com.iyuba.talkshow.event.WordStepEvent;
import com.iyuba.talkshow.lil.help_fix.ui.main.ui.BaseVBFragment;
import com.iyuba.talkshow.lil.help_fix.ui.main.ui.moc.MocShowActivity;
import com.iyuba.talkshow.lil.junior.choose.JuniorChooseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @title: 中小学主界面
 * @date: 2023/7/13 18:44
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorFragment extends BaseVBFragment<FragmentJuniorBinding> {

    @Inject
    ConfigManager configManager;

    private JuniorListFragment listFragment;
    private JuniorWordFragment wordFragment;

    //显示数据
    private List<Pair<String, Fragment>> pairList;

    public static JuniorFragment getInstance(){
        JuniorFragment fragment = new JuniorFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        fragmentComponent().inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initFragment();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**************************初始化***********************/
    private void initToolbar(){
        String courseTitle = configManager.getCourseTitle();
        if (TextUtils.isEmpty(courseTitle)){
            courseTitle = App.getBookDefaultShowData().getBookName();
        }

        binding.title.setText(courseTitle);
        binding.sync.setVisibility(View.GONE);
        binding.sync.setOnClickListener(v->{
            int curTabPosition = binding.tabLayout.getSelectedTabPosition();
            if (curTabPosition==0){
                if (!App.APP_MOC_BOTTOM){
                    MocShowActivity.start(getActivity());
                }
            }else {
                syncWord();
            }
        });
        binding.search.setVisibility(View.GONE);
        binding.search.setImageResource(R.mipmap.ic_search_white);
        binding.search.setOnClickListener(v->{
            jumpSearch();
        });
        binding.choose.setVisibility(View.VISIBLE);
        binding.choose.setVisibility(View.VISIBLE);
        binding.choose.setImageResource(R.mipmap.ic_course);
        binding.choose.setOnClickListener(v->{
            chooseBook();
        });
    }

    private void initFragment(){
        //这里将两个数据都添加进来，但是只显示一个
        pairList = new ArrayList<>();
        listFragment = JuniorListFragment.getInstance();
        wordFragment = JuniorWordFragment.getInstance();
        pairList.add(new Pair<>("课程",listFragment));
        pairList.add(new Pair<>("单词",wordFragment));

        showFragment(false,pairList,0);

        for (int i = 0; i < pairList.size(); i++) {
            String showText = pairList.get(i).first;

            TabLayout.Tab tab = binding.tabLayout.newTab();
            tab.setText(showText);
            binding.tabLayout.addTab(tab);
        }
        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLayout.setTabIndicatorFullWidth(true);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showFragment(true,pairList,tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //如果只有一个，则不用显示
        if (pairList.size()<=1){
            binding.tabLayout.setVisibility(View.GONE);
        }else {
            binding.tabLayout.setVisibility(View.VISIBLE);
        }
    }

    /*************************************刷新界面****************************/
    //显示界面
    private void showFragment(boolean isAdd,List<Pair<String,Fragment>> list,int position){
        showTitleAndAbility(position);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
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

    //刷新标题和功能
    private void showTitleAndAbility(int position){
        switch (position){
            case 0:
                binding.search.setVisibility(View.VISIBLE);
                binding.sync.setVisibility(View.GONE);

                if(App.APP_MOC_BOTTOM){
                    binding.sync.setVisibility(View.GONE);
                }else {
                    binding.sync.setVisibility(View.VISIBLE);
                    binding.sync.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.ic_top_moc),null,null);
                    binding.sync.setText("微课");
                    binding.sync.setTextSize(12);
                }
                break;
            case 1:
                binding.search.setVisibility(View.GONE);
                binding.sync.setVisibility(View.VISIBLE);

                binding.sync.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_changebook),null,null);
                binding.sync.setText("同步闯关数据");
                binding.sync.setTextSize(12);
                break;
        }
    }

    /***********************辅助功能*********************/
    //跳转查询功能
    private void jumpSearch(){
        if (listFragment!=null){
            listFragment.jumpSearch();
        }
    }

    //单词同步功能
    private void syncWord(){
        if (wordFragment!=null){
            wordFragment.syncExamWord();
        }
    }

    //选择书籍
    private void chooseBook(){
        JuniorChooseActivity.start(getActivity());
    }

    /*****************************回调**************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SelectBookEvent event){
        binding.title.setText(configManager.getCourseTitle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WordStepEvent event){
        binding.tabLayout.getTabAt(1).select();
        showFragment(true,pairList,1);
    }
}
