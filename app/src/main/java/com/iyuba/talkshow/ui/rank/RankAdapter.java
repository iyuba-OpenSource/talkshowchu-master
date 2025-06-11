package com.iyuba.talkshow.ui.rank;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * @desction:
 * @date: 2023/2/9 15:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class RankAdapter extends FragmentStateAdapter {

    private List<Fragment> list;

    public RankAdapter(@NonNull FragmentActivity fragmentActivity,List<Fragment> list) {
        super(fragmentActivity);
        this.list = list;
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
}
