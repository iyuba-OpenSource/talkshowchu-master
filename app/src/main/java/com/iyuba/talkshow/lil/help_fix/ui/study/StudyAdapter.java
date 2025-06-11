package com.iyuba.talkshow.lil.help_fix.ui.study;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/22 16:00
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyAdapter extends FragmentStateAdapter {

    private List<Fragment> list;

    public StudyAdapter(@NonNull FragmentActivity fragmentActivity,List<Fragment> list) {
        super(fragmentActivity);
        this.list = list;
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    //刷新数据
    public void refreshData(List<Fragment> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }
}
