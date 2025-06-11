package com.iyuba.talkshow.lil.help_fix.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @title:  适用于viewpager2的适配器
 * @date: 2023/4/27 13:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ViewPager2FragmentAdapter extends FragmentStateAdapter {

    private List<Fragment> list;

    public ViewPager2FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.list = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    //刷新数据
    public void refreshList(List<Fragment> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }
}
