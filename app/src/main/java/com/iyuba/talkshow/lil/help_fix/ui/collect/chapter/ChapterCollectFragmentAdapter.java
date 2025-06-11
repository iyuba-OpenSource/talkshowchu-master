package com.iyuba.talkshow.lil.help_fix.ui.collect.chapter;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * @title:
 * @date: 2023/7/18 16:22
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ChapterCollectFragmentAdapter extends FragmentStateAdapter {

    private List<Pair<String,Fragment>> pairList;

    public ChapterCollectFragmentAdapter(@NonNull FragmentActivity fragmentActivity,List<Pair<String,Fragment>> list) {
        super(fragmentActivity);
        this.pairList = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return pairList.get(position).second;
    }

    @Override
    public int getItemCount() {
        return pairList==null?0:pairList.size();
    }
}
