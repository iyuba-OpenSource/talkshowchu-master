package com.iyuba.talkshow.newce.study;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 学习界面的适配器(这里进行了替换操作)
 */
public class StudyNewAdapter2 extends FragmentStatePagerAdapter {

    private List<Fragment> Fraglist;
    private String[] category_dian;

    public StudyNewAdapter2(@NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm);

        this.Fraglist = new ArrayList<>();
        this.Fraglist = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return Fraglist.get(position);
    }

    @Override
    public int getCount() {
        return Fraglist==null?0:Fraglist.size();
    }

    public void setPageTitle(String[] category) {
        category_dian = category;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (category_dian == null) {
            return Fraglist.get(position).getTag();
        }
        return category_dian[position];
    }

    //获取标题信息
    public String[] getTitle(){
        return category_dian;
    }

    //刷新list和title
    public void refreshListAndTitle(List<Fragment> fragments,String[] titles){
        this.Fraglist = fragments;
        this.category_dian = titles;

        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
